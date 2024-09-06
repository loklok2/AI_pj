import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../CSS/ProductDetails.css'; 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as solidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as regularHeart } from '@fortawesome/free-regular-svg-icons';

const productList = [
  { id: 1, name: '로맨틱 핑크 플로럴 러플 블라우스', price: '39,900', category: '블라우스/남방' },
  { id: 2, name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일', price: '29,900', category: '반팔티' },
  { id: 3, name: '내추럴 크로셰 니트 볼레로', price: '34,900', category: '니트' },
  { id: 4, name: '스트라이프 셔츠', price: '25,900', category: '셔츠' },
  { id: 5, name: '클래식 데님 재킷', price: '45,900', category: '자켓' },
];

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const product = productList.find(product => product.id === parseInt(id));

  const [liked, setLiked] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [isModalOpen, setIsModalOpen] = useState(false);

  if (!product) {
    return <div>해당 제품을 찾을 수 없습니다.</div>;
  }

  const toggleLike = () => {
    setLiked(!liked);
  };

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    // 현재 장바구니 항목을 sessionStorage에서 가져오기 (없으면 빈 배열)
    const cartItems = JSON.parse(sessionStorage.getItem('cartItems')) || [];

    // 이미 장바구니에 있는 항목인지 확인
    const existingItem = cartItems.find(item => item.id === product.id);
    
    if (existingItem) {
      // 이미 있으면 수량만 증가
      existingItem.quantity += quantity;
    } else {
      // 없으면 새 항목 추가
      cartItems.push({ ...product, quantity });
    }

    // 업데이트된 장바구니를 sessionStorage에 저장
    sessionStorage.setItem('cartItems', JSON.stringify(cartItems));

    setIsModalOpen(true); // 모달 열기
  };

  const handleCloseModal = () => {
    setIsModalOpen(false); // 모달 닫기
  };

  const handleConfirmModal = () => {
    navigate('/cart'); // 장바구니 페이지로 이동
  };

  return (
    <div className="product-details-page">
      <div className="product-details-page-image">
        <div className="product-details-page-placeholder-image"></div>
      </div>
      <div className="product-details-page-info">
        <div className="product-title">
          <h2>{product.name}</h2>
          <div className="wishlist-icon" onClick={toggleLike}>
            <FontAwesomeIcon icon={liked ? solidHeart : regularHeart} />
          </div>
        </div>
        <div className="product-details-page-price">
          <span>가격 </span>
          <strong>{product.price}</strong>
          <span>원</span>
        </div>
        <div className="product-details-page-shipping">
          <p>배송 정보: <span>3일 이내 출고</span></p>
          <p>배송비: <span>무료배송</span></p>
        </div>
        <div className="product-details-page-quantity">
          <span>수량</span>
          <div className="product-details-page-quantity-controls">
            <button onClick={() => handleQuantityChange(quantity - 1)}>-</button>
            <input
              type="number"
              value={quantity}
              min="1"
              onChange={(e) => handleQuantityChange(parseInt(e.target.value))}
              style={{ textAlign: 'center' }} 
            />
            <button onClick={() => handleQuantityChange(quantity + 1)}>+</button>
          </div>
        </div>
        <div className="product-details-page-buttons">
          <button className="product-details-page-add-to-cart" onClick={handleAddToCart}>
            장바구니 담기
          </button>
          <button className="product-details-page-buy-now" onClick={() => navigate('/payment')}>
            바로 구매하기
          </button>
        </div>
      </div>

      {isModalOpen && (
        <div className="modal">
          <div className="modal-content">
            <p>상품이 장바구니에 성공적으로 담겼습니다.</p>
            <div className="modal-buttons">
              <button onClick={handleCloseModal}>닫기</button>
              <button onClick={handleConfirmModal}>확인</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductDetails;