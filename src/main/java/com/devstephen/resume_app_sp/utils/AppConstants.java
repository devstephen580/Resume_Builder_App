package com.devstephen.resume_app_sp.utils;

public class AppConstants {

    public static final String AUTH_CONTROLLER = "/api/auth";

    public static final String REGISTER = "/register";
    public static final String VERIFY_EMAIL = "/verify-email";
    public static final String UPLOAD_IMAGE = "/upload-image";
    public static final String LOGIN = "/login";
    public static final String RESEND_VERIFICATION = "/resend-verification";
    public static final String PROFILE = "/get-profile";

    public static final String RESUME_CONTROLLER = "/api/resume";

    public static final String CREATE = "/create-resume";
    public static final String GET_RESUMES = "/get-resume";
    public static final String GET_RESUMES_BY_ID = "/get-resume-by-id/{id}";
    public static final String UPDATE_RESUME = "/update-resume/{id}";
    public static final String UPLOAD_IMAGE_R = "/{id}/upload-image";
    public static final String DELETE_RESUME =  "/{id}/delete-resume";
}
