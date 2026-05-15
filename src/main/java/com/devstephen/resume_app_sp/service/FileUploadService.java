package com.devstephen.resume_app_sp.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.entity.Resume;
import com.devstephen.resume_app_sp.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileUploadService {

    private final Cloudinary cloudinary;
    private final AuthService authService;
    private final ResumeRepository resumeRepository;

    public Map<String, String> uploadSingleImage(MultipartFile file) throws IOException {
        Map<String, Object> imageUploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "image"));


        log.info("Inside FileUploadService - uploadSingleImage(): {}", imageUploadResult.get("secure_url").toString());
        return Map.of("imageUrl", imageUploadResult.get("secure_url").toString());
    }

    public Map<String, String> uploadResumeImage(String resumeId, MultipartFile thumbnail, MultipartFile profileImage, @Nullable Object principal) throws IOException {

        AuthResponse currentProfile = authService.getProfile(principal);
        Resume existingResume = resumeRepository.findById(resumeId).orElseThrow(() -> new RuntimeException("Resume already exist!"));

        Map<String, String> response = new HashMap<>();

        Map<String, String> uploadResult = null;

        if (Objects.nonNull(thumbnail)) {
            uploadResult = uploadSingleImage(thumbnail); //thumbnail upload
            existingResume.setThumbnailLink(uploadResult.get("imageUrl"));
            response.put("thumbnailLink", uploadResult.get("imageUrl"));

        }


        if (Objects.nonNull(profileImage)) {
            uploadResult = uploadSingleImage(profileImage); //profileImage upload
            if (Objects.isNull(existingResume.getProfileInfo())) {
                existingResume.setProfileInfo(new Resume.ProfileInfo());
            }
            existingResume.getProfileInfo().setProfilePrevUrl(uploadResult.get("imageUrl"));
            response.put("imagePreviewUrl", uploadResult.get("imageUrl"));
        }


        resumeRepository.save(existingResume);
        response.put("message", "Images uploaded successfully.");


        return response;
    }
}
