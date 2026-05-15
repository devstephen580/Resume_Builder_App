package com.devstephen.resume_app_sp.service;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.LoginRequest;
import com.devstephen.resume_app_sp.dto.RegisterRequest;
import com.devstephen.resume_app_sp.entity.User;
import com.devstephen.resume_app_sp.exceptions.ResourceExistsException;
import com.devstephen.resume_app_sp.repository.UserRepository;
import com.devstephen.resume_app_sp.jwtconfig.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


    @Value("${app.base.url}")
    private String appBaseUrl;

    public AuthResponse register(RegisterRequest request) {
        log.info("Inside AuthService: register() {} ", request);

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResourceExistsException("User already exists with this email");
        }

        //To entity
        User newUser = toEntity(request);

        userRepository.save(newUser);


        sendVerificationEmail(newUser);

        return toRegisterResponse(newUser);
    }

    private void sendVerificationEmail(User newUser) {

        log.info("Inside AuthService - sendVerificationEMail(): {}", newUser);
        try {
            String link = appBaseUrl + "/api/auth/verify-email?token=" + newUser.getVerificationToken();
            String htmlMessage = "<!DOCTYPE html>" +
                    "<html lang='en'>" +
                    "<head>" +
                    "<meta charset='UTF-8'/>" +
                    "<meta name='viewport' content='width=device-width, initial-scale=1.0'/>" +
                    "</head>" +
                    "<body style='margin:0;padding:0;background-color:#0f0f14;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;'>" +
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='background-color:#0f0f14;'>" +
                    "<tr><td align='center' style='padding:40px 16px;'>" +
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='540' style='margin:auto;'>" +


                    // MAIN CARD
                    "<tr><td style='background:#16161f;border:1px solid rgba(255,255,255,0.07);border-radius:24px;overflow:hidden;box-shadow:0 24px 64px rgba(0,0,0,0.5);'>" +

                    // Hero
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr>" +
                    "<td style='background:linear-gradient(140deg,#1e1b38 0%,#16161f 65%);padding:44px 44px 32px;border-bottom:1px solid rgba(255,255,255,0.05);text-align:center;'>" +
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' style='margin:0 auto 22px;'><tr>" +
                    "<td style='width:68px;height:68px;background:rgba(108,99,255,0.1);border:1.5px solid rgba(108,99,255,0.3);border-radius:50%;text-align:center;vertical-align:middle;'>" +
                    "<span style='font-size:28px;line-height:68px;'>&#9993;</span>" +
                    "</td></tr></table>" +
                    "<h1 style='margin:0 0 10px;font-size:24px;font-weight:700;color:#f0f0f8;letter-spacing:-0.03em;line-height:1.3;'>Verify your email address</h1>" +
                    "<p style='margin:0;font-size:14px;color:#7b7b94;line-height:1.6;'>Hi <strong style='color:#c5c0f0;'>" + newUser.getName() + "</strong>, thanks for signing up!<br/>Please confirm your email to activate your account.</p>" +
                    "</td></tr></table>" +

                    // Body
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr>" +
                    "<td style='padding:36px 44px 40px;text-align:center;'>" +
                    "<p style='margin:0 0 28px;font-size:14px;color:#9b9bb0;line-height:1.7;'>Click the button below to verify your email address.<br/>This link will expire at <strong style='color:#a89cff;'>" +
                    newUser.getVerificationExpires().format(DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")) +
                    "</strong>.</p>" +

                    // Button
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' align='center' style='margin:0 auto 32px;'><tr>" +
                    "<td style='border-radius:12px;background:linear-gradient(135deg,#6c63ff,#8b83ff);box-shadow:0 8px 24px rgba(108,99,255,0.4);'>" +
                    "<a href='" + link + "' style='display:inline-block;padding:16px 44px;font-size:15px;font-weight:700;color:#ffffff;text-decoration:none;border-radius:12px;letter-spacing:0.01em;font-family:Helvetica Neue,Helvetica,Arial,sans-serif;'>" +
                    "Verify Email &rarr;</a>" +
                    "</td></tr></table>" +

                    // Divider
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%' style='margin-bottom:24px;'><tr>" +
                    "<td style='height:1px;background:rgba(255,255,255,0.06);font-size:0;line-height:0;'>&nbsp;</td></tr></table>" +

                    // Fallback link
                    "<p style='margin:0 0 10px;font-size:12px;color:#6b6b80;text-transform:uppercase;letter-spacing:0.08em;'>Or copy this link</p>" +
                    "<a style='margin:0 0 28px;font-size:11px;color:#5a5a72;word-break:break-all;background:#1a1a26;border:1px solid rgba(255,255,255,0.06);border-radius:8px;padding:12px 16px;line-height:1.6;font-family:Courier New,monospace;'>" + link + "</a>" +

                    // Warning box
                    "<table role='presentation' cellspacing='0' cellpadding='0' border='0' width='100%'><tr>" +
                    "<td style='padding:14px 18px;background:#1a1a26;border-radius:10px;border-left:3px solid #6c63ff;text-align:left;'>" +
                    "<p style='margin:0;font-size:12px;color:#9b9bb0;line-height:1.6;'><strong style='color:#c5c0f0;'>Didn't sign up?</strong><br/>If you didn't create this account, you can safely ignore this email.</p>" +
                    "</td></tr></table>" +
                    "</td></tr></table>" +
                    "</td></tr>" +

                    // Footer
                    "<tr><td style='padding:24px 44px 8px;text-align:center;'>" +
                    "<br/>&copy; 2026 Authify. All rights reserved.</p>" +
                    "<p style='margin:0;'><a href='#' style='font-size:11px;color:#6c63ff;text-decoration:none;'>Privacy Policy</a>&nbsp;&middot;&nbsp;<a href='#' style='font-size:11px;color:#6c63ff;text-decoration:none;'>Terms of Service</a></p>" +
                    "</td></tr>" +

                    "</table></td></tr></table>" +
                    "</body></html>";
            emailService.sendEmail(newUser.getEmail(), "Verify your email", htmlMessage);

        } catch (Exception e) {
            log.info("Exception occurred at sendVerificationEMail(): {}", e.getMessage());
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

    private AuthResponse toRegisterResponse(User newUser) {
        return AuthResponse.builder()
                .userId(newUser.getId())
                .name(newUser.getName())
                .email(newUser.getEmail())
                .emailVerified(newUser.getEmailVerified())
                .token(newUser.getVerificationToken()) //Set to null later, added for postman accessing
                .profileImageUrl(newUser.getProfileImageUrl())
                .subscriptionPlan(newUser.getSubscription())
                .createdAt(newUser.getCreatedAt())
                .updatedAt(newUser.getUpdatedAt())
                .build();
    }

    public User toEntity(RegisterRequest request) {

        
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImageUrl(request.getProfileImageUrl())
                .subscription("basic")
                .emailVerified(false)
                .verificationToken(UUID.randomUUID().toString())
                .verificationExpires(LocalDateTime.now().plusHours(24))
                .build();
    }


    public void verifyEmail(String token) {
        log.info("Inside AuthService : verifyEmail(): {}", token);
        User user = userRepository.findByVerificationToken(token).orElseThrow(() -> new RuntimeException("Invalid or expired verification token"));

        if (user.getVerificationToken() != null && user.getVerificationExpires().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Verification token has expired. Please request new one.");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationExpires(null);

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {

        User existingUser = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new ResourceExistsException("User does not exist for the provided email: " + request.getEmail()));

        if (!existingUser.getEmailVerified()) {
            throw new RuntimeException("Please verify email before logging in!");
        }

        if (!passwordEncoder.matches(request.getPassword(), existingUser.getPassword())) {
            throw new UsernameNotFoundException("Invalid email or password");
        }

        return convertToLoginResponse(existingUser);

    }

    public AuthResponse convertToLoginResponse(User user) {

        String token = jwtUtil.generateToken(user.getId());

            return AuthResponse.builder()
                    .userId(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .subscriptionPlan(user.getSubscription())
                    .profileImageUrl(user.getProfileImageUrl())
                    .emailVerified(user.getEmailVerified())
                    .token(token)
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
    }


    public void resendVerification(String email) {
        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found."));

        if (!existingUser.getEmailVerified()) {
            existingUser.setVerificationToken(existingUser.getVerificationToken());
            existingUser.setVerificationExpires(LocalDateTime.now().plusHours(24));


            userRepository.save(existingUser);
            sendVerificationEmail(existingUser);
        }else {
            throw new RuntimeException("Email is already verified");
        }

    }

    public AuthResponse getProfile(Object principalObject) {
        User existingUser = (User) principalObject;
        return convertToLoginResponse(existingUser);
    }
}
