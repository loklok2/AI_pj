package com.choice.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.choice.board.dto.QboardCreateRequest;
import com.choice.board.dto.QboardDTO;
import com.choice.board.entity.QboardImg;
import com.choice.board.service.QboardService;

@RestController
@RequestMapping("/api/qboards")
public class QboardController {

    @Autowired
    private QboardService qboardService;

    // Q&A 게시글 생성
    // @PostMapping
    // public ResponseEntity<?> createQboard(@RequestPart("qboard") QboardDTO
    // qboardDTO,
    // @RequestPart(value = "images", required = false) List<MultipartFile> images,
    // @AuthenticationPrincipal UserDetails userDetails) {
    // try {
    // QboardDTO createdQboard = qboardService.createQboard(qboardDTO,
    // userDetails.getUsername(), images);
    // return new ResponseEntity<>(createdQboard, HttpStatus.CREATED);
    // } catch (IOException e) {
    // return new ResponseEntity<>("이미지 처리 중 오류가 발생했습니다.",
    // HttpStatus.INTERNAL_SERVER_ERROR);
    // } catch (RuntimeException e) {
    // return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    // } catch (Exception e) {
    // return new ResponseEntity<>("Q&A 게시글 작성 중 오류가 발생했습니다.",
    // HttpStatus.INTERNAL_SERVER_ERROR);
    // }
    // }

    @PostMapping
    public ResponseEntity<?> createQboard(@RequestBody QboardCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            QboardDTO createdQboard = qboardService.createQboard(request.getQboard(), userDetails.getUsername(),
                    request.getBase64Images());
            return new ResponseEntity<>(createdQboard, HttpStatus.CREATED);
        } catch (IOException e) {
            return new ResponseEntity<>("이미지 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 작성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Q&A 게시글 조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getQboard(@PathVariable("id") Long id) {
        try {
            QboardDTO qboard = qboardService.getQboardWithComments(id);
            return new ResponseEntity<>(qboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    // 모든 Q&A 게시글 조회
    @GetMapping
    public ResponseEntity<?> getAllQboards() {
        try {
            List<QboardDTO> qboards = qboardService.getAllQboards();
            return new ResponseEntity<>(qboards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자의 Q&A 게시글 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyQboards(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            List<QboardDTO> myQboards = qboardService.getQboardsByUserId(userDetails.getUsername());
            return new ResponseEntity<>(myQboards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("내 Q&A 게시글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQboard(@PathVariable("id") Long id, @RequestPart("qboard") QboardDTO qboardDTO,
            @RequestPart(value = "newImages", required = false) List<MultipartFile> newImages,
            @RequestPart(value = "deletedImageIds", required = false) List<Long> deletedImageIds,
            @AuthenticationPrincipal UserDetails userDetails) throws IOException {
        try {
            QboardDTO updatedQboard = qboardService.updateQboard(id, qboardDTO, userDetails.getUsername(),
                    newImages,
                    deletedImageIds);
            return new ResponseEntity<>(updatedQboard, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 업데이트 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQboard(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            qboardService.deleteQboard(id, userDetails.getUsername());
            return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{qboardId}/images")
    public ResponseEntity<?> getImagesForQboard(@PathVariable("qboardId") Long qboardId) {
        try {
            List<QboardImg> images = qboardService.getImagesForQboard(qboardId);
            return new ResponseEntity<>(images, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("이미지를 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
