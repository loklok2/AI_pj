import React, { useRef, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner, faRotateRight, faCircleXmark } from '@fortawesome/free-solid-svg-icons'; 
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import '../../CSS/FaAnalysis.css';  // CSS 파일 임포트

ChartJS.register(ArcElement, Tooltip, Legend);

const FaAnalysis = () => {
    const fileInputRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const [chartData, setChartData] = useState(null);
    const [imageFile, setImageFile] = useState(null);
    const [previewUrl, setPreviewUrl] = useState(null);

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

        const formData = new FormData();
        formData.append('image', imageFile);

        fetch('http://localhost:5000/style_predict', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            setLoading(false);

            const chartLabels = data.predictions.map(prediction => prediction[0]);
            const chartDataValues = data.predictions.map(prediction => prediction[1] * 100);

            setChartData({
                labels: chartLabels,
                datasets: [
                    {
                        label: 'Probability',
                        data: chartDataValues,
                        backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
                        hoverBackgroundColor: ['#FF6384', '#36A2EB', '#FFCE56'],
                    },
                ],
            });
        })
        .catch(error => {
            setLoading(false);
            console.error('Error:', error);
        });
    };

    const handleReload = () => {
        window.location.reload();
    };

    const handleRemoveImage = (e) => {
        e.stopPropagation(); // 이벤트 전파를 막아 부모 요소의 onClick 이벤트가 발생하지 않도록 합니다.
        setImageFile(null);
        setPreviewUrl(null);
        if (fileInputRef.current) {
            fileInputRef.current.value = null; // 파일 입력 값을 초기화합니다.
        }
    };

    return (
        <div className="fa-analysis-unique-container">
            <div className="fa-analysis-unique-header">
                <h1>옷 이미지 분석</h1>
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
                        <p>* 명확한 사진을 업로드 해주세요.</p>
                        <FontAwesomeIcon 
                            icon={faRotateRight}  
                            onClick={handleReload} 
                            style={{ cursor: 'pointer', marginLeft: '430px', marginTop:'-15px'}} 
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

                {chartData && (
                    <div className="fa-analysis-unique-description-section">
                        <div style={{ width: '400px', margin: '15px auto 0 auto' }}>
                            <Doughnut data={chartData} />
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}

export default FaAnalysis;