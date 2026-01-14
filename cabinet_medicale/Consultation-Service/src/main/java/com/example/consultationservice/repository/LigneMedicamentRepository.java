package com.example.consultationservice.repository;

import com.example.consultationservice.entity.LigneMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LigneMedicamentRepository extends JpaRepository<LigneMedicament, Long> {
}