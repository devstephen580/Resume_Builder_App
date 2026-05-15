package com.devstephen.resume_app_sp.controller;

import com.devstephen.resume_app_sp.dto.CreateResumeRequest;
import com.devstephen.resume_app_sp.entity.Resume;
import com.devstephen.resume_app_sp.service.FileUploadService;
import com.devstephen.resume_app_sp.service.ResumeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.devstephen.resume_app_sp.utils.AppConstants.*;


@RestController
@RequiredArgsConstructor
@RequestMapping(RESUME_CONTROLLER)
@Slf4j
public class ResumeController {

    private final ResumeService resumeService;
    private final FileUploadService fileUploadService;

    @PostMapping(CREATE)
    public ResponseEntity<?> createResume(@Valid @RequestBody CreateResumeRequest request, Authentication authentication){
        Resume newResume = resumeService.createResume(request, authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.CREATED).body(newResume);
    }

    @GetMapping(GET_RESUMES)
    public ResponseEntity<?> getResumes(Authentication authentication){
        List<Resume> resumes = resumeService.getResumes(authentication.getPrincipal());
        return ResponseEntity.ok().body(resumes);
    }

    @GetMapping(GET_RESUMES_BY_ID)
    public ResponseEntity<?> getResumeById(@PathVariable String id, Authentication authentication){
        Resume existingResume = resumeService.getResumeById(id, authentication.getPrincipal());

        return ResponseEntity.status(HttpStatus.OK).body(existingResume);
    }

    @PutMapping(UPDATE_RESUME)
    public ResponseEntity<?> updateResume(@RequestBody Resume updatedData,
                                          @PathVariable String id,
                                          Authentication authentication){
        Resume response = resumeService.updateResume(id, updatedData, authentication.getPrincipal());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping(UPLOAD_RESUME_IMAGE)
    public ResponseEntity<?> uploadImage(@PathVariable String id,
                                         @RequestPart (value = "thumbnail", required = false) MultipartFile thumbnail,
                                         @RequestPart (value = "profileImage", required = false) MultipartFile profileImage,
                                         HttpServletRequest request,
                                         Authentication authentication) throws IOException {
        Map<String, String> response = fileUploadService.uploadResumeImage(id, thumbnail, profileImage, authentication.getPrincipal());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping(DELETE_RESUME)
    public ResponseEntity<?> deleteResume(@PathVariable String id, Authentication authentication){
        resumeService.deleteResume(id, authentication.getPrincipal());
        return ResponseEntity.ok(Map.of("message: ", "Resume deleted successfully."));
    }
}
