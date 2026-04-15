package com.devstephen.resume_app_sp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String authorizationUrl;
    private String receipt;
    private int amount;
    private String currency;
    private String planType;
    private String status;

    private String paystackOrderId;


}
