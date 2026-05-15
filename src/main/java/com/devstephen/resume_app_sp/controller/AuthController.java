package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.LoginRequest;
import com.devstephen.resume_app_sp.dto.RegisterRequest;
import com.devstephen.resume_app_sp.service.AuthService;
import com.devstephen.resume_app_sp.service.FileUploadService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

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

    @GetMapping(VERIFY_EMAIL)
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

    @PostMapping(RESEND_VERIFICATION)
    public ResponseEntity<?> resendVerification(@RequestBody Map<String, String> body) {
        String email = body.get("email");

        if (Objects.isNull(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message ", " Email is required!"));
        }

        authService.resendVerification(email);
        return ResponseEntity.ok().body(Map.of("success", true, "message", "verification email sent"));
    }

    @GetMapping(PROFILE)
    public ResponseEntity<?> getProfile(Authentication authentication){
        Object principalObject = authentication.getPrincipal();

        AuthResponse currentProfile = authService.getProfile(principalObject);

        return ResponseEntity.ok(currentProfile);
    }
}
