package com.streetburger.controller;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.OrderRequest;
import com.streetburger.model.MenuItem;
import com.streetburger.model.Order;
import com.streetburger.model.OrderItem;
import com.streetburger.model.User;
import com.streetburger.repository.MenuItemRepository;
import com.streetburger.repository.OrderRepository;
import com.streetburger.repository.UserRepository;
import com.streetburger.service.PushNotificationService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order management endpoints")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private PushNotificationService notificationService;

    @PostMapping("/add")
    @Operation(summary = "Place a new order")
    public ResponseEntity<ApiResponse<Order>> placeOrder(
            @AuthenticationPrincipal User currentUser,
            @RequestBody OrderRequest request) {

        if (request.getItems() == null || request.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Order must contain items"));
        }

        Order order = new Order();
        order.setUser(currentUser);
        order.setPhoneNumber(
                request.getPhoneNumber() != null ? request.getPhoneNumber() : currentUser.getPhoneNumber());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setNotes(request.getNotes());
        order.setStatus(Order.OrderStatus.PENDING);
        try {
            order.setOrderType(Order.OrderType.valueOf(request.getOrderType()));
        } catch (Exception e) {
            order.setOrderType(Order.OrderType.PICKUP);
        }

        List<OrderItem> orderItems = request.getItems().stream().map(itemRequest -> {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemRequest.getMenuItemId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setPrice(menuItem.getPrice()); // Snapshot price
            return orderItem;
        }).collect(Collectors.toList());

        order.setItems(orderItems);

        BigDecimal total = orderItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);

        Order savedOrder = orderRepository.save(order);

        // Notify Admins
        try {
            List<User> admins = userRepository.findByRole(User.Role.ADMIN);
            List<String> adminTokens = admins.stream()
                    .map(User::getPushToken)
                    .filter(token -> token != null && !token.isEmpty())
                    .collect(Collectors.toList());

            if (!adminTokens.isEmpty()) {
                Map<String, Object> data = new HashMap<>();
                data.put("orderId", savedOrder.getId());
                data.put("type", "NEW_ORDER");
                notificationService.sendPushNotification(adminTokens, "New Order Received!",
                        "Order #" + savedOrder.getId() + " - " + savedOrder.getOrderType(), data);
            }
        } catch (Exception e) {
            System.err.println("Failed to notify admins: " + e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Order placed successfully", savedOrder));
    }

    @GetMapping("/mine")
    @Operation(summary = "Get current user's order history")
    public ResponseEntity<ApiResponse<List<Order>>> getMyOrders(@AuthenticationPrincipal User currentUser) {
        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all orders (Admin)")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        List<Order> orders = orderRepository.findAllByOrderByCreatedAtDesc();
        return ResponseEntity.ok(ApiResponse.success(orders));
    }

    @PutMapping("/update-status/{id}")
    @Operation(summary = "Update order status")
    public ResponseEntity<ApiResponse<Order>> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return orderRepository.findById(id).map(order -> {
            try {
                Order.OrderStatus newStatus = Order.OrderStatus.valueOf(status);
                order.setStatus(newStatus);
                Order updatedOrder = orderRepository.save(order);

                // Notify User
                String userToken = order.getUser().getPushToken();
                if (userToken != null && !userToken.isEmpty()) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("orderId", order.getId());
                    data.put("type", "ORDER_UPDATE");
                    notificationService.sendPushNotification(
                            List.of(userToken),
                            "Order Update",
                            "Your order #" + order.getId() + " is now " + newStatus,
                            data);
                }

                return ResponseEntity.ok(ApiResponse.success("Order status updated", updatedOrder));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(ApiResponse.<Order>error("Invalid status"));
            }
        }).orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Order not found")));
    }
}
