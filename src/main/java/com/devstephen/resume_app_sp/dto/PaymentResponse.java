package com.devstephen.resume_app_sp.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentResponse {
    private String authorizationUrl;
    private String paystackSignature;
    private String paystackAccessCode;
    private String receipt;
    private Integer amount;
    private String currency;
    private String planType;
    private String status;

    private String paystackOrderId;


}
