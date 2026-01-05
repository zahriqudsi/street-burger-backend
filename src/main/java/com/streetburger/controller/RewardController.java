package com.streetburger.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.streetburger.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.streetburger.dto.ApiResponse;
import com.streetburger.dto.RewardPointsRequest;
import com.streetburger.model.RewardPoints;
import com.streetburger.repository.RewardPointsRepository;
import com.streetburger.repository.UserRepository;

@RestController
@RequestMapping("/rwdpts")
@Tag(name = "Rewards", description = "Reward points management")
public class RewardController {

    @Autowired
    private RewardPointsRepository rewardPointsRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/addrwdpts")
    @Operation(summary = "Add reward points (Admin)")
    public ResponseEntity<ApiResponse<RewardPoints>> addRewardPoints(@RequestBody RewardPointsRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber()).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }

        RewardPoints rewardPoints = new RewardPoints();
        rewardPoints.setUser(user);
        rewardPoints.setPoints(request.getPoints());
        rewardPoints.setDescription(request.getDescription());

        if (request.getTransactionType() != null) {
            rewardPoints.setTransactionType(
                    RewardPoints.TransactionType.valueOf(request.getTransactionType().toUpperCase()));
        } else {
            rewardPoints.setTransactionType(RewardPoints.TransactionType.ADMIN_ADD);
        }

        rewardPoints.setCreatedAt(LocalDateTime.now());

        RewardPoints saved = rewardPointsRepository.save(rewardPoints);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reward points added", saved));
    }

    @GetMapping("/getrwdpts")
    @Operation(summary = "Get current user's reward points")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRewardPoints(
            @AuthenticationPrincipal User currentUser) {

        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("User not authenticated"));
        }

        Integer totalPoints = rewardPointsRepository.getTotalPointsByUserId(currentUser.getId());
        List<RewardPoints> history = rewardPointsRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("totalPoints", totalPoints != null ? totalPoints : 0);
        result.put("history", history);

        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @GetMapping("/getrwdpts/{phoneNumber}")
    @Operation(summary = "Get reward points by phone number")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getRewardPointsByPhone(@PathVariable String phoneNumber) {
        User user = userRepository.findByPhoneNumber(phoneNumber).orElse(null);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }

        Integer totalPoints = rewardPointsRepository.getTotalPointsByUserId(user.getId());
        List<RewardPoints> history = rewardPointsRepository.findByUserIdOrderByCreatedAtDesc(user.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("totalPoints", totalPoints != null ? totalPoints : 0);
        result.put("history", history);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
