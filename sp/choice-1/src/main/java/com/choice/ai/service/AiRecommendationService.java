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
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
import com.choice.product.entity.Product;
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
                throw new RuntimeException("이미지 분석 실패", e);
            }
        });
    }

    @Transactional(readOnly = true)
    private AiAnalysisResult processFlaskResponse(AiAnalysisResponse flaskResponse, MultipartFile image,
            Long memberId) {
        List<Long> productIds = flaskResponse.getRecomResult().stream()
                .map(Long::valueOf)
                .collect(Collectors.toList());

        List<Product> products = productRepository.findAllWithImagesAndAttributesById(productIds);
        List<ProductsDTO> recommendedProducts = products.stream()
                .map(this::convertToProductDTO)
                .collect(Collectors.toList());

        List<StyleIndexDTO> styleIndices = flaskResponse.getStyleResult().stream()
                .map(index -> {
                    ProductAttribute attribute = productAttributeRepository.findById(index.longValue())
                            .orElseThrow(() -> new RuntimeException("스타일 속성을 찾을 수 없습니다: " + index));
                    return new StyleIndexDTO(attribute.getAttributeId(), attribute.getNameKo());
                })
                .collect(Collectors.toList());

        AiAnalysis savedAnalysis = saveAnalysis(flaskResponse, image, memberId);

        return new AiAnalysisResult(savedAnalysis.getId().longValue(), recommendedProducts, styleIndices);

    }

    @Transactional
    private AiAnalysis saveAnalysis(AiAnalysisResponse flaskResponse, MultipartFile image, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다"));

        AiAnalysis aiAnalysis = new AiAnalysis();
        aiAnalysis.setMember(member);
        aiAnalysis.setImagePath(saveImage(image, memberId));
        aiAnalysis.setAnalysisDate(LocalDateTime.now()); // 분석 날짜 설정

        // 스타일 인덱스 저장
        for (Integer styleIndex : flaskResponse.getStyleResult()) {
            ProductAttribute attribute = productAttributeRepository.findById(styleIndex.longValue())
                    .orElseThrow(() -> new RuntimeException("스타일 속성을 찾을 수 없습니다: " + styleIndex));
            AiAnalysisStyle style = new AiAnalysisStyle();
            style.setAiAnalysis(aiAnalysis);
            style.setAttribute(attribute);
            aiAnalysis.getStyles().add(style);
        }

        // 추천 상품 저장
        for (int i = 0; i < flaskResponse.getRecomResult().size(); i++) {
            Long productId = flaskResponse.getRecomResult().get(i).longValue();
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
            // 현재 날짜와 시간을 포함한 디렉토리 경로 생성
            LocalDateTime now = LocalDateTime.now();
            String datePath = now.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

            // 절대 경로 사용
            Path directoryPath = Paths.get(imageStoragePath, memberId.toString(), datePath).toAbsolutePath();
            Files.createDirectories(directoryPath);

            // 원본 파일 이름 가져오기
            String originalFilename = image.getOriginalFilename();

            // 유니크한 파일명 생성 (원본 파일 이름 유지)
            String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = directoryPath.resolve(fileName);

            // 이미지 저장
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 저장된 이미지의 상대 경로 반환
            return Paths.get(memberId.toString(), datePath, fileName).toString();
        } catch (IOException e) {
            log.error("이미지 저장 실패: ", e);
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null) {
            return "";
        }
        int lastIndexOf = filename.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ""; // 확장자가 없는 경우
        }
        return filename.substring(lastIndexOf);
    }

    private ProductsDTO convertToProductDTO(Product product) {
        ProductsDTO dto = new ProductsDTO();
        dto.setProductId(product.getProductId());
        dto.setName(product.getName());
        dto.setInfo(product.getInfo());
        dto.setPrice(product.getPrice());

        if (product.getImages() != null && !product.getImages().isEmpty()) {
            dto.setImagePath(product.getImages().iterator().next().getPimgPath());
        }

        return dto;
    }
}