package com.devstephen.resume_app_sp.repository;

import com.devstephen.resume_app_sp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByEmail(String email);

    Boolean existsByEmail (String email);

    Optional<User> findByVerificationToken(String verificationToken);
}
