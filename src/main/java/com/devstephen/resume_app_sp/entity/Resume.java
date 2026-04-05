package com.devstephen.resume_app_sp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "resumes")
@EntityListeners(AuditingEntityListener.class)
@Data
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String userId;

    private String title;

    private String thumbnailLink;

    @Embedded
    private Template template;

    @Embedded
    private ProfileInfo profileInfo;

    @Embedded
    private ContactInfo contactInfo;

    @ElementCollection
    @CollectionTable(name = "resume_work_experiences", joinColumns = @JoinColumn(name = "resume_id"))
    private List<WorkExperience> workExperiences;

    @ElementCollection
    @CollectionTable(name = "resume_education", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Education> education;

    @ElementCollection
    @CollectionTable(name = "resume_skills", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Skill> skills;

    @ElementCollection
    @CollectionTable(name = "resume_certifications", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Certification> certifications;

    @ElementCollection
    @CollectionTable(name = "resume_languages", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Language> languages;

    @ElementCollection
    @CollectionTable(name = "resume_projects", joinColumns = @JoinColumn(name = "resume_id"))
    private List<Projects> projects;

    @ElementCollection
    @CollectionTable(name = "resume_hobbies", joinColumns = @JoinColumn(name = "resume_id"))
    private List<String> hobbies;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // ✅ Single object = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Template {
        private String theme;

        @ElementCollection
        @CollectionTable(name = "resume_template_colors", joinColumns = @JoinColumn(name = "resume_id"))
        private List<String> colorPalette;
    }

    // ✅ Single object = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class ProfileInfo {
        private String profilePrevUrl;
        private String fullName;
        private String designation;
        private String summary;
    }

    // ✅ Single object = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class ContactInfo {
        private String email;
        private String phone;
        private String location;
        private String linkedIn;
        private String github;
        private String website;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class WorkExperience {
        private String company;
        private String role;
        private String startDate;
        private String endDate;
        private String description;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Education {
        private String degree;
        private String institution;
        private String startDate;
        private String endDate;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Skill {
        private String company;
        private Integer progress;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Projects {
        private String title;
        private String description;
        private String github;
        private String liveDemo;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Certification {
        private String title;
        private String issuer;
        private String year;
    }

    // ✅ List of objects = @Embeddable
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Embeddable
    public static class Language {
        private String name;
        private String progress;
    }
}