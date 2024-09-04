package com.choice.shopping.service;

import java.io.IOException;
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
public class QboardService {

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private QboardImgRepository qboardImgRepository;

    @Autowired
    private MemberRepository memberRepository;

    public Qboard createQboard(Qboard qboard, String username, List<MultipartFile> images) throws IOException {
        Long userId = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId();
        qboard.setUserId(userId);
        Qboard savedQboard = qboardRepository.save(qboard);

        if (images != null) {
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    addImage(savedQboard.getQboardId(), image);
                }
            }
        }

        return savedQboard;
    }

    public Qboard getQboard(Long id) {
        return qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
    }

    public List<Qboard> getAllQboards() {
        return qboardRepository.findAll();
    }

    public Qboard updateQboard(Long id, Qboard qboardDetails, String username, List<MultipartFile> newImages,
            List<Long> deletedImageIds) throws IOException {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!qboard.getUserId().equals(memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId())) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }
        qboard.setTitle(qboardDetails.getTitle());
        qboard.setContent(qboardDetails.getContent());

        // 새 이미지 추가
        if (newImages != null) {
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    addImage(qboard.getQboardId(), image);
                }
            }
        }

        // 삭제할 이미지 제거
        if (deletedImageIds != null) {
            for (Long imageId : deletedImageIds) {
                deleteImage(imageId);
            }
        }

        return qboardRepository.save(qboard);
    }

    public void deleteQboard(Long id, String username) {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        if (!qboard.getUserId().equals(memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."))
                .getUserId())) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }
        // 게시글 관련 이미지 모두 삭제
        List<QboardImg> images = qboardImgRepository.findByQboardId(id);
        qboardImgRepository.deleteAll(images);

        qboardRepository.delete(qboard);
    }

    private void addImage(Long qboardId, MultipartFile file) throws IOException {
        QboardImg img = new QboardImg();
        img.setQboardId(qboardId);
        img.setQimgName(file.getOriginalFilename());
        img.setQimgData(file.getBytes());

        qboardImgRepository.save(img);
    }

    private void deleteImage(Long qimgId) {
        qboardImgRepository.deleteById(qimgId);
    }

    public List<QboardImg> getImagesForQboard(Long qboardId) {
        return qboardImgRepository.findByQboardId(qboardId);
    }
}