package com.daniel.s3api.upload_api.infrastructure.repository;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository <User, Integer> {
}
