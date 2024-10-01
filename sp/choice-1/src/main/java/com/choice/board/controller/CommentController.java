package com.choice.board.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.choice.board.dto.CommentRequestDTO;
import com.choice.board.dto.CommentResponseDTO;
import com.choice.board.service.CommentService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{qboardId}")
    public ResponseEntity<?> createComment(@PathVariable("qboardId") Long qboardId,
            @RequestBody CommentRequestDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            if (userDetails != null) {
                String username = userDetails.getUsername();
                CommentResponseDTO createdCommentDTO = commentService.createComment(qboardId, username,
                        commentDTO.getContent());
                return new ResponseEntity<>(createdCommentDTO, HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 작성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable("commentId") Long commentId,
            @RequestBody CommentRequestDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            CommentResponseDTO updatedCommentDTO = commentService.updateComment(commentId, userDetails.getUsername(),
                    commentDTO.getContent());
            return new ResponseEntity<>(updatedCommentDTO, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable("commentId") Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.deleteComment(commentId, userDetails.getUsername());
            return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 댓글 목록 조회
    @GetMapping("/qboard/{qboardId}")
    public ResponseEntity<?> getCommentsByQboardId(@PathVariable("qboardId") Long qboardId) {
        try {
            List<CommentResponseDTO> comments = commentService.getCommentsByQboardId(qboardId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}