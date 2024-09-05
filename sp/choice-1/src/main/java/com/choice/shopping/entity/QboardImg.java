package com.choice.shopping.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;

@Entity
@Table(name = "qboard_img")
@Data
public class QboardImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long qimgId; // Q&A 이미지 ID

    @ManyToOne
    @JoinColumn(name = "qboard_id")
    private Qboard qboard; // Q&A 게시판 ID

    private String qimgName; // 이미지 이름

    @Lob
    private Blob qimgData; // 이미지 데이터
}