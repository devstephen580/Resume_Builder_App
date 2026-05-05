package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.PaymentResponse;
import com.devstephen.resume_app_sp.service.AuthService;
import com.devstephen.resume_app_sp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Objects;

import static com.devstephen.resume_app_sp.utils.AppConstants.PREMIUM;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;


    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, String> request, Authentication authentication) {

        String planType = request.get("planType");
        if (!PREMIUM.equalsIgnoreCase(planType)) {
            return ResponseEntity.badRequest().body(Map.of("message: ", "Invalid plan type."));
        }

        PaymentResponse payment = paymentService.createPayment(authentication.getPrincipal(), planType);

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request, Authentication authentication) {

        String paystackOrderId = request.get("paystack_order_id");
        String paystackPaymentId = request.get("paystack_payment_id");
        String paystackSignature = request.get("paystack_signature");

        if (Objects.isNull(paystackPaymentId) || Objects.isNull(paystackOrderId) || Objects.isNull(paystackSignature)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Missing required payment parameter"));

        }

        boolean isValid = paymentService.verifyPayment(paystackOrderId, paystackPaymentId, paystackSignature);
        if (isValid) {
            return ResponseEntity.ok().body(Map.of(
                    "message", "Payment successfully made",
                    "status", "success"));
        } else
            return ResponseEntity.badRequest().body(Map.of(
                    "message ", "Payment verification failed.",
                    "status", "error"));
    }

    @GetMapping("/history")
    public ResponseEntity<?> paymentHistory(Authentication authentication) {
        AuthResponse response = authService.getProfile(authentication.getPrincipal());
        return null;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId) {

        return null;
    }

}
