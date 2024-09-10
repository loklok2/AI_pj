import React, { useRef, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner, faRotateRight, faCircleXmark, faCircleInfo, faAngleRight } from '@fortawesome/free-solid-svg-icons'; 
import ProgressBar from 'react-bootstrap/ProgressBar';
import 'bootstrap/dist/css/bootstrap.min.css';  // Bootstrap 스타일 적용
import '../../CSS/FaAnalysis.css';  // CSS 파일 임포트

const FaAnalysis = () => {
    const fileInputRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const [chartData, setChartData] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 상태 관리 추가
    const [showRelatedStyles, setShowRelatedStyles] = useState(false); // 관련 스타일 섹션을 위한 상태 추가

    const mockData = {
        predictions: [
            ['스트릿 스타일', 0.5], // 50%
            ['캐주얼 스타일', 0.3], // 30%
            ['클래식 스타일', 0.2], // 20%
        ]
    };

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

    const handleAnalyzeClick = () => {
        if (!imageFile) {
            alert('이미지를 업로드 해주세요.');
            return;
        }

        setLoading(true);

        setTimeout(() => {
            setLoading(false);
            setChartData(mockData.predictions);
        }, 1000); // 로딩을 위한 딜레이 추가
    };

    const handleReload = () => {
        window.location.reload();
    };

    const handleRemoveImage = (e) => {
        e.stopPropagation(); 
        setImageFile(null);
        setPreviewUrl(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = null;
        }
    };

    const toggleModal = () => {
        setIsModalOpen(!isModalOpen);
    };

    const toggleRelatedStyles = () => {
        setShowRelatedStyles(!showRelatedStyles); // 관련 스타일 섹션을 토글로 보여줌
    };

    return (
        <div className="fa-analysis-unique-container">
            <div className="fa-analysis-unique-header">
                <h1>패션 분석</h1>
                <p>정보를 알고 싶은 옷의 이미지를 이곳에 넣어 옷에 대한 정보를 확인해보세요.</p>
                <p className="fa-analysis-unique-small-text">
                    * 옷과 관련된 이미지를 넣어주세요. 관련된 이미지가 아닌 경우 올바른 결과를 보장하지 못합니다.
                </p>
                <div className="fa-analysis-unique-divider"></div>
            </div>

            <div className="fa-analysis-unique-content">
                <div className="fa-analysis-unique-upload-section">
                    <h2>옷 업로드</h2>
                    <div style={{ display: 'flex', alignItems: 'center' }}>
                        <p style={{ margin: 0 }}>* 명확한 사진을 업로드 해주세요.</p>
                        <FontAwesomeIcon 
                            icon={faCircleInfo}  
                            style={{ marginLeft: '8px', cursor: 'pointer', color: 'gray' }} 
                            onClick={toggleModal}  
                        />
                        <FontAwesomeIcon 
                            icon={faRotateRight}  
                            onClick={handleReload} 
                            style={{ cursor: 'pointer', marginLeft: 'auto', marginTop: '-15px', color: 'gray' }} 
                        />
                    </div>

                    <div 
                        className="fa-analysis-unique-upload-box" 
                        onClick={handleClick}
                        style={{ position: 'relative' }}
                    >
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
                                    onClick={handleRemoveImage} 
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

                {/* 진행형 막대를 사용한 분석 결과 */}
                {chartData && (
                    <div className="fa-analysis-unique-description-section">
                        <p>이미지의 분석 결과 아래의 내용과 같습니다.</p>
                        <h2>스트릿 스타일</h2>
                        {chartData.map((style, index) => (
                            <div key={index} style={{ marginBottom: '20px', textAlign: 'left' }}>
                                <p style={{ fontWeight: 'bold' }}>{style[0]} ({style[1] * 100}%)</p>
                                <ProgressBar 
                                    now={style[1] * 100} 
                                    label={`${style[1] * 100}%`} 
                                    style={{ height: '10px', borderRadius: '5px' }} 
                                />
                            </div>
                        ))}
                        <div style={{ textAlign: 'right', marginTop: '50px' }}>
                            <a 
                                href="#" 
                                onClick={toggleRelatedStyles} 
                                style={{ color: 'gray', textDecoration: 'none', fontSize: '18px' }}
                            >
                                관련 스타일 더보기
                                <FontAwesomeIcon icon={faAngleRight} style={{ marginLeft: '5px' }} />
                            </a>
                        </div>
                    </div>
                )}
            </div>

           {/* 관련 스타일 이미지 섹션 (관련 스타일 더보기 버튼을 눌러야 보임) */}
            {showRelatedStyles && (
                <div>
                    <h2 className="related-styles-title">관련 스타일</h2> {/* 타이틀 추가 */}
                    <div className="related-styles-section">
                        <div className="related-style">
                            <div className="related-style-placeholder"></div>
                            <p className="related-style-description">스타일 설명</p>
                        </div>
                        <div className="related-style">
                            <div className="related-style-placeholder"></div>
                            <p className="related-style-description">스타일 설명</p>
                        </div>
                        <div className="related-style">
                            <div className="related-style-placeholder"></div>
                            <p className="related-style-description">스타일 설명</p>
                        </div>
                    </div>
                </div>
            )}
            {/* 모달 컴포넌트 */}
            {isModalOpen && (
                <div className="modal-overlay" onClick={toggleModal}>
                    <div className="modal-content" onClick={(e) => e.stopPropagation()}>
                        <h2 style={{ fontSize: '24px', fontWeight: 'bold' }}>주의사항</h2>
                        <p>옷만 나오는 경우, 옷이 아닌 다른 물건인 경우는 올바르지 못한 결과가 나옵니다. 사람이 착용하고 있는 옷이 올바른 결과를 제공합니다.</p>
                        <button onClick={toggleModal}>닫기</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default FaAnalysis;