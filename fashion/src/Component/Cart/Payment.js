import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Payment.css';

const Payment = () => {
  const navigate = useNavigate();
  
  // 목업 데이터를 기본 주소로 설정
  const [address, setAddress] = useState('서울특별시 강남구 테헤란로 123, 5층');

  // 결제 완료 페이지로 이동하는 함수
  const handlePaymentSubmit = () => {
    navigate('/paycompleted');
  };

  // 장바구니 페이지로 이동하는 함수
  const handleCartClick = () => {
    navigate('/cart');
  };

  // 새로운 주소 입력 버튼을 클릭했을 때 주소 초기화
  const handleNewAddress = () => {
    setAddress(''); // 주소 필드를 비움
  };

  return (
    <div className="payment-page-container">
      <h1 className="payment-page-title">주문/결제</h1>
      <p className="payment-page-subtitle">주문상품 및 결제정보를 확인해주세요.</p>

      {/* 주문 상품 정보 */}
      <div className="payment-order-info">
        <h2>주문 상품정보</h2>
        <div className="payment-order-item">
          <div className="payment-order-item-image-placeholder"></div>
          <div className="payment-order-item-details">
            <p className="payment-order-item-name">엘레강스 블랙 튜브탑 with 핑크 리본 디테일</p>
            <p className="payment-order-item-price">29,900원</p>
          </div>
        </div>
      </div>

      {/* 배송지 정보 */}
      <div className="payment-delivery-info">
        <h2>배송지 정보</h2>
        <input
          type="text"
          className="payment-input-fields"
          placeholder="주소를 입력하세요.(상세 주소까지 입력해주세요)"
          value={address} // 목업된 주소가 입력된 상태로 표시
          onChange={(e) => setAddress(e.target.value)} // 사용자가 주소를 수정할 수 있도록 설정
        />
        <button className="add-address-btn" onClick={handleNewAddress}>새로운 주소 입력</button>
        <hr className="divider-line" />
      </div>

      {/* 주문 요청 사항 */}
      <div className="payment-request-info">
        <h2>주문 요청 사항</h2>
        <input type="text" className="payment-input-field" placeholder="요청 사항을 입력하세요." />
        <select className="payment-input-fields">
          <option value="">요청 사항을 선택하세요.</option>
          <option value="문앞">문앞</option>
          <option value="경비실">경비실</option>
        </select>
      </div>

      {/* 결제 수단 */}
      <div className="payment-method-info">
        <h2>결제 수단</h2>
        <div className="payment-method-options">
          <div className="payment-method-option">신용카드</div>
          <div className="payment-method-option">가상계좌</div>
          <div className="payment-method-option">휴대폰 결제</div>
          <div className="payment-method-option">토스 페이</div>
          <div className="payment-method-option">카카오 페이</div>
        </div>
      </div>

      {/* 최종 결제 금액 */}
      <div className="payment-summary-info">
        <div className="payment-summary-item">
          <span>상품 가격</span>
          <span>29,900원</span>
        </div>
        <div className="payment-summary-item">
          <span>배송비</span>
          <span>무료 배송</span>
        </div>
        <div className="payment-summary-total">
          <span>총 결제 금액</span>
          <span>29,900원</span>
        </div>
      </div>

      {/* 버튼 */}
      <div className="payment-buttons">
        <button className="payment-cancel-btn" onClick={handleCartClick}>장바구니</button>
        <button className="payment-submit-btn" onClick={handlePaymentSubmit}>
          결제하기
        </button>
      </div>
    </div>
  );
};

export default Payment;