package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.LoginRequest;
import com.devstephen.resume_app_sp.dto.LoginResponse;
import com.devstephen.resume_app_sp.dto.RegisterRequest;
import com.devstephen.resume_app_sp.service.AuthService;
import com.devstephen.resume_app_sp.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

import static com.devstephen.resume_app_sp.utils.AppConstants.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(AUTH_CONTROLLER)
public class AuthController {

    private final AuthService authService;
    private final FileUploadService fileUploadService;

    @PostMapping(REGISTER)
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Inside AuthController - register(): {}", request);

        AuthResponse response = authService.register(request);
        log.info("Inside AuthController. Response from authService: {}", response);

        return ResponseEntity.status(HttpStatus.OK).body(response);

    }

    @GetMapping(VERY_EMAIL)
    public ResponseEntity<?> verifyEmail (@RequestParam String token){
        log.info("Inside AuthController - verifyEmail(): {}", token);

        authService.verifyEmail(token);
        return ResponseEntity.status(HttpStatus.OK).body(Map.of("meassage", "Email verified successfully."));
    }

    @PostMapping(UPLOAD_IMAGE)
    public ResponseEntity<?> uploadImage(@RequestPart ("image")MultipartFile file) throws IOException {

        Map<String, String> response = fileUploadService.uploadSingleImage(file);
        log.info("Inside AuthController - uploadImage(): {}", response);
        return ResponseEntity.ok(response);
    }

    @PostMapping(LOGIN)
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        AuthResponse loginResponse = authService.login(request);
        return ResponseEntity.ok(loginResponse);
    }
}
