package com.devstephen.resume_app_sp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CreateResumeRequest {

    @NotBlank(message = "Title is required!")
    private String title;
}
