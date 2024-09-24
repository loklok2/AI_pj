package com.choice.board.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
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

@Service
@Transactional
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
    public QboardDTO createQboard(QboardDTO qboardDTO, String username, List<MultipartFile> images) throws IOException {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Qboard qboard = convertToEntity(qboardDTO);
        qboard.setMember(member);
        Qboard savedQboard = qboardRepository.save(qboard);

        if (images != null && !images.isEmpty()) {
            addImagesToQboard(savedQboard.getQboardId(), images);
        }

        return convertToDTO(savedQboard);
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

    private void addImagesToQboard(Long qboardId, List<MultipartFile> images) throws IOException {
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                addImage(qboardId, image);
            }
        }
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
        qboard.setQboardId(qboardDTO.getId());
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        qboard.setContent(qboardDTO.getContent());
        qboard.setCreateDate(qboardDTO.getCreateDate());
        qboard.setEditedDate(qboardDTO.getEditedDate());
        return qboard;
    }

    // Q&A 게시글 엔티티 수정
    private void updateQboardFromDTO(Qboard qboard, QboardDTO qboardDTO) {
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        qboard.setContent(qboardDTO.getContent());
        qboard.setEditedDate(qboardDTO.getEditedDate());
    }
}