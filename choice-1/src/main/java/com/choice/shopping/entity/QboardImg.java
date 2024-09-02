package com.choice.shopping.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity // 엔티티로 설정
@Table(name = "qboard_img") // 테이블 이름 설정
public class QboardImg {

    @Id // 기본 키 설정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 설정
    private Long qimgId; // Q&A 게시판 이미지 ID

    private Long qboardId; // Q&A 게시판 ID (Foreign Key)
    private String qimgName; // 이미지 이름
    private byte[] qimgData; // 이미지 데이터


}
