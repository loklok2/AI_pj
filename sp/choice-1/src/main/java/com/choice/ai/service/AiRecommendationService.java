package com.choice.ai.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.choice.ai.dto.AiAnalysisResponse;
import com.choice.ai.dto.AiAnalysisResult;
import com.choice.ai.dto.ProductsDTO;
import com.choice.ai.dto.StyleIndexDTO;
import com.choice.ai.entity.AiAnalysis;
import com.choice.ai.entity.AiAnalysisStyle;
import com.choice.ai.entity.AiRecommendation;
import com.choice.ai.repository.AiAnalysisRepository;
import com.choice.auth.entity.Member;
import com.choice.auth.repository.MemberRepository;
import com.choice.product.entity.ProductAttribute;
import com.choice.product.repository.ProductAttributeRepository;
import com.choice.product.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AiRecommendationService {

    @Value("${app.image.storage.path}")
    private String imageStoragePath;

    private final AiAnalysisRepository aiAnalysisRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final ProductAttributeRepository productAttributeRepository;
    private final FlaskClientService flaskClientService;

    public AiRecommendationService(AiAnalysisRepository aiAnalysisRepository,
            ProductRepository productRepository,
            MemberRepository memberRepository,
            ProductAttributeRepository productAttributeRepository,
            FlaskClientService flaskClientService) {
        this.aiAnalysisRepository = aiAnalysisRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.productAttributeRepository = productAttributeRepository;
        this.flaskClientService = flaskClientService;
    }

    @Transactional
    public CompletableFuture<AiAnalysisResult> analyzeAndRecommend(MultipartFile image, Long memberId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                AiAnalysisResponse flaskResponse = flaskClientService.analyzeImage(image);
                return processFlaskResponse(flaskResponse, image, memberId);
            } catch (Exception e) {
                log.error("이미지 분석 중 오류 발생", e);
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 분석 실패", e);
            }
        }).orTimeout(300, TimeUnit.SECONDS);
    }

    @Transactional(readOnly = true)
    private AiAnalysisResult processFlaskResponse(AiAnalysisResponse flaskResponse, MultipartFile image,
            Long memberId) {
        List<Long> productIds = flaskResponse.getRecomResult().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<Object[]> productDetails = productRepository.findProductDetailsById(productIds);
        List<ProductsDTO> recommendedProducts = productDetails.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        List<StyleIndexDTO> styleIndices = flaskResponse.getStyleResult().stream()
                .map(index -> {
                    ProductAttribute attribute = productAttributeRepository.findById(index.longValue())
                            .orElseThrow(() -> new RuntimeException("스타일 속성을 찾을 수 없습니다: " + index));
                    return new StyleIndexDTO(attribute.getAttributeId(), attribute.getNameKo());
                })
                .collect(Collectors.toList());

        AiAnalysis savedAnalysis = null;
        if (memberId != null) {
            savedAnalysis = saveAnalysis(flaskResponse, image, memberId);
        }

        String captionTranslated = flaskResponse.getCaptionResult();

        return new AiAnalysisResult(
                savedAnalysis != null ? savedAnalysis.getId().longValue() : null,
                recommendedProducts,
                styleIndices,
                captionTranslated);
    }

    @Transactional
    private AiAnalysis saveAnalysis(AiAnalysisResponse flaskResponse, MultipartFile image, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));

        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setMember(member);
        aiAnalysis.setImagePath(saveImage(image, memberId));
        aiAnalysis.setAnalysisDate(LocalDateTime.now());

        for (Integer styleIndex : flaskResponse.getStyleResult()) {
            ProductAttribute attribute = productAttributeRepository.findById(styleIndex.longValue())
                    .orElseThrow(() -> new RuntimeException("스타일 속성을 찾을 수 없습니다: " + styleIndex));
            AiAnalysisStyle style = new AiAnalysisStyle();
            style.setAiAnalysis(aiAnalysis);
            style.setAttribute(attribute);
            aiAnalysis.getStyles().add(style);
        }

        for (int i = 0; i < flaskResponse.getRecomResult().size(); i++) {
            Long productId = Long.valueOf(flaskResponse.getRecomResult().get(i));
            AiRecommendation recommendation = new AiRecommendation();
            recommendation.setAiAnalysis(aiAnalysis);
            recommendation.setProductId(productId);
            recommendation.setRecommendationOrder(i + 1);
            aiAnalysis.getRecommendations().add(recommendation);
        }

        return aiAnalysisRepository.save(aiAnalysis);
    }

    private String saveImage(MultipartFile image, Long memberId) {
        try {
            LocalDateTime now = LocalDateTime.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            Path directoryPath = Paths.get(imageStoragePath, memberId.toString(), datePath).toAbsolutePath();
            Files.createDirectories(directoryPath);

            String originalFilename = image.getOriginalFilename();

            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = directoryPath.resolve(fileName);

            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return Paths.get(memberId.toString(), datePath, fileName).toString();
        } catch (IOException e) {
            log.error("이미지 저장 실패: ", e);
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    private ProductsDTO convertToProductDTO(Object[] productDetail) {
        ProductsDTO dto = new ProductsDTO();
        dto.setProductId((Long) productDetail[0]);
        dto.setName((String) productDetail[1]);
        dto.setInfo((String) productDetail[2]);
        dto.setPrice((Long) productDetail[4]);
        dto.setImagePath((String) productDetail[10]);
        return dto;
    }
}