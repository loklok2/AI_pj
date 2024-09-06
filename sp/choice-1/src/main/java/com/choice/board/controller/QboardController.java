package com.choice.board.controller;

import com.choice.board.entity.Qboard;
import com.choice.board.entity.QboardImg;
import com.choice.board.service.QboardService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/qboard")
// Q&A 게시판 컨트롤러
public class QboardController {

    @Autowired
    private QboardService qboardService;

    @PostMapping
    // Q&A 게시글 생성
    public ResponseEntity<Qboard> createQboard(
            @RequestPart("qboard") Qboard qboard,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Qboard createdQboard = qboardService.createQboard(qboard, userDetails.getUsername(), images);// QboardService
                                                                                                     // 클래스의
                                                                                                     // createQboard
                                                                                                     // 메서드를 호출하여 새로운
                                                                                                     // Q&A 게시글을 생성
        return new ResponseEntity<>(createdQboard, HttpStatus.CREATED);// 생성된 Q&A 게시글을 응답으로 반환
    }

    @GetMapping("/{id}")
    // Q&A 게시글 조회
    public ResponseEntity<Qboard> getQboard(@PathVariable Long id) {
        Qboard qboard = qboardService.getQboard(id);// QboardService 클래스의 getQboard 메서드를 호출하여 해당 ID의 Q&A 게시글을 조회
        return ResponseEntity.ok(qboard);
    }

    @GetMapping
    // Q&A 게시글 전체 조회
    public ResponseEntity<List<Qboard>> getAllQboards() {
        List<Qboard> qboards = qboardService.getAllQboards(); // QboardService 클래스의 getAllQboards 메서드를 호출하여 모든 Q&A 게시글을
                                                              // 조회
        return ResponseEntity.ok(qboards);
    }

    @PutMapping("/{id}")
    // Q&A 게시글 업데이트
    public ResponseEntity<Qboard> updateQboard(
            @PathVariable Long id,
            @RequestPart("qboard") Qboard qboardDetails,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deletedImageIds", required = false) List<Long> deletedImageIds,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        Qboard updatedQboard = qboardService.updateQboard(id, qboardDetails, userDetails.getUsername(), newImages,
                deletedImageIds); // QboardService 클래스의 updateQboard 메서드를 호출하여 해당 ID의 Q&A 게시글을 업데이트
        return ResponseEntity.ok(updatedQboard);
    }

    @DeleteMapping("/{id}")
    // Q&A 게시글 삭제
    public ResponseEntity<?> deleteQboard(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        qboardService.deleteQboard(id, userDetails.getUsername()); // QboardService 클래스의 deleteQboard 메서드를 호출하여 해당 ID의
                                                                   // Q&A 게시글을 삭제
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{qboardId}/images")
    // Q&A 게시글 이미지 조회
    public ResponseEntity<List<QboardImg>> getImagesForQboard(@PathVariable Long qboardId) {
        List<QboardImg> images = qboardService.getImagesForQboard(qboardId); // QboardService 클래스의 getImagesForQboard
                                                                             // 메서드를 호출하여 해당 ID의 Q&A 게시글 이미지를 조회
        return ResponseEntity.ok(images);
    }
}