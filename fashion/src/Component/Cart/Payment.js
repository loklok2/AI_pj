import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Payment.css';

const Payment = () => {
  const navigate = useNavigate();

  // 주문 상품 목록 상태 관리 (sessionStorage에서 선택된 상품 가져오기)
  const [orderItems, setOrderItems] = useState([]);

  // 주소 상태 관리
  const [address, setAddress] = useState('서울특별시 강남구 테헤란로 123, 5층');

  // sessionStorage에서 선택된 상품 데이터를 가져와서 주문 목록에 저장
  useEffect(() => {
    const selectedItems = JSON.parse(sessionStorage.getItem('selectedItems')) || [];
    setOrderItems(selectedItems);
  }, []);

  // 결제 완료 페이지로 이동하는 함수
  const handlePaymentSubmit = () => {
    const totalPrice = calculateTotalPrice(); // 총 결제 금액 계산 함수
    sessionStorage.setItem('totalPrice', totalPrice); // 총 결제 금액을 sessionStorage에 저장
    navigate('/paycompleted');
  };

  // 장바구니 페이지로 이동하는 함수
  const handleCartClick = () => {
    navigate('/cart');
  };

  // 새로운 주소 입력 버튼을 클릭했을 때 주소 초기화
  const handleNewAddress = () => {
    setAddress('');
  };

  // 상품 삭제 함수
  const handleRemoveItem = (itemId) => {
    const updatedItems = orderItems.filter(item => item.id !== itemId);
    setOrderItems(updatedItems);
    sessionStorage.setItem('selectedItems', JSON.stringify(updatedItems)); // sessionStorage 업데이트
  };

  // 총 결제 금액 계산 함수
  const calculateTotalPrice = () => {
    return orderItems.reduce((acc, item) => {
      const price = parseInt(item.price.replace(/,/g, '')) || 0; // 가격에서 쉼표 제거 후 숫자로 변환
      return acc + (price * item.quantity);
    }, 0);
  };

  return (
    <div className="payment-page-container">
      <h1 className="payment-page-title">주문/결제</h1>
      <p className="payment-page-subtitle">주문상품 및 결제정보를 확인해주세요.</p>

      {/* 주문 상품 정보 */}
      <div className="payment-order-info">
        <h2>주문 상품정보</h2>
        {orderItems.length > 0 ? (
          orderItems.map((item) => (
            <div key={item.id} className="payment-order-item">
              <div className="payment-order-item-image-placeholder">
                {/* 실제 이미지가 있을 경우 <img> 태그로 대체 */}
              </div>
              <div className="payment-order-item-details">
                <p className="payment-order-item-name">{item.name}</p>
                <p className="payment-order-item-price">
                  {/* 상품 가격 * 수량 */}
                  {(parseInt(item.price.replace(/,/g, '')) * item.quantity).toLocaleString()}원
                </p>
                <p className="payment-order-item-quantity">
                  수량: {item.quantity}개
                </p>
              </div>
              {/* X 버튼 */}
              <button className="remove-item-btn" onClick={() => handleRemoveItem(item.id)}>
                x
              </button>
            </div>
          ))
        ) : (
          <p>주문하신 상품이 없습니다.</p>
        )}
      </div>

      {/* 배송지 정보 */}
      <div className="payment-delivery-info">
        <h2>배송지 정보</h2>
        <input
          type="text"
          className="payment-input-fields"
          placeholder="주소를 입력하세요.(상세 주소까지 입력해주세요)"
          value={address}
          onChange={(e) => setAddress(e.target.value)}
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
          <span>{calculateTotalPrice().toLocaleString()}원</span>
        </div>
        <div className="payment-summary-item">
          <span>배송비</span>
          <span>무료 배송</span>
        </div>
        <div className="payment-summary-total">
          <span>총 결제 금액</span>
          <span>{calculateTotalPrice().toLocaleString()}원</span>
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