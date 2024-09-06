import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom'; 
import '../../CSS/PayCompleted.css'; 

const PayCompleted = () => {
  const navigate = useNavigate(); 

  const [orderNumber, setOrderNumber] = useState('');
  const [totalPrice, setTotalPrice] = useState(0);

  // 주문번호를 랜덤하게 생성하는 함수
  const generateOrderNumber = () => {
    return Math.floor(10000 + Math.random() * 90000).toString(); // 5자리 랜덤 숫자 생성
  };

  useEffect(() => {
    // 주문번호 생성
    const newOrderNumber = generateOrderNumber();
    setOrderNumber(newOrderNumber);

    // 총 결제 금액을 sessionStorage에서 가져옴
    const storedTotalPrice = sessionStorage.getItem('totalPrice');
    if (storedTotalPrice) {
      setTotalPrice(parseInt(storedTotalPrice).toLocaleString()); // 쉼표 추가
    }
  }, []);

  const handleOrderCheck = () => {
    navigate('/order'); 
  };

  return (
    <div className="pay-comp-container">
      <h2 className="pay-comp-title">구매가 정상적으로 완료되었습니다.</h2>

      <div className="pay-comp-summary">
        <div className="pay-comp-order-number">
          <p>주문번호</p>
          <p className="pay-comp-order-value">{orderNumber}</p>
        </div>
        <div className="pay-comp-total-price">
          <p>총 결제 금액</p>
          <p className="pay-comp-price-value">{totalPrice}원</p>
        </div>
      </div>

      <p className="pay-comp-info">
        현재 주문한 상품의 정보는 <strong>{'['}마이페이지 {'>'} 주문 내역 확인하기{']'}</strong>에서
        상세한 주문 내역을<br />다시 확인 할 수 있습니다.
      </p>

      <div className="pay-comp-buttons">
        <button className="pay-comp-main-page-btn">메인 홈 가기</button>
        <button className="pay-comp-order-check-btn" onClick={handleOrderCheck}>
          주문 내역 확인하기
        </button>
      </div>
    </div>
  );
};

export default PayCompleted;