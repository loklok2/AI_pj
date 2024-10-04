package com.choice.board.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.choice.auth.entity.Member;
import com.choice.auth.entity.Role;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.dto.QboardDTO;
import com.choice.board.dto.QboardMapper;
import com.choice.board.entity.Qboard;
import com.choice.board.repository.QboardRepository;

@Service
public class QboardService {

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private QboardMapper qboardMapper; // Mapper를 통해 엔티티와 DTO 간 변환을 처리

    @Value("${image.upload.path}")
    private String uploadPath;

    // 이미지 업로드 로직
    public String uploadImage(MultipartFile imageFile) throws IOException {
        String originalFileName = imageFile.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);
        String newFileName = UUID.randomUUID().toString() + "." + fileExtension;
        Path filePath = Paths.get(uploadPath + File.separator + newFileName);

        // 파일 저장
        Files.copy(imageFile.getInputStream(), filePath);

        // 저장된 이미지의 URL 반환
        return "/images/qboard/" + newFileName;
    }

    // Q&A 게시글 생성 로직
    @Transactional
    public QboardDTO createQboard(QboardDTO qboardDTO, String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // DTO에서 엔티티로 변환 후 설정
        Qboard qboard = qboardMapper.toEntity(qboardDTO);
        qboard.setMember(member);
        qboard.setCreateDate(LocalDateTime.now());
        qboard.setEditedDate(LocalDateTime.now());

        Qboard savedQboard = qboardRepository.save(qboard);

        // 엔티티를 DTO로 변환하여 반환
        return qboardMapper.toDto(savedQboard);
    }

    // Q&A 게시글 수정 로직
    @Transactional
    public QboardDTO updateQboard(Long id, QboardDTO qboardDTO, String username) {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 작성자가 맞는지 확인
        if (!qboard.getMember().getUsername().equals(username)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        // DTO의 데이터를 엔티티에 반영
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        qboard.setContent(qboardDTO.getContent());
        qboard.setEditedDate(LocalDateTime.now());

        Qboard updatedQboard = qboardRepository.save(qboard);

        // 수정된 엔티티를 DTO로 변환하여 반환
        return qboardMapper.toDto(updatedQboard);
    }

    // 파일 확장자 추출 메서드
    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // 게시글 및 댓글을 함께 조회
    @Transactional(readOnly = true)
    public QboardDTO getQboardWithComments(Long id) {
        // ID로 게시글을 조회하고, 없으면 예외 발생
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        // 게시글을 DTO로 변환하여 반환
        return qboardMapper.toDto(qboard);
    }

    // 모든 게시글 조회
    @Transactional(readOnly = true)
    public List<QboardDTO> getAllQboards() {
        // 모든 게시글을 조회한 후, DTO로 변환하여 리스트로 반환
        return qboardRepository.findAll()
                .stream()
                .map(qboardMapper::toDto) // 각 게시글을 DTO로 변환
                .collect(Collectors.toList());
    }

    // 특정 사용자의 게시글 조회
    @Transactional(readOnly = true)
    public List<QboardDTO> getQboardsByUserId(String username) {
        // 사용자 정보를 조회
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 해당 사용자의 게시글을 조회한 후, DTO 리스트로 반환
        return qboardRepository.findByMember(member)
                .stream()
                .map(qboardMapper::toDto) // 각 게시글을 DTO로 변환
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteQboard(Long id, String username) {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!qboard.getMember().getUsername().equals(username) && !member.getRole().equals(Role.ADMIN)) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        qboardRepository.delete(qboard);
    }

}
