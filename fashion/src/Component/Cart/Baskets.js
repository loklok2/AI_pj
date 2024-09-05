import React from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate 훅 임포트
import '../../CSS/Baskets.css'; 

const Baskets = () => {
  const navigate = useNavigate(); // useNavigate 훅 초기화

  const items = [
    {
      id: 1,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
    },
    {
      id: 2,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
    },
    {
      id: 3,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
    },
  ];

  const handlePayment = () => {
    navigate('/payment'); // 결제 페이지로 이동
  };

  return (
    <div className="basket-container">
      <h1 className="basket-title">장바구니</h1>
      <p className="basket-description">
        판매자 설정에 따라, 개별 배송되는 상품이 있습니다.
      </p>

      <div className="basket-selection">
        <input type="checkbox" id="select-all" />
        <label htmlFor="select-all">전체 선택</label>
      </div>

      {items.map((item) => (
        <div key={item.id} className="basket-item">
          <div className="basket-item-checkbox">
            <input type="checkbox" />
          </div>
          <div className="basket-item-image">
            <div className="placeholder-image"></div>
          </div>
          <div className="basket-item-info">
            <p>{item.name}</p>
            <p className="basket-item-price">{item.price.toLocaleString()}원</p>
          </div>
          <div className="basket-item-controls">
            <button className="quantity-btn">-</button>
            <span className="quantity-number">1</span>
            <button className="quantity-btn">+</button>
          </div>
          <div className="basket-item-total">
            <p>총 {item.price.toLocaleString()}원</p>
          </div>
          <button className="delete-btn">x</button>
        </div>
      ))}

      <div className="basket-actions">
        <button className="delete-all-btn">장바구니 삭제</button>
        <button className="purchase-btn" onClick={handlePayment}>결제하기</button> {/* 결제 버튼 클릭 시 handlePayment 호출 */}
      </div>
    </div>
  );
};

export default Baskets;