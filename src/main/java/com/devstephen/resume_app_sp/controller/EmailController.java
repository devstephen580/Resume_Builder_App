package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.service.EmailService;
import jakarta.mail.MessagingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
public class EmailController {

  private final EmailService emailService;

  @PostMapping(path = "/send-resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String, Object>> sendResumeByEmail(
      @RequestPart("recipientEmail") String recipientEmail,
      @RequestPart("subject") String subject,
      @RequestPart("message") String message,
      @RequestPart("pdfFile") MultipartFile pdfFile,
      Authentication authentication)
      throws IOException, MessagingException {

    Map<String, Object> response = new HashMap<>();

    if (Objects.isNull(recipientEmail) || Objects.isNull(pdfFile)) {
      response.put("success", false);
      response.put("message ", "Missing the required fields");
      return ResponseEntity.badRequest().body(response);
    }

    byte[] pdfBytes = pdfFile.getBytes();
    String originalFilename = pdfFile.getOriginalFilename();

    String fileName = Objects.nonNull(originalFilename) ? originalFilename : "resume.pdf";
    String emailSubject = Objects.nonNull(subject) ? subject : "Resume Application";
    String emailBody =
        Objects.nonNull(message) ? message : "Please find my resume attached. \n\nBest regards";

    emailService.emailWithAttachment(recipientEmail, emailBody, emailSubject, fileName, pdfBytes);

    response.put("success", true);
    response.put("message ", "Resume successfully sent to " + recipientEmail);

    return ResponseEntity.ok(response);
  }
}
