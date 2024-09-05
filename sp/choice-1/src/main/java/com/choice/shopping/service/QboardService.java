package com.choice.shopping.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.choice.auth.repository.MemberRepository;
import com.choice.shopping.entity.Qboard;
import com.choice.shopping.entity.QboardImg;
import com.choice.shopping.repository.QboardImgRepository;
import com.choice.shopping.repository.QboardRepository;

@Service
@Transactional
// Q&A 게시글 서비스
public class QboardService {

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private QboardImgRepository qboardImgRepository;

    @Autowired
    private MemberRepository memberRepository;

    // Q&A 게시글 생성
    public Qboard createQboard(Qboard qboard, String username, List<MultipartFile> images) throws IOException {
        Long userId = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId();
        qboard.setUserId(userId); // 사용자 ID 설정
        Qboard savedQboard = qboardRepository.save(qboard); // Q&A 게시글 저장

        if (images != null) { // 이미지가 있는 경우
            for (MultipartFile image : images) {
                if (!image.isEmpty()) { // 이미지가 비어있지 않은 경우
                    try {
                        addImage(savedQboard.getQboardId(), image); // 이미지 추가
                    } catch (SQLException e) {
                        throw new RuntimeException("이미지 추가 중 오류 발생", e);
                    }
                }
            }
        }

        return savedQboard;
    }

    // Q&A 게시글 조회
    public Qboard getQboard(Long id) {
        return qboardRepository.findById(id) // ID로 Q&A 게시글 조회
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    // 모든 Q&A 게시글 조회
    public List<Qboard> getAllQboards() {
        return qboardRepository.findAll(); // 모든 Q&A 게시글 조회
    }

    // Q&A 게시글 업데이트
    public Qboard updateQboard(Long id, Qboard qboardDetails, String username, List<MultipartFile> newImages,
            List<Long> deletedImageIds) throws IOException {
        Qboard qboard = qboardRepository.findById(id) // ID로 Q&A 게시글 조회
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!qboard.getUserId().equals(memberRepository.findByUsername(username) // 사용자 ID 조회
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId())) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }
        qboard.setTitle(qboardDetails.getTitle()); // 게시글 제목 업데이트
        qboard.setContent(qboardDetails.getContent()); // 게시글 내용 업데이트

        // 새 이미지 추가
        if (newImages != null) { // 새 이미지가 있는 경우
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) { // 이미지가 비어있지 않은 경우
                    try {
                        addImage(qboard.getQboardId(), image); // 이미지 추가
                    } catch (SQLException e) {
                        throw new RuntimeException("이미지 추가 중 오류 발생", e);
                    }
                }
            }
        }

        // 삭제할 이미지 제거
        if (deletedImageIds != null) { // 삭제할 이미지가 있는 경우
            for (Long imageId : deletedImageIds) {
                deleteImage(imageId); // 이미지 삭제
            }
        }

        return qboardRepository.save(qboard);
    }

    // Q&A 게시글 삭제
    public void deleteQboard(Long id, String username) {
        Qboard qboard = qboardRepository.findById(id) // ID로 Q&A 게시글 조회
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!qboard.getUserId().equals(memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId())) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        // 게시글 관련 이미지 모두 삭제
        List<QboardImg> images = qboardImgRepository.findByQboard_QboardId(id); // 게시글 ID로 이미지 조회
        qboardImgRepository.deleteAll(images); // 이미지 삭제

        qboardRepository.delete(qboard); // 게시글 삭제
    }

    // 이미지 추가
    private void addImage(Long qboardId, MultipartFile file) throws IOException, SQLException {
        QboardImg img = new QboardImg(); // 이미지 객체 생성
        Qboard qboard = qboardRepository.findById(qboardId) // 게시글 ID로 게시글 조회
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        img.setQboard(qboard); // 게시글 설정
        img.setQimgName(file.getOriginalFilename()); // 이미지 이름 설정

        // Blob 객체 생성
        byte[] bytes = file.getBytes(); // 이미지 바이트 배열 생성
        Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes); // Blob 객체 생성
        img.setQimgData(blob); // 이미지 데이터 설정

        qboardImgRepository.save(img);
    }

    // 이미지 삭제
    private void deleteImage(Long qimgId) {
        qboardImgRepository.deleteById(qimgId); // 이미지 ID로 이미지 삭제
    }

    // 게시글 이미지 조회
    public List<QboardImg> getImagesForQboard(Long qboardId) {
        return qboardImgRepository.findByQboard_QboardId(qboardId); // 게시글 ID로 이미지 조회
    }
}