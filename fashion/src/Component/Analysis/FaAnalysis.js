import React, { useRef } from 'react';
import '../../CSS/FaAnalysis.css';

const FaAnalysis = () => {
    const fileInputRef = useRef(null);

    const handleClick = () => {
        fileInputRef.current.click();
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
                    <p>* 여권상의 사진을 권장하지 않습니다. 명확한 사진을 업로드 해주세요.</p>
                    <div className="fa-analysis-unique-upload-box" onClick={handleClick}>
                        <p>클릭하여 이미지를 업로드</p>
                        <input 
                            type="file" 
                            ref={fileInputRef} 
                            style={{ display: 'none' }} 
                            onChange={(e) => console.log(e.target.files[0])}
                        />
                    </div>
                    <button className="fa-analysis-unique-analyze-button">분석하기</button>
                </div>

                <div className="fa-analysis-unique-description-section">
                    <p>본 분석 결과는 어떤 방식으로 해서 어떤 데이터를 사용해서 나오는 결과임을 알려드립니다.</p>
                </div>
            </div>
        </div>
    );
}

export default FaAnalysis;