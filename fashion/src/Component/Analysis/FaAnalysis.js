import React, { useRef, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner, faCircleXmark, faRotateRight, faCircleInfo, faAngleRight } from '@fortawesome/free-solid-svg-icons'; 
import 'bootstrap/dist/css/bootstrap.min.css';  // Bootstrap 스타일 적용
import '../../CSS/FaAnalysis.css';  // CSS 파일 임포트

const FaAnalysis = () => {
    const fileInputRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const [imageFile, setImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [topStyles, setTopStyles] = useState([]); // 상위 3개 스타일 상태
    const [recommendedProducts, setRecommendedProducts] = useState([]); // 추천 상품 상태
    const [clothesDescription, setClothesDescription] = useState(''); // 옷 설명 상태
    const [showRelatedStyles, setShowRelatedStyles] = useState(false); // 관련 스타일 더보기 상태
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달창 상태

    const handleClick = () => {
        fileInputRef.current.click();
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        setImageFile(file);

        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setPreviewUrl(reader.result);
            };
            reader.readAsDataURL(file);
        } else {
            setPreviewUrl(null);
        }
    };

    const handleAnalyzeClick = async () => {
        if (!imageFile) {
            alert('이미지를 업로드 해주세요.');
            return;
        }

        setLoading(true);

        try {
            const formData = new FormData();
            formData.append('image', imageFile);

            // 분석 API 호출
            const response = await fetch('http://10.125.121.188:8080/api/recommendation/analyze', {
                method: 'POST',
                body: formData,
            });
            const data = await response.json();

             // 응답 데이터 로그
            console.log('API 응답 데이터:', data);

            // API 응답 처리
            const { styleindex, recommendedProducts } = data;

            // 스타일 인덱스 설정
            setTopStyles(styleindex.map(style => [style.nameKo, Math.random()]));
            // 추천 상품 설정
            setRecommendedProducts(recommendedProducts);
            // 옷 설명 설정 (수동으로 추가)
            setClothesDescription('이 옷은 편안하고 스타일리시한 디자인을 가진 캐주얼 아이템입니다. 일상에서 착용하기 좋으며 다양한 스타일과 매치하기 적합합니다.');

        } catch (error) {
            console.error('분석 중 오류 발생:', error);
            alert('분석 중 오류가 발생했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const handleReload = () => {
        window.location.reload();
    };

    const toggleModal = () => {
        setIsModalOpen(!isModalOpen);
    };

    return (
        <div className="fa-analysis-unique-container">
            <div className="fa-analysis-unique-header">
                <h1>패션 분석</h1>
                <p>정보를 알고 싶은 옷의 이미지를 이곳에 넣어 옷에 대한 정보를 확인해보세요.</p>
                <div className="fa-analysis-unique-divider"></div>
            </div>

            <div className="fa-analysis-unique-content">
                <div className="fa-analysis-unique-upload-section">
                    <h2>옷 업로드</h2>
                    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                        <p style={{ margin: 0 }}>* 명확한 사진을 업로드 해주세요.</p>
                        <FontAwesomeIcon 
                            icon={faCircleInfo} 
                            onClick={toggleModal} 
                            style={{ cursor: 'pointer', fontSize: '18px', color: 'gray', marginRight: '400px' }} 
                        />
                        <FontAwesomeIcon 
                            icon={faRotateRight} 
                            onClick={handleReload} 
                            style={{ cursor: 'pointer', fontSize: '18px', color: 'gray' }} 
                        />
                    </div>

                    <div className="fa-analysis-unique-upload-box" onClick={handleClick} style={{ position: 'relative' }}>
                        {loading ? (
                            <FontAwesomeIcon icon={faSpinner} spin />
                        ) : previewUrl ? (
                            <div style={{ position: 'relative', width: '100%', height: '100%' }}>
                                <img 
                                    src={previewUrl} 
                                    alt="Preview" 
                                    style={{ 
                                        width: '100%', 
                                        height: '100%', 
                                        objectFit: 'contain'
                                    }} 
                                />
                                <FontAwesomeIcon 
                                    icon={faCircleXmark} 
                                    onClick={() => setImageFile(null)} 
                                    style={{ 
                                        position: 'absolute', 
                                        top: '10px', 
                                        right: '10px', 
                                        cursor: 'pointer', 
                                        fontSize: '24px'
                                    }} 
                                />
                            </div>
                        ) : (
                            <p>클릭하여 이미지를 업로드</p>
                        )}
                        <input 
                            type="file" 
                            ref={fileInputRef} 
                            style={{ display: 'none' }} 
                            onChange={handleImageChange}
                        />
                    </div>
                    <button 
                        className="fa-analysis-unique-analyze-button" 
                        onClick={handleAnalyzeClick}
                        disabled={loading}
                    >
                        {loading ? "분석 중..." : "분석하기"}
                    </button>
                </div>

                {/* 분석 결과 및 옷 설명 영역 */}
                {topStyles.length > 0 && (
                    <div className="fa-analysis-unique-description-section">
                        <p>이미지의 분석 결과 아래의 내용과 같습니다.</p>
                        <h2>
                            {topStyles.length > 0 ? topStyles.map((style, index) => (
                                <span key={index}>
                                    {style[0]}{index < topStyles.length - 1 && ', '}
                                </span>
                            )) : ''}
                        </h2>

                        <p style={{ marginTop: '80px', marginBottom: '80px' }}>
                            {clothesDescription ? clothesDescription : ''}
                        </p>

                        {topStyles.length > 0 && (
                            <div style={{ textAlign: 'center', marginTop: '30px' }}>
                                <a 
                                    href="#" 
                                    onClick={() => setShowRelatedStyles(true)}  
                                    style={{ color: 'gray', textDecoration: 'none', fontSize: '18px' }}
                                >
                                    관련 스타일 더보기
                                    <FontAwesomeIcon icon={faAngleRight} style={{ marginLeft: '5px' }} />
                                </a>
                            </div>
                        )}
                    </div>
                )}
            </div>

            {/* 모달창 코드 */}
            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h2>주의 사항</h2>
                        <p>사물(옷)하나만 있을 경우, 옷과 관련이 없는 사물의 경우 올바른 결과가 나오지 않습니다.</p>
                        <button onClick={toggleModal}>닫기</button>
                    </div>
                </div>
            )}

            {/* 관련 스타일 이미지 섹션 (관련 스타일 더보기 버튼을 눌러야 보임) */}
            {showRelatedStyles && (
                <div>
                    <h2 className="related-styles-title">관련 스타일</h2>

                    <div className="related-styles-section" style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'space-around' }}>
                        {recommendedProducts.map((product) => (
                            <div key={product.productId} className="related-style" style={{ flex: '0 0 30%', marginBottom: '20px' }}>
                                <p className="related-style-title" style={{ textAlign: 'center' }}>{product.name}</p>
                                <div className="related-style-inner">
                                    <img src={`http://localhost:8080/images/${product.imagePath}`} alt={product.name} style={{ height: '200px', width: '100%', objectFit: 'cover' }} />
                                    <div className="related-style-info">
                                        <p>{product.info}</p>
                                        <p>{product.price.toLocaleString()} 원</p>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
            )}
        </div>
    );
};

export default FaAnalysis;