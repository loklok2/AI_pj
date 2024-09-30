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

import com.choice.board.dto.CommentDTO;
import com.choice.board.entity.Comment;
import com.choice.board.service.CommentService;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/{qboardId}")
    public ResponseEntity<?> createComment(@PathVariable("qboardId") Long qboardId, @RequestBody CommentDTO commentDTO,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Comment comment = commentService.createComment(qboardId, userDetails.getUsername(),
                    commentDTO.getContent());
            CommentDTO createdCommentDTO = commentService.convertToDTO(comment);
            return new ResponseEntity<>(createdCommentDTO, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 작성 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable Long commentId, @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Comment updatedComment = commentService.updateComment(commentId, userDetails.getUsername(), content);
            return new ResponseEntity<>(updatedComment, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 수정 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            commentService.deleteComment(commentId, userDetails.getUsername());
            return new ResponseEntity<>("댓글이 성공적으로 삭제되었습니다.", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 삭제 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/qboard/{qboardId}")
    public ResponseEntity<?> getCommentsByQboardId(@PathVariable Long qboardId) {
        try {
            List<Comment> comments = commentService.getCommentsByQboardId(qboardId);
            return new ResponseEntity<>(comments, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("댓글 목록을 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
