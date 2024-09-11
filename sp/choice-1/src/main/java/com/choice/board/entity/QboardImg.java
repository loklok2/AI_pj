package com.choice.board.entity;

import jakarta.persistence.*;
import lombok.Data;


@Entity
@Table(name = "qboard_img")
@Data
// Q&A 이미지 엔티티
public class QboardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qimgId; // Q&A 이미지 ID

    @ManyToOne
    @JoinColumn(name = "qboard_id")
    private Qboard qboard; // Q&A 게시판 ID

    private String qimgName; // 이미지 이름

    private String qimgPath; // 이미지 파일 경로
}