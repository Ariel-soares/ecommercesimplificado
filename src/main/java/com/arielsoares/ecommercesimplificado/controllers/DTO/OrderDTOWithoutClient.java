package com.arielsoares.ecommercesimplificado.controllers.DTO;

import java.time.Instant;
import java.util.List;

import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;

public record OrderDTOWithoutClient(Long id, Instant moment, List<OrderItem> items, OrderStatus status) {

}
