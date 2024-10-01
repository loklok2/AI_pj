package com.choice.board.dto;

import com.choice.board.entity.Qboard;
import com.choice.board.entity.Comment;
import com.choice.board.entity.QboardImg;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class QboardMapper {

    // Qboard 엔티티를 QboardDTO로 변환
    public QboardDTO toDto(Qboard qboard) {
        return QboardDTO.builder()
                .id(qboard.getQboardId())
                .userId(qboard.getMember().getUserId())
                .username(qboard.getMember().getUsername())
                .boardType(qboard.getBoardType())
                .title(qboard.getTitle())
                .content(qboard.getContent())
                .images(toImageDtoList(qboard.getImages())) // 이미지 리스트를 DTO로 변환
                .createDate(qboard.getCreateDate())
                .editedDate(qboard.getEditedDate())
                .comments(toCommentDtoList(qboard.getComments())) // 댓글 리스트를 DTO로 변환
                .build();
    }

    // QboardDTO를 Qboard 엔티티로 변환
    public Qboard toEntity(QboardDTO qboardDTO) {
        Qboard qboard = new Qboard();
        qboard.setBoardType(qboardDTO.getBoardType());
        qboard.setTitle(qboardDTO.getTitle());
        qboard.setContent(qboardDTO.getContent());
        return qboard;
    }

    // Comment 엔티티 리스트를 CommentDTO 리스트로 변환
    public List<CommentResponseDTO> toCommentDtoList(List<Comment> comments) {
        return comments.stream()
                .map(comment -> CommentResponseDTO.builder()
                        .commentId(comment.getCommentId())
                        .qboardId(comment.getQboard().getQboardId())
                        .userId(comment.getMember().getUserId())
                        .username(comment.getMember().getUsername())
                        .content(comment.getContent())
                        .createDate(comment.getCreateDate())
                        .editedDate(comment.getEditedDate())
                        .build())
                .collect(Collectors.toList());
    }

    // QboardImg 엔티티 리스트를 QboardImgDTO 리스트로 변환
    public List<QboardImgDTO> toImageDtoList(List<QboardImg> images) {
        return images.stream()
                .map(image -> QboardImgDTO.builder()
                        .id(image.getQimgId())
                        .qboardId(image.getQboard().getQboardId())
                        .imgName(image.getQimgName())
                        .imgPath(image.getQimgPath())
                        .build())
                .collect(Collectors.toList());
    }
}
