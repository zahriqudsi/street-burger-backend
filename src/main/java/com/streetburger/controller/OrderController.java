package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.OrderItemRequest;
import com.streetburger.dto.OrderRequest;
import com.streetburger.model.MenuItem;
import com.streetburger.model.Order;
import com.streetburger.model.OrderItem;
import com.streetburger.model.User;
import com.streetburger.repository.MenuItemRepository;
import com.streetburger.repository.OrderRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @PostMapping("/add")
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @AuthenticationPrincipal User currentUser,
            @RequestBody OrderRequest request) {

        Order order = new Order();
        order.setUser(currentUser);
        order.setCustomerName(request.getCustomerName() != null ? request.getCustomerName()
                : (currentUser != null ? currentUser.getName() : "Guest"));
        order.setPhoneNumber(request.getPhoneNumber() != null ? request.getPhoneNumber()
                : (currentUser != null ? currentUser.getPhoneNumber() : ""));
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        java.math.BigDecimal total = java.math.BigDecimal.ZERO;
        List<OrderItem> items = new ArrayList<>();

        for (OrderItemRequest itemReq : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemReq.getMenuItemId()).orElse(null);
            if (menuItem != null) {
                OrderItem item = new OrderItem();
                item.setOrder(order);
                item.setMenuItem(menuItem);
                item.setQuantity(itemReq.getQuantity());
                item.setPrice(menuItem.getPrice());
                items.add(item);
                total = total.add(menuItem.getPrice().multiply(new java.math.BigDecimal(itemReq.getQuantity())));
            }
        }

        order.setItems(items);
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", saved));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's orders")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders (Admin)")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/updateStatus/{id}")
    @Operation(summary = "Update order status (Admin)")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id,
            @RequestParam Order.OrderStatus status) {

        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    order.setUpdatedAt(LocalDateTime.now());
                    orderRepository.save(order);
                    return ResponseEntity.ok(ApiResponse.success("Order status updated", order));
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("Order not found")));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an order (Admin)")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("Order deleted", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error("Order not found"));
    }
}
