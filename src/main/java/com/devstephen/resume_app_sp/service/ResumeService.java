package com.devstephen.resume_app_sp.service;

import com.devstephen.resume_app_sp.dto.AuthResponse;
import com.devstephen.resume_app_sp.dto.CreateResumeRequest;
import com.devstephen.resume_app_sp.entity.Resume;
import com.devstephen.resume_app_sp.repository.ResumeRepository;
import com.devstephen.resume_app_sp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ResumeService {

    private final ResumeRepository repository;
    private final UserRepository userRepository;
    private final AuthService authService;

    public Resume createResume(CreateResumeRequest request,
                               Object principalObject) {
        Resume newResume = new Resume();

        AuthResponse currentProfile = authService.getProfile(principalObject);

        newResume.setUserId(currentProfile.getUserId());
        newResume.setTitle(request.getTitle());

        setDefaultResumeData(newResume);

        return repository.save(newResume);
    }

    private void setDefaultResumeData(Resume resume) {
        resume.setProfileInfo(new Resume.ProfileInfo());
        resume.setContactInfo(new Resume.ContactInfo());
        resume.setWorkExperiences(new ArrayList<>());
        resume.setEducation(new ArrayList<>());
        resume.setSkills(new ArrayList<>());
        resume.setProjects(new ArrayList<>());
        resume.setCertifications(new ArrayList<>());
        resume.setLanguages(new ArrayList<>());
        resume.setHobbies(new ArrayList<>());

    }

    public List<Resume> getResumes(Object principalObject) {

        AuthResponse currentProfile = authService.getProfile(principalObject);

        String userId = currentProfile.getUserId();
        return repository.findByUserIdOrderByUpdatedAtDesc(userId);
    }


    public Resume getResumeById(String resumeId , @Nullable Object principal) {

        AuthResponse currentProfile = authService.getProfile(principal);

        Optional<Resume> existingResume = repository.findByUserIdAndId(currentProfile.getUserId(), resumeId);

        if (existingResume.isPresent()) {
            return existingResume.get();

        }
        throw new RuntimeException("No resumes available for the Id: "+ resumeId);

    }

}
