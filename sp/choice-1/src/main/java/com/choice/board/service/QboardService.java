package com.choice.board.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.board.dto.CommentDTO;
import com.choice.board.dto.QboardDTO;
import com.choice.board.entity.Comment;
import com.choice.board.entity.Qboard;
import com.choice.board.entity.QboardImg;
import com.choice.board.repository.CommentRepository;
import com.choice.board.repository.QboardImgRepository;
import com.choice.board.repository.QboardRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class QboardService {

    @Autowired
    private QboardRepository qboardRepository;

    @Autowired
    private QboardImgRepository qboardImgRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Value("${image.upload.path}")
    private String uploadPath;

    // Q&A 게시글 생성
    @Transactional
    public QboardDTO createQboard(QboardDTO qboardDTO, String username) throws IOException {
        log.info("게시글 저장 시작: {}", qboardDTO);

        // 사용자 정보 가져오기
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // Qboard 엔티티로 변환
        Qboard qboard = convertToEntity(qboardDTO);
        qboard.setMember(member);
        qboard.setCreateDate(LocalDateTime.now());
        qboard.setEditedDate(LocalDateTime.now());
        Qboard savedQboard = qboardRepository.save(qboard);

        // content에서 base64 이미지 추출
        List<String> base64Images = extractBase64Images(qboardDTO.getContent());
        if (!base64Images.isEmpty()) {
            addBase64ImagesToQboard(savedQboard.getQboardId(), base64Images);
        }

        // 이미지 태그를 제거한 텍스트만 content에 저장
        String contentWithoutImages = removeImageTags(qboardDTO.getContent());
        savedQboard.setContent(contentWithoutImages);
        qboardRepository.save(savedQboard);

        log.info("게시글 저장 완료: {}", savedQboard);
        return convertToDTO(savedQboard);
    }

    // Base64 이미지 추출 메서드
    private List<String> extractBase64Images(String content) {
        List<String> base64Images = new ArrayList<>();
        Pattern pattern = Pattern.compile("data:image/\\w+;base64,[^\"]+");
        Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            base64Images.add(matcher.group());
        }
        return base64Images;
    }

    // Base64 이미지를 파일로 저장
    private void addBase64ImagesToQboard(Long qboardId, List<String> base64Images) throws IOException {
        for (String base64Image : base64Images) {
            byte[] imageBytes = Base64.getDecoder().decode(base64Image.split(",")[1]);
            String fileName = System.currentTimeMillis() + ".png";
            String filePath = uploadPath + File.separator + fileName;

            // 이미지 파일 저장
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }

            // QboardImg 엔티티 저장
            QboardImg img = new QboardImg();
            Qboard qboard = qboardRepository.findById(qboardId)
                    .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
            img.setQboard(qboard);
            img.setQimgName(fileName);
            String relativePath = "/qboard/" + fileName;
            img.setQimgPath(relativePath);

            qboardImgRepository.save(img);
        }
    }

    // // 이미지 태그를 제거하고 텍스트만 남기기
    // private String removeImageTags(String content) {
    // // 이미지 태그 및 그 안의 모든 속성을 제거
    // return
    // content.replaceAll("<img\\s*[^>]*src\\s*=\\s*['\"]?([^'\"\\s>]*)['\"]?[^>]*>",
    // "").trim();
    // }

    // <img> 태그와 <p> 태그를 제거하고 텍스트만 남기는 메서드
    private String removeImageTags(String content) {
        // 1. <img> 태그를 제거
        String withoutImgTags = content.replaceAll("<img[^>]*>", "");

        // 2. <p> 태그를 제거하고 텍스트만 남기기
        return withoutImgTags.replaceAll("<(/)?p[^>]*>", "").trim();
    }

    // Q&A 게시글 조회
    public QboardDTO getQboard(Long id) {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return convertToDTO(qboard);
    }

    public QboardDTO getQboardWithComments(Long id) {
        Qboard qboard = qboardRepository.findByIdWithComments(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        return convertToDTO(qboard);
    }

    // 모든 Q&A 게시글 조회
    public List<QboardDTO> getAllQboards() {
        return qboardRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // 사용자의 Q&A 게시글 조회
    public List<QboardDTO> getQboardsByUserId(String username) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        List<Qboard> qboards = qboardRepository.findByMember(member);
        return qboards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Q&A 게시글 수정
    public QboardDTO updateQboard(Long id, QboardDTO qboardDTO, String username, List<MultipartFile> newImages,
            List<Long> deletedImageIds) throws IOException {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        if (!qboard.getMember().getUsername().equals(username)) {
            throw new RuntimeException("게시글을 수정할 권한이 없습니다.");
        }

        updateQboardFromDTO(qboard, qboardDTO);

        if (newImages != null) {
            for (MultipartFile image : newImages) {
                if (!image.isEmpty()) {
                    addImage(qboard.getQboardId(), image);
                }
            }
        }

        if (deletedImageIds != null) {
            for (Long imageId : deletedImageIds) {
                deleteImage(imageId);
            }
        }

        Qboard updatedQboard = qboardRepository.save(qboard);
        return convertToDTO(updatedQboard);
    }

    // Q&A 게시글 삭제
    @Transactional
    public void deleteQboard(Long id, String username) {
        Qboard qboard = qboardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));

        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (!qboard.getMember().getUsername().equals(username) && !member.getRole().equals("ADMIN")) {
            throw new RuntimeException("게시글을 삭제할 권한이 없습니다.");
        }

        // 댓글 삭제
        commentRepository.deleteByQboard(qboard);

        // 이미지 삭제
        List<QboardImg> images = qboardImgRepository.findByQboard_QboardId(id);
        for (QboardImg image : images) {
            File file = new File(image.getQimgPath());
            if (file.exists()) {
                file.delete();
            }
        }
        qboardImgRepository.deleteAll(images);

        // 게시글 삭제
        qboardRepository.delete(qboard);
    }

    // Q&A 게시글 이미지 조회
    public List<QboardImg> getImagesForQboard(Long qboardId) {
        return qboardImgRepository.findByQboard_QboardId(qboardId);
    }

    // Q&A 게시글 이미지 추가
    private void addImage(Long qboardId, MultipartFile file) throws IOException {
        QboardImg img = new QboardImg();
        Qboard qboard = qboardRepository.findById(qboardId)
                .orElseThrow(() -> new RuntimeException("게시글을 찾을 수 없습니다."));
        img.setQboard(qboard);
        img.setQimgName(file.getOriginalFilename());

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        String filePath = uploadPath + fileName;
        file.transferTo(new File(filePath));
        img.setQimgPath(filePath);

        qboardImgRepository.save(img);
    }

    // Q&A 게시글 이미지 삭제
    private void deleteImage(Long qimgId) {
        qboardImgRepository.deleteById(qimgId);
    }

    // Q&A 게시글 DTO 변환
    private QboardDTO convertToDTO(Qboard qboard) {
        return QboardDTO.builder()
                .id(qboard.getQboardId())
                .userId(qboard.getMember().getUserId())
                .username(qboard.getMember().getUsername())
                .boardType(qboard.getBoardType())
                .title(qboard.getTitle())
                .content(qboard.getContent())
                .createDate(qboard.getCreateDate())
                .editedDate(qboard.getEditedDate())
                .comments(qboard.getComments().stream()
                        .map(this::convertToCommentDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    // 댓글 DTO 변환
    private CommentDTO convertToCommentDTO(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getMember().getUserId())
                .username(comment.getMember().getUsername())
                .content(comment.getContent())
                .createDate(comment.getCreateDate())
                .editedDate(comment.getEditedDate())
                .build();
    }

    // Q&A 게시글 엔티티 변환
    private Qboard convertToEntity(QboardDTO qboardDTO) {
        Qboard qboard = new Qboard();
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        // qboard.setContent(qboardDTO.getContent());
        return qboard;
    }

    // Q&A 게시글 엔티티 수정
    private void updateQboardFromDTO(Qboard qboard, QboardDTO qboardDTO) {
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        qboard.setContent(qboardDTO.getContent());
        qboard.setEditedDate(LocalDateTime.now()); // 수정 시 현재 시간으로 갱신
    }
}
