package com.arielsoares.ecommercesimplificado.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.arielsoares.ecommercesimplificado.controllers.DTO.OrderDTOWithoutClient;
import com.arielsoares.ecommercesimplificado.entities.Order;
import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.Product;
import com.arielsoares.ecommercesimplificado.entities.User;
import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;
import com.arielsoares.ecommercesimplificado.exception.InvalidArgumentException;
import com.arielsoares.ecommercesimplificado.exception.ResourceNotFoundException;
import com.arielsoares.ecommercesimplificado.repositories.OrderRepository;
import com.arielsoares.ecommercesimplificado.services.mail.EmailService;

@Service
public class OrderService {

	private OrderRepository repository;
	private OrderItemService orderItemService;
	private ProductService productService;
	private UserService userService;
	private EmailService mailService;

	public OrderService(OrderRepository repository, OrderItemService orderItemService, ProductService productService,
			UserService userService, EmailService mailService) {
		this.repository = repository;
		this.orderItemService = orderItemService;
		this.productService = productService;
		this.userService = userService;
		this.mailService = mailService;
	}

	@Cacheable(value = "orders")
	public List<Order> findAll() {
		return repository.findAll();
	}

	public Order findById(Long orderId) {
		return repository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found with id " + orderId));
	}

	public List<Order> findByClientId(Long clientId) {
		return repository.findByClientId(clientId);
	}

	public List<OrderDTOWithoutClient> findByClient(Long clientId) {
		List<Order> orders = repository.findByClientId(clientId);
		List<OrderDTOWithoutClient> orderDTOS = new ArrayList<>();

		for (Order o : orders) {
			OrderDTOWithoutClient orderDTO = new OrderDTOWithoutClient(o.getId(), o.getMoment(), o.getItems(),
					o.getStatus());
			orderDTOS.add(orderDTO);
		}
		return orderDTOS;
	}

	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTOWithoutClient insert(Order Order) {

		Order newOrder = repository.save(Order);

		OrderDTOWithoutClient order = new OrderDTOWithoutClient(newOrder.getId(), newOrder.getMoment(),
				newOrder.getItems(), newOrder.getStatus());

		return order;
	}

