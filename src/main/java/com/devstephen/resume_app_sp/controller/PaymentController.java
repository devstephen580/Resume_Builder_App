package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.PaymentResponse;
import com.devstephen.resume_app_sp.entity.Payment;
import com.devstephen.resume_app_sp.service.AuthService;
import com.devstephen.resume_app_sp.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
import java.util.Map;

import static com.devstephen.resume_app_sp.utils.AppConstants.PREMIUM;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;
    private final AuthService authService;


    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestBody Map<String, String> request, Authentication authentication){

        String planType = request.get("planType");
        if (!PREMIUM.equalsIgnoreCase(planType)) {
            return ResponseEntity.badRequest().body(Map.of("message: ", "Invalid plan type."));
        }

        PaymentResponse payment = paymentService.createPayment(authentication.getPrincipal(), planType);

        return ResponseEntity.ok(payment);
    }

    @PostMapping("/verify-payment")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> request){

        return  null;
    }

    @GetMapping("/history")
    public ResponseEntity<?> paymentHistory(Authentication authentication){
        AuthResponse response = authService.getProfile(authentication.getPrincipal());
        return null;
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<?> getOrderDetails(@PathVariable String orderId){

        return null;
    }

}
