import React from 'react';
import '../../CSS/Order.css'; 
import { useNavigate } from 'react-router-dom';

const Order = () => {
  const navigate = useNavigate(); 

  return (
    <div className="order-page-container">
      <h2 className="order-page-title">주문 내역</h2>
      <p className="order-page-subtitle">주문한 물건의 주문내역을 확인하세요.</p>

      <div className="order-page-items">
        {[1, 2, 3, 4].map((item, index) => (
          <div key={index} className="order-page-item">
            <div className="order-page-item-shape"></div>
            <div className="order-page-item-details">
              <p className="order-page-item-name">일레갈스 블랙 튜탑 with 핑크 리본 디테일</p>
              <p className="order-page-item-sub">실사이즈 블랙 탑 핑크 리본 (170)</p>
              <p className="order-page-item-price">29,900원</p>
            </div>
          </div>
        ))}
      </div>

      <div className="order-page-info">
        <h3 className="order-page-info-title">주문 정보</h3>
        <div className="order-page-info-details">
          <p><strong>아이디:</strong> hong</p>
          <p><strong>전화번호:</strong> 010-1234-5698</p>
          <p><strong>주소:</strong> 부산광역시 남구 진포대로 72-8(문현동)</p>
          <p><strong>주문일자:</strong> 2024.03.01</p>
          <p><strong>주문 요청사항:</strong> 문 앞에 두고 가주세요.</p>
        </div>
      </div>

      <div className="order-page-buttons">
      <button className="order-page-main-button" onClick={() => navigate('/')}>메인 홈 가기
        </button>
        <button className="order-page-mypage-button" onClick={() => navigate('/mypage')}>마이페이지
        </button>
      </div>
    </div>
  );
};

export default Order;