	@CacheEvict(value = "orders", allEntries = true)
	public void delete(Long id) {
		Order order = findById(id);

		if (order.getStatus() != OrderStatus.OPEN || order.getStatus() != OrderStatus.WAITING_PAYMENT)
			throw new InvalidArgumentException("Order cannot be deleted because it is already " + order.getStatus());

		repository.deleteById(id);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public Order updateOrder(Order order) {
		return repository.save(order);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTOWithoutClient update(Long userId, Long id, String status) {

		if (!status.toUpperCase().equals("CANCELLED") && !status.toUpperCase().equals("PAID")
				&& !status.toUpperCase().equals("WAITING_PAYMENT"))
			throw new InvalidArgumentException("Only CANCELLED status, WAITING_PAYMENT or PAID status accepted ");

		List<Order> clientOrders = findByClientId(userId);
		Order obj = findById(id);
		if (!clientOrders.contains(obj))
			throw new InvalidArgumentException("Client can update only their own orders");

		if (obj.getStatus() == OrderStatus.PAID || obj.getStatus() == OrderStatus.COMPLETE
				|| obj.getStatus() == OrderStatus.CANCELLED)
			throw new InvalidArgumentException("Order is already " + obj.getStatus());

		if (status.toUpperCase().equals("PAID"))
			completeOrder(obj);

		if (status.toUpperCase().equals("WAITING_PAYMENT"))
			obj.setStatus(OrderStatus.WAITING_PAYMENT);

		if (status.toUpperCase().equals("CANCELLED"))
			obj.setStatus(OrderStatus.CANCELLED);

		obj = updateOrder(obj);
		
		OrderDTOWithoutClient orderDTO = new OrderDTOWithoutClient(obj.getId(), obj.getMoment(),
				obj.getItems(), obj.getStatus());
		
		return orderDTO;
	}

	@CacheEvict(value = {"orders", "products"}, allEntries = true)
	private void completeOrder(Order obj) {

		Integer quantity = 0;
		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			quantity += 1;
		}
		if (quantity < 1)
			throw new InvalidArgumentException("Order can only be completed if it has at least 1 active Item and Product");

		for (OrderItem oi : obj.getItems()) {
			if (!oi.getProduct().getActive() || !oi.getActive())
				continue;
			if (oi.getQuantity() > oi.getProduct().getStorage_quantity() || oi.getProduct().getStorage_quantity() == 0)
				throw new InvalidArgumentException("Insuficient Storage Quantity of this product: "
						+ oi.getProduct().getName() + " id: " + oi.getProduct().getId());
		}
		List<OrderItem> itemsToRemove = new ArrayList<>();

		for (OrderItem oi : obj.getItems()) {
		    if (!oi.getProduct().getActive() || !oi.getActive()) {
		        itemsToRemove.add(oi);
		        orderItemService.delete(oi.getId());
		    } else {
		        Product product = oi.getProduct();

		        product.setStorage_quantity(product.getStorage_quantity() - oi.getQuantity());
		        productService.update(product.getId(), product);
		    }
		}


		String email = obj.getClient().getEmail();
		
		obj.getItems().removeAll(itemsToRemove);

		obj.setStatus(OrderStatus.PAID);
		updateOrder(obj);
		
		String subject = "Order Completed";
		String body = "Thank you for buying with us, we received your payment with the value of R$" + obj.getTotal();

		mailService.sendSimpleEmail(email, subject, body);
	}

	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTOWithoutClient addOrderItem(Long userId, Long orderId, Integer quantity, Long productId) {
		Order order = findById(orderId);

		if (order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.COMPLETE
				|| order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.WAITING_PAYMENT)
			throw new InvalidArgumentException("Order is already " + order.getStatus());

		User newUser = userService.findById(userId);
		if (!newUser.getOrders().contains(order))
			throw new IllegalArgumentException("Users can only modify their own orders");
		Product product = productService.findById(productId);

		OrderItem oi = new OrderItem(product, quantity);
		for (OrderItem i : order.getItems()) {
			if (i.getProduct() == oi.getProduct() && i.getActive()) {
				i.setQuantity(i.getQuantity() + quantity);

				OrderDTOWithoutClient orderDTO = new OrderDTOWithoutClient(order.getId(), order.getMoment(),
						order.getItems(), order.getStatus());

				order = updateOrder(order);

				return orderDTO;
			}
		}

		OrderItem newOi = orderItemService.insert(oi);

		order.getItems().add(newOi);

		order = updateOrder(order);

		OrderDTOWithoutClient orderDTO2 = new OrderDTOWithoutClient(order.getId(), order.getMoment(), order.getItems(),
				order.getStatus());
		return orderDTO2;
	}

	@CacheEvict(value = "orders", allEntries = true)
	public OrderDTOWithoutClient inactiveOrderItem(Long userId, Long orderId, Long orderItemId) {

		List<Order> clientOrders = findByClientId(userId);
		Order order = findById(orderId);
		if (!clientOrders.contains(order))
			throw new InvalidArgumentException("Client can update only their own orders");
		
		OrderItem orderItem = orderItemService.findById(orderItemId);
		
		if(orderItem.getActive() == false) throw new InvalidArgumentException("Order item is already inactive");

		for (OrderItem oi : order.getItems()) {
			if (oi.getId() == orderItemId)
				oi.setActive(false);
			orderItemService.update(oi);
		}

		Order updatedOrder = findById(orderId);

		OrderDTOWithoutClient ordeDTO = new OrderDTOWithoutClient(updatedOrder.getId(), updatedOrder.getMoment(),
				updatedOrder.getItems(), updatedOrder.getStatus());

		return ordeDTO;
	}

}
