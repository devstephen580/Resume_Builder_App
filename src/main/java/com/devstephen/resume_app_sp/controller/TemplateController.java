package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/templates")
@RequiredArgsConstructor
@Slf4j
public class TemplateController {

    private final TemplateService templateService;

    @GetMapping
    public ResponseEntity<?> getTemplate(Authentication authentication){
        Map<String, Object> response = templateService.getTemplates(authentication.getPrincipal());
        return ResponseEntity.ok(response);
    }
}
