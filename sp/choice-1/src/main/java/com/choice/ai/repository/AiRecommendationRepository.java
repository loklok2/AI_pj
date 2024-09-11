package com.choice.ai.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.choice.ai.entity.AiRecommendation;

public interface AiRecommendationRepository extends JpaRepository<AiRecommendation, Long> {
}
