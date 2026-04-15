package com.devstephen.resume_app_sp.service;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.devstephen.resume_app_sp.utils.AppConstants.PREMIUM;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {
    private final AuthService authService;

    public Map<String, Object> getTemplates(Object principal){

        AuthResponse currentProfile = authService.getProfile(principal);

        String subscriptionPlan = currentProfile.getSubscriptionPlan();

        Boolean isPremium = PREMIUM.equalsIgnoreCase(currentProfile.getSubscriptionPlan());

        List<String> availableTemplates;

        if (isPremium) {
            availableTemplates = List.of("01", "02", "03");
        }else {
            availableTemplates = List.of("01");
        }

        Map<String, Object> data = new HashMap<>();
        data.put("subscriptionPlan", subscriptionPlan);
        data.put("availableTemplates", availableTemplates);
        data.put("isPremium", isPremium);
        data.put("allTemplates", List.of("01", "02", "03"));

        return data;

    }
}
