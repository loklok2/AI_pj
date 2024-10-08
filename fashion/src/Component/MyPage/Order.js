import React, { useState, useEffect } from 'react';
import '../../CSS/Order.css'; 
import { useParams, useNavigate } from 'react-router-dom';
import { fetchAPI } from '../../hook/api';

const Order = () => {
  const { id: productId } = useParams();
  const navigate = useNavigate();
  
  // 날짜 형식을 원하는 포맷으로 변환하는 함수
  const formatDate = (date) => {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0'); // 두 자리 숫자로 표시
    const day = String(date.getDate()).padStart(2, '0'); // 두 자리 숫자로 표시
    return `${year}.${month}.${day}`;
  };

  // 주문 내역 데이터를 관리하는 상태
  const [orderItems, setOrderItems] = useState([]);
  const [orderInfo, setOrderInfo] = useState({
    orderDate: '',
    orderId: '', // 주문번호 추가
    recipientAddress: '',
    recipientMessage: '',
    recipientName: '',
    recipientPhone: '',
  });
  const handleProductClick = (productId)=>{
    navigate(`/product/${productId}`)
  }
  // sessionStorage에서 주문 데이터를 불러오는 함수
  useEffect(() => {
    const fetchOrderData = async () => {
      try {
        const response = await fetchAPI(`/orders/my-orders/${productId}`, {});
        console.log("order", response);
        setOrderItems(response.orderItems);
        setOrderInfo(response);
      } catch (error) {
        console.error('주문 정보를 가져오는 중 오류 발생:', error);
      }
    };
  
    fetchOrderData();
  }, [productId]);

  return (
    <div className="order-page-container">
      <h2 className="order-page-title">주문 내역</h2>
      <p className="order-page-subtitle">주문한 물건의 주문내역을 확인하세요.</p>

      {/* 주문 상품 목록 */}
      <div className="order-page-items">
        {orderItems.length > 0 ? (
          orderItems.map((item, index) => (
            <div key={index} className="order-page-item" >
              <div className="order-page-item-image-placeholder" >
                {item.pimgPath && item.pimgPath.length > 0 ? (
                  <img
                    src={`${process.env.REACT_APP_URL}${item.pimgPath}`}
                    alt={item.name}
                    className="order-page-item-image"
                    onError={(e) => (e.target.src = '/images/default-placeholder.png')} // 이미지 로드 실패 시 대체 이미지
                  />
                ) : (
                  <img
                    src="/images/default-placeholder.png"
                    alt="Placeholder"
                    className="order-page-item-image"
                  />
                )}
              </div>
              <div className="order-page-item-details">
                <p className="order-page-item-name" onClick={()=>{handleProductClick(item.productId)}}>{item.productName}</p>
                <p className="order-page-item-price">
                  {(item.price * item.quantity).toLocaleString()}원 {/* 수정된 부분 */}
                </p>
                <p className="order-page-item-quantity">수량: {item.quantity}</p>
                <p className="order-page-item-size">사이즈: {item.size}</p> {/* 사이즈 추가 */}
              </div>
            </div>
          ))
        ) : (
          <p>주문 내역이 없습니다.</p>
        )}
      </div>

      {/* 주문 정보 */}
      <div className="order-page-info">
        <h3 className="order-page-info-title">주문 정보</h3>
        <div className="order-page-info-details">
          <p><strong>주문번호:</strong> {orderInfo.orderId}</p> {/* 주문번호 추가 */}
          <p><strong>주문자명:</strong> {orderInfo.recipientName}</p>
          <p><strong>전화번호:</strong> {orderInfo.recipientPhone}</p>
          <p><strong>주소:</strong> {orderInfo.recipientAddress}</p>
          <p><strong>주문일자:</strong> {orderInfo.orderDate}</p>
          <p><strong>주문 요청사항:</strong> {orderInfo.recipientMessage ? orderInfo.recipientMessage : '없음'}</p>
        </div>
      </div>

      {/* 버튼 */}
      <div className="order-page-buttons">
        <button className="order-page-main-button" onClick={() => navigate('/products')}>메인 홈 가기</button>
        <button className="order-page-mypage-button" onClick={() => navigate('/mypage')}>마이페이지</button>
      </div>
    </div>
  );
};

export default Order;
