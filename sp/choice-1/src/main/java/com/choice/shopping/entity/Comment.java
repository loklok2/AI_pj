package com.choice.shopping.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

import com.choice.auth.entity.Member;

@Entity
@Table(name = "comment")
@Data
// 댓글 엔티티
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId; // 댓글 ID

    @ManyToOne
    @JoinColumn(name = "qboard_id")
    private Qboard qboard; // 게시글 ID

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member; // 사용자 ID

    private String content; // 댓글 내용

    @Column(name = "create_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createDate; // 생성 날짜

    @Column(name = "edited_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime editedDate; // 수정 날짜

}