package com.arielsoares.ecommercesimplificado.controllers.DTO;

import java.time.Instant;
import java.util.List;

import com.arielsoares.ecommercesimplificado.entities.OrderItem;
import com.arielsoares.ecommercesimplificado.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

public record OrderDTOWithoutClient(Long id, @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT") Instant moment, List<OrderItem> items, OrderStatus status) {
}