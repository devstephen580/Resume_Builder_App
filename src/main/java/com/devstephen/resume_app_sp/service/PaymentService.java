package com.devstephen.resume_app_sp.service;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.PaymentResponse;
import com.devstephen.resume_app_sp.entity.Payment;
import com.devstephen.resume_app_sp.entity.User;
import com.devstephen.resume_app_sp.repository.PaymentRepository;
import com.devstephen.resume_app_sp.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.devstephen.resume_app_sp.utils.AppConstants.PREMIUM;
import static com.devstephen.resume_app_sp.utils.AppConstants.SUCCESS;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

  private final WebClient webClient;
  private final PaymentRepository paymentRepository;
  private final UserRepository userRepository;
  private final AuthService authService;

  @Value("${paystack.secret.key}")
  private String paystackSecretKey;

  @Value("${paystack.public.key}")
  private String paystackPublicKey;

  public PaymentResponse createPayment(@Nullable Object principal, String planType) {
    AuthResponse profile = authService.getProfile(principal);

    // Amount based on planType
    int amount = planType.equalsIgnoreCase(PREMIUM) ? 500000 : 100000;
    String receipt = PREMIUM + "_" + UUID.randomUUID().toString().substring(0, 5);

    String currency = "NGN";

    Map<String, Object> requestBody = new HashMap<>();
    requestBody.put("email", profile.getEmail());
    requestBody.put("amount", amount);
    requestBody.put("currency", currency);

    Map response =
        webClient
            .post()
            .uri("/transaction/initialize")
            .header("Authorization", "Bearer " + paystackSecretKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    // Step 2: Extract the authorization URL from Paystack's response
    Map<String, Object> data = (Map<String, Object>) response.get("data");
    if (data == null) {
      log.error("Paystack response: {}", response);
      throw new RuntimeException("Invalid response from Paystack");
    }

    String authorizationUrl = (String) data.get("authorization_url");
    String paystackReference = (String) data.get("reference");
    String paystackAccessCode = (String) data.get("access_code");
    String paystackSignature = (String) data.get("authorization_signature");

    // Step 3: Save payment record to DB with "Pending" status
    Payment payment =
        Payment.builder()
            .userId(profile.getUserId())
            .amount(amount)
            .planType(planType)
            .paystackOrderId(paystackReference)
            .paystackAccessCode(paystackAccessCode)
            .currency(currency)
            .receipt(receipt)
            .status("Pending")
            .build();
    paymentRepository.save(payment);

    return PaymentResponse.builder()
        .authorizationUrl(authorizationUrl)
        .receipt(receipt)
        .amount(amount)
        .paystackOrderId(paystackReference)
        .paystackAccessCode(paystackAccessCode)
        .currency(currency)
        .planType(planType)
        .status("Pending")
        .build();
  }

  public boolean verifyPayment(String paystackOrderId, String paystackAccessCode) {
    try {

      Map response =
          webClient
              .get()
              .uri("/transaction/verify/" + paystackOrderId)
              .header("Authorization", "Bearer " + paystackSecretKey)
              .retrieve()
              .bodyToMono(Map.class)
              .block();

      if (response != null && Boolean.TRUE.equals(response.get("status"))) {
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        String paymentStatus = (String) data.get("status");
        paystackAccessCode = (String) data.get("access_code");

        Payment payment;
        if (!SUCCESS.equalsIgnoreCase(paymentStatus)) {
          throw new RuntimeException("Payment failed");

        } else {

          // Update the payment status
          payment =
              paymentRepository
                  .findByPaystackOrderId(paystackOrderId)
                  .orElseThrow(() -> new RuntimeException("Payment not found."));

          // check before updating
          if ("paid".equals(payment.getStatus())) {
            return true; // already processed, skip
          }

          payment.setPaystackAccessCode(paystackAccessCode);
          payment.setStatus("paid");
          paymentRepository.save(payment);
        }

        // Upgrade the user subscription
        upgradeUserSubscription(payment.getUserId(), payment.getPlanType());
        return true;
      }

    } catch (Exception e) {
      log.error("Error verifying the payment for orderId {}: ", paystackOrderId, e);
      throw new RuntimeException("Payment verification error: " + e.getMessage());
    }
    return false;
  }

  private void upgradeUserSubscription(String userId, String planType) {
    log.info("User {} upgraded to {} plan", userId, planType);

    User existingUser =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
    existingUser.setSubscription(planType);
    userRepository.save(existingUser);
  }

  public List<PaymentResponse> getUserPayments(@Nullable Object principal) {

    AuthResponse profile = authService.getProfile(principal);

    List<Payment> payments =
        paymentRepository.findByUserIdOrderByCreatedAtDesc(profile.getUserId());
    return payments
        .stream()
        .map(p -> toResponse(p)).collect(Collectors.toList());
  }

  public PaymentResponse getPaymentDetails(String orderId) {
    Payment paymentDetails =
        paymentRepository
            .findByPaystackOrderId(orderId)
            .orElseThrow(() -> new RuntimeException("No payment exist with id: " + orderId));
    return toResponse(paymentDetails);
  }

  private PaymentResponse toResponse(Payment payment) {
    return PaymentResponse.builder()
        .userId(payment.getUserId())
        .paystackOrderId(payment.getPaystackOrderId())
        .status(payment.getStatus())
        .amount(payment.getAmount())
        .currency(payment.getCurrency())
        .receipt(payment.getReceipt())
        .planType(payment.getPlanType())
        .createdAt(payment.getCreatedAt())
        .build();
  }
}
