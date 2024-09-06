package com.choice.board.service;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.entity.Comment;
import com.choice.board.entity.Qboard;
import com.choice.board.repository.CommentRepository;
import com.choice.board.repository.QboardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
// 댓글 서비스
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private MemberRepository memberRepository;

    // 댓글 생성
    public Comment createComment(Long qboardId, String username, String content) {
        Qboard qboard = qboardRepository.findById(qboardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        Member user = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Comment comment = new Comment();
        comment.setQboard(qboard); // 게시글 설정
        comment.setMember(user); // 사용자 설정
        comment.setContent(content); // 내용 설정
        comment.setCreateDate(LocalDateTime.now()); // 생성 날짜 설정
        comment.setEditedDate(LocalDateTime.now()); // 수정 날짜 설정

        return commentRepository.save(comment);
    }

    // 댓글 수정
    public Comment updateComment(Long commentId, String username, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getUsername().equals(username) && !isAdmin(username)) {
            throw new AccessDeniedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(content); // 댓글 내용 업데이트
        comment.setEditedDate(LocalDateTime.now()); // 수정 날짜 설정

        return commentRepository.save(comment);
    }

    // 댓글 삭제
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("댓글을 찾을 수 없습니다."));

        if (!comment.getMember().getUsername().equals(username) && !isAdmin(username)) {
            throw new AccessDeniedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    // 댓글 조회
    public List<Comment> getCommentsByQboardId(Long qboardId) {
        return commentRepository.findByQboard_QboardId(qboardId);
    }

    // 관리자 여부 확인
    private boolean isAdmin(String username) {
        return memberRepository.findByUsername(username)
                .map(member -> "ADMIN".equals(member.getRole()))
                .orElse(false);
    }
}