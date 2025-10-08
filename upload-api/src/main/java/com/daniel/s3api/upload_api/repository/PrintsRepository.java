package com.daniel.s3api.upload_api.repository;

import com.daniel.s3api.upload_api.infrastructure.entities.Print;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrintsRepository extends JpaRepository <Print, Integer>{
}
