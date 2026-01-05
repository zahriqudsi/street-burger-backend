package com.streetburger.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class OrderRequest {
    private String customerName;
    private String phoneNumber;
    private List<OrderItemRequest> items;
}
