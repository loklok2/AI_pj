package com.choice.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.choice.ai.entity.AiAnalysis;

public interface AiAnalysisRepository extends JpaRepository<AiAnalysis, Long> {
}
