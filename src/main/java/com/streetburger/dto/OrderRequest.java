package com.streetburger.dto;

import java.util.List;
import lombok.Data;

@Data
public class OrderRequest {
    private List<OrderItemRequest> items;
    private String orderType; // DELIVERY, PICKUP, DINE_IN
    private String deliveryAddress;
    private String phoneNumber;
    private String notes;
}
