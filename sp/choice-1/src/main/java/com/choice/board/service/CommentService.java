package com.choice.board.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.stream.Collectors;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.dto.CommentResponseDTO;
import com.choice.board.entity.Comment;
import com.choice.board.entity.Qboard;
import com.choice.board.repository.CommentRepository;
import com.choice.board.repository.QboardRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private MemberRepository memberRepository;

    public CommentResponseDTO createComment(Long qboardId, String username, String content) {
        Qboard qboard = qboardRepository.findById(qboardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Member user = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setQboard(qboard);
        comment.setMember(user);
        comment.setContent(content);
        comment.setCreateDate(LocalDateTime.now());
        comment.setEditedDate(LocalDateTime.now());

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment);
    }

    public CommentResponseDTO updateComment(Long commentId, String username, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getUsername().equals(username) && !isAdmin(username)) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(content);
        comment.setEditedDate(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return convertToDTO(updatedComment);
    }

    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getUsername().equals(username) && !isAdmin(username)) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDTO> getCommentsByQboardId(Long qboardId) {
        List<Comment> comments = commentRepository.findByQboard_QboardId(qboardId);
        return comments.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private boolean isAdmin(String username) {
        return memberRepository.findByUsername(username)
                .map(member -> "ADMIN".equals(member.getRole()))
                .orElse(false);
    }

    private CommentResponseDTO convertToDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getCommentId())
                .qboardId(comment.getQboard().getQboardId())
                .userId(comment.getMember().getUserId())
                .username(comment.getMember().getUsername())
                .content(comment.getContent())
                .createDate(comment.getCreateDate())
                .editedDate(comment.getEditedDate())
                .build();
    }
}