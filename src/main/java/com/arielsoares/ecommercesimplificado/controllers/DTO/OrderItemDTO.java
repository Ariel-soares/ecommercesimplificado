package com.arielsoares.ecommercesimplificado.controllers.DTO;

import jakarta.validation.constraints.NotNull;

public record OrderItemDTO(
		
		@NotNull(message = "Item quantity cannot be null") 
		Integer quantity,
		
		@NotNull(message = "Item Id cannot be null") 
		Long productId
		) {

	
	
}
