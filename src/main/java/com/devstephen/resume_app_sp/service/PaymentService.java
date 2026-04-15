package com.devstephen.resume_app_sp.service;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.PaymentResponse;
import com.devstephen.resume_app_sp.entity.Payment;
import com.devstephen.resume_app_sp.repository.PaymentRepository;
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
import org.springframework.http.MediaType;

import static com.devstephen.resume_app_sp.utils.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final WebClient webClient;
    private final PaymentRepository repository;
    private final AuthService authService;
    @Value("${paystack.secret.key}")
    private String paystackSecretKey;
    @Value("${paystack.public.key}")
    private String paystackPublicKey;

    public PaymentResponse createPayment(@Nullable Object principal, String planType) {
        AuthResponse profile = authService.getProfile(principal);

        //Amount based on planType
        int amount = planType.equalsIgnoreCase(PREMIUM) ? 50000 : 10000;
        String receipt = PREMIUM + "_" + UUID.randomUUID().toString().substring(0, 5);

        String currency = "NGN";


        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", profile.getEmail());
        requestBody.put("amount", amount);
        requestBody.put("currency", currency);

        Map response = webClient.post()
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

        // Step 3: Save payment record to DB with "Pending" status
        Payment payment = Payment.builder()
                .userId(profile.getUserId())
                .amount(amount)
                .planType(planType)
                .paystackOrderId(paystackReference.substring(0, 8))
                .currency(currency)
                .receipt(receipt)
                .status("Pending")
                .build();
        repository.save(payment);

        return PaymentResponse.builder()
                .authorizationUrl(authorizationUrl)
                .receipt(receipt)
                .amount(amount)
                .paystackOrderId(paystackReference)
                .currency(currency)
                .planType(planType)
                .status("Pending")
                .build();
    }

    public String verifyPayment(String reference) {
        Map response = webClient.get()
                .uri("/transaction/verify/" + reference)
                .header("Authorization", "Bearer " + paystackSecretKey)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        Map<String, Object> data = (Map<String, Object>) response.get("data");
        String status = (String) data.get("status");

        if ("success".equals(status)) {
            // Update payment status in DB
            Payment payment = repository.findByReceipt(reference)
                    .orElseThrow(() -> new RuntimeException("Payment not found"));
            payment.setStatus("Paid");
            repository.save(payment);
            return "Payment verified!";
        }

        return "Payment failed or pending";
    }
}
