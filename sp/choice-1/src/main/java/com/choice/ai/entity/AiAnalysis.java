package com.choice.ai.entity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

import com.choice.auth.entity.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "ai_analysis")
@Data
public class AiAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id; // int 타입으로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "analysis_date", nullable = false)
    private LocalDateTime analysisDate;

    @Column(name = "image_path")
    private String imagePath;

    @OneToMany(mappedBy = "aiAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiAnalysisStyle> styles = new ArrayList<>();

    @OneToMany(mappedBy = "aiAnalysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AiRecommendation> recommendations = new ArrayList<>();

    public AiAnalysis() {
        this.analysisDate = LocalDateTime.now();
    }

    public LocalDateTime getAnalysisDate() {
        return analysisDate;
    }

    public void setAnalysisDate(LocalDateTime analysisDate) {
        this.analysisDate = analysisDate;
    }
}