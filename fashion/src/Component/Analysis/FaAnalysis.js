import React, { useRef, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSpinner, faReplyAll } from '@fortawesome/free-solid-svg-icons';
import { Doughnut } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import '../../CSS/FaAnalysis.css';

ChartJS.register(ArcElement, Tooltip, Legend);

const FaAnalysis = () => {
    const fileInputRef = useRef(null);
    const [loading, setLoading] = useState(false);
    const [chartData, setChartData] = useState(null); // 도넛 차트를 위한 데이터 상태
    const [imageFile, setImageFile] = useState(null); // 업로드된 이미지를 저장할 상태

    const handleClick = () => {
        fileInputRef.current.click();
    };

    const handleImageChange = (e) => {
        setImageFile(e.target.files[0]);
    };

    const handleAnalyzeClick = () => {
        if (!imageFile) {
            alert('이미지를 업로드 해주세요.');
            return;
        }

        setLoading(true);

        const formData = new FormData();
        formData.append('image', imageFile);

        fetch('http://localhost:5000/predict', {
            method: 'POST',
            body: formData
        })
        .then(response => response.json())
        .then(data => {
            setLoading(false);

            // 서버에서 받은 데이터를 차트 데이터로 변환
            const chartLabels = data.predictions.map(prediction => prediction.class);
            const chartDataValues = data.predictions.map(prediction => prediction.probability * 100); // 퍼센트로 변환

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
        window.location.reload(); // 페이지 리로드
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
                        <p>* 여권상의 사진을 권장하지 않습니다. 명확한 사진을 업로드 해주세요.</p>
                        <FontAwesomeIcon 
                            icon={faReplyAll} 
                            onClick={handleReload} 
                            style={{ cursor: 'pointer', marginLeft: '255px' }} 
                        />
                    </div>
                    <div className="fa-analysis-unique-upload-box" onClick={handleClick}>
                        {loading ? (
                            <FontAwesomeIcon icon={faSpinner} spin />
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
                        disabled={loading} // 로딩 중일 때는 버튼 비활성화
                    >
                        {loading ? "분석 중..." : "분석하기"}
                    </button>
                </div>

                {/* 도넛 차트 섹션 */}
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