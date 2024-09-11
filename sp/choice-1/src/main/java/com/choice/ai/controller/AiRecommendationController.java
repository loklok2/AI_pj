package com.choice.ai.controller;

import java.util.concurrent.CompletableFuture;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.choice.ai.dto.AiAnalysisResult;
import com.choice.ai.service.AiRecommendationService;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RestController
@RequestMapping("/api/recommendation")
public class AiRecommendationController {

    private final AiRecommendationService aiRecommendationService;
    private final MemberRepository memberRepository;

    public AiRecommendationController(AiRecommendationService aiRecommendationService,
            MemberRepository memberRepository) {
        this.aiRecommendationService = aiRecommendationService;
        this.memberRepository = memberRepository;
    }

    @PostMapping("/analyze")
    public CompletableFuture<ResponseEntity<AiAnalysisResult>> analyzeAndRecommend(
            @RequestParam("file") MultipartFile image,
            @AuthenticationPrincipal UserDetails userDetails) {

        Long memberId = null;
        if (userDetails != null) {
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            memberId = member.getUserId();
        }

        return aiRecommendationService.analyzeAndRecommend(image, memberId)
                .thenApply(ResponseEntity::ok);
    }
}