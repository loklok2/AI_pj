package com.choice.board.controller;

import com.choice.board.entity.Comment;
import com.choice.board.service.CommentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
// 댓글 컨트롤러
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 생성
    @PostMapping("/{qboardId}")
    public ResponseEntity<Comment> createComment(@PathVariable Long qboardId,
            @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment comment = commentService.createComment(qboardId, userDetails.getUsername(), content);
        return ResponseEntity.ok(comment);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable Long commentId,
            @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        Comment updatedComment = commentService.updateComment(commentId, userDetails.getUsername(), content);
        return ResponseEntity.ok(updatedComment);
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok().build();
    }

    // 댓글 조회
    @GetMapping("/qboard/{qboardId}")
    public ResponseEntity<List<Comment>> getCommentsByQboardId(@PathVariable Long qboardId) {
        List<Comment> comments = commentService.getCommentsByQboardId(qboardId);
        return ResponseEntity.ok(comments);
    }
}