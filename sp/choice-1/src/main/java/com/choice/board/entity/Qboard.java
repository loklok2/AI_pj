package com.choice.board.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "qboard")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// Q&A 게시판 엔티티
public class Qboard {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long qboardId; // Q&A 게시판 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // Member 엔티티와의 연관 관계

    private String boardType; // 게시판 유형
    private String title; // 게시글 제목
    private String content; // 게시글 내용

    @Column(name = "create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 게시글 생성 일자

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 게시글 수정 일자

    // 댓글 엔티티와의 연관 관계
    @OneToMany(mappedBy = "qboard", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();
}
