import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../CSS/ProductDetails.css'; 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as solidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as regularHeart } from '@fortawesome/free-regular-svg-icons';

const productList = [
  { id: 1, name: '로맨틱 핑크 플로럴 러플 블라우스', price: '39,900원', category: '블라우스/남방' },
  // 기타 데이터
];

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate(); // 페이지 이동을 위한 useNavigate 사용
  const product = productList.find(product => product.id === parseInt(id));

  const [liked, setLiked] = useState(false);
  const [quantity, setQuantity] = useState(1); // 수량 상태 관리

  if (!product) {
    return <div>해당 제품을 찾을 수 없습니다.</div>;
  }

  const toggleLike = () => {
    setLiked(!liked);
  };

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1) {
      setQuantity(newQuantity); // 수량이 1 이상인 경우에만 업데이트
    }
  };

  const handleAddToCart = () => {
    // 장바구니 페이지로 이동
    navigate('/cart');
  };

  const handleBuyNow = () => {
    // 결제 페이지로 이동
    navigate('/payment');
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
              style={{ textAlign: 'center' }} // 가운데 정렬
            />
            <button onClick={() => handleQuantityChange(quantity + 1)}>+</button>
          </div>
        </div>
        <select className="product-details-page-size">
          <option>사이즈 선택</option>
          <option value="S">S</option>
          <option value="M">M</option>
          <option value="L">L</option>
          <option value="XL">XL</option>
        </select>
        <div className="product-details-page-buttons">
          <button className="product-details-page-add-to-cart" onClick={handleAddToCart}>
            장바구니 담기
          </button>
          <button className="product-details-page-buy-now" onClick={handleBuyNow}>
            바로 구매하기
          </button>
        </div>
      </div>
    </div>
  );
}

export default ProductDetails;