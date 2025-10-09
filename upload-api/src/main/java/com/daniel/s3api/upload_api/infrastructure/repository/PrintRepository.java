package com.daniel.s3api.upload_api.infrastructure.repository;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintRepository extends JpaRepository <Print, Long> {
}
