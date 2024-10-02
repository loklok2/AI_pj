package com.choice.board.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.dto.QboardDTO;
import com.choice.board.service.QboardService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/api/qboards")
public class QboardController {

    @Autowired
    private QboardService qboardService;

    @Autowired
    private MemberRepository memberRepository;

    // Q&A 게시글 작성

    @PostMapping("/create")
    public ResponseEntity<?> createQboard(@RequestBody QboardDTO qboardDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        log.info("Q&A 게시글 작성 요청 받음: {}", qboardDTO);
        try {
            // 서비스에서 게시글 생성 후, 생성된 QboardDTO 반환
            QboardDTO createdQboard = qboardService.createQboard(qboardDTO, userDetails.getUsername());
            log.info("Q&A 게시글 작성 완료: {}", createdQboard);
            return new ResponseEntity<>(createdQboard, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND); // 예외 처리
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 작성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 이미지 업로드 엔드포인트
    @PostMapping("/uploadImage")
    public ResponseEntity<?> uploadImage(@RequestParam("image") MultipartFile imageFile) {
        try {
            // 이미지 업로드 후 이미지 URL 반환
            String imageUrl = qboardService.uploadImage(imageFile);
            log.info("이미지 업로드 완료: {}", imageUrl);
            return new ResponseEntity<>(imageUrl, HttpStatus.OK); // URL 반환
        } catch (IOException e) {
            return new ResponseEntity<>("이미지 업로드 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Q&A 게시글 상세조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getQboard(@PathVariable("id") Long id, @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            QboardDTO qboard = qboardService.getQboardWithComments(id);

            // 관리자이거나 게시글 작성자인 경우에만 조회 가능
            if (member.getRole().equals(Role.ADMIN) || qboard.getUserId().equals(member.getUserId())) {
                return new ResponseEntity<>(qboard, HttpStatus.OK);
            } else {
                return new ResponseEntity<>("접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
            }
        } catch (RuntimeException e) {
            return new ResponseEntity<>("해당 게시글을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
        }
    }

    // 모든 Q&A 게시글 조회
    @GetMapping
    public ResponseEntity<?> getAllQboards(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails == null) {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
            Member member = memberRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            if (!member.getRole().equals(Role.ADMIN)) {
                return new ResponseEntity<>("관리자만 접근 가능합니다.", HttpStatus.FORBIDDEN);
            }
            List<QboardDTO> qboards = qboardService.getAllQboards();
            return new ResponseEntity<>(qboards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 사용자의 Q&A 게시글 목록 조회
    @GetMapping("/my")
    public ResponseEntity<?> getMyQboards(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 로그인된 사용자의 게시글만 조회
            List<QboardDTO> myQboards = qboardService.getQboardsByUserId(userDetails.getUsername());
            return new ResponseEntity<>(myQboards, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("내 Q&A 게시글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Q&A 게시글 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<?> updateQboard(@PathVariable("id") Long id,
            @RequestBody QboardDTO qboardDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 서비스에서 게시글 수정 후, 수정된 QboardDTO 반환
            QboardDTO updatedQboard = qboardService.updateQboard(id, qboardDTO, userDetails.getUsername());
            return new ResponseEntity<>(updatedQboard, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("게시글 수정 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("Q&A 게시글 업데이트 중 오류가 발생했습니다: " + e.getMessage(),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Q&A 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQboard(@PathVariable("id") Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 서비스에서 게시글 삭제
            qboardService.deleteQboard(id, userDetails.getUsername());
            return new ResponseEntity<>("게시글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("게시글 삭제 권한이 없습니다.", HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>("게시글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
