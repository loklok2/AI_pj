package com.sbs.auth.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sbs.auth.domain.Member;
import com.sbs.auth.domain.UserInfo;
import com.sbs.auth.repository.MemberRepository;
import com.sbs.auth.service.MemberService;
import com.sbs.board.domain.BoardDTO;
import com.sbs.board.domain.Comment;
import com.sbs.board.repository.BoardRepository;
import com.sbs.board.repository.CommentRepository;

@RestController
@RequestMapping("/api/mypage")
public class MyPageController {

    @Autowired
    private MemberService memberService; // MemberService 주입

    @Autowired
    private MemberRepository memberRepository; // MemberRepository 주입

    @Autowired
    private BoardRepository boardRepository; // BoardRepository 주입

    @Autowired
    private CommentRepository commentRepository; // CommentRepository 주입
    
    // 로그인된 사용자의 정보 반환
    @GetMapping("/info")
    public ResponseEntity<UserInfo> getMyInfo(Authentication authentication) {
        String username = authentication.getName(); // 인증 객체에서 사용자 이름 가져오기
        UserInfo userInfo = memberService.getUserInfo(username); // 사용자 이름으로 UserInfo 객체 생성
        return new ResponseEntity<>(userInfo, HttpStatus.OK); // 사용자 정보와 함께 OK 응답 반환
    }
    
    // 로그인된 사용자가 작성한 게시글 반환
    @GetMapping("/my-boards")
    public ResponseEntity<List<BoardDTO>> getMyBoards(Authentication authentication) {
        String username = authentication.getName(); // 인증 객체에서 사용자 이름 가져오기
        Member member = memberRepository.findByUsername(username).orElse(null); // 사용자 이름으로 Member 객체 조회
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // 사용자 정보가 없으면 UNAUTHORIZED 응답 반환
        }

        // 사용자가 작성한 게시글 목록 조회 및 DTO 변환
        List<BoardDTO> boardDTOs = boardRepository.findByAuthor(member)
                .stream()
                .map(BoardDTO::fromEntity) // 엔티티를 DTO로 변환
                .collect(Collectors.toList());

        return new ResponseEntity<>(boardDTOs, HttpStatus.OK); // 게시글 목록과 함께 OK 응답 반환
    }
    
    // 로그인된 사용자가 작성한 댓글 반환
    @GetMapping("/my-comments")
    public ResponseEntity<List<Comment>> getMyComments(Authentication authentication) {
        String username = authentication.getName(); // 인증 객체에서 사용자 이름 가져오기
        Member member = memberRepository.findByUsername(username).orElse(null); // 사용자 이름으로 Member 객체 조회

        // 사용자 정보가 없으면 UNAUTHORIZED 응답 반환
        if (member == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // 사용자가 작성한 댓글 목록 조회
        List<Comment> comments = commentRepository.findByAuthor(member);
        return new ResponseEntity<>(comments, HttpStatus.OK); // 댓글 목록과 함께 OK 응답 반환
    }
}
