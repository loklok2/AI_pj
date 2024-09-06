import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Payment.css';

const Payment = () => {
  const navigate = useNavigate(); 
  const handlePaymentSubmit = () => {
    navigate('/paycompleted');
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
        <input type="text" className="payment-input-fields" placeholder="상세 주소를 입력하세요." />
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
          <div className="payment-method-option">페이팔</div>
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
        <button className="payment-cancel-btn">장바구니</button>
        <button className="payment-submit-btn" onClick={handlePaymentSubmit}>
          결제하기
        </button>
      </div>
    </div>
  );
};

export default Payment;