import React, { useState, useEffect } from 'react';
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
  { id: 6, name: '화이트 레이스 블라우스', price: '29,900', category: '블라우스/남방' },
  { id: 7, name: '블랙 슬림 슬랙스', price: '39,900', category: '바지' },
  { id: 8, name: '스카이블루 셔츠', price: '32,900', category: '셔츠' },
  { id: 9, name: '네이비 니트 스웨터', price: '27,900', category: '니트' },
  { id: 10, name: '플로럴 프린트 스커트', price: '37,900', category: '스커트' },
  { id: 11, name: '오버사이즈 브라운 카디건', price: '45,500', category: '니트' },
  { id: 12, name: '라벤더 레이스 미디 드레스', price: '49,900', category: '원피스' },
  { id: 13, name: '와이드 핏 데님 팬츠', price: '39,900', category: '바지' },
  { id: 14, name: '벨트 디테일 레더 자켓', price: '79,900', category: '자켓' },
  { id: 15, name: '캣프린트 실크 블라우스', price: '41,900', category: '블라우스/남방' },
  { id: 16, name: '민트 컬러 스윙 스커트', price: '29,900', category: '스커트' },
  { id: 17, name: '코튼 스트라이프 후디', price: '34,900', category: '후드티' },
  { id: 18, name: '버건디 롱 슬리브 티셔츠', price: '19,900', category: '긴팔티' },
  { id: 19, name: '플레어 니트 원피스', price: '53,900', category: '원피스' },
  { id: 20, name: '슬림 핏 블랙 청바지', price: '44,900', category: '바지' },
  { id: 21, name: '그레이 울 코트', price: '109,900', category: '자켓' },
  { id: 22, name: '크림 컬러 하이웨이스트 치마', price: '27,900', category: '스커트' },
  { id: 23, name: '패턴 니트 머플러', price: '18,900', category: '악세서리' },
  { id: 24, name: '오렌지 컬러 프린트 티셔츠', price: '21,900', category: '티셔츠' },
  { id: 25, name: '푸른빛 라운드넥 스웨터', price: '37,900', category: '니트' },
  { id: 26, name: '퍼프소매 레드 블라우스', price: '42,900', category: '블라우스/남방' },
  { id: 27, name: '피트니스 레깅스', price: '29,900', category: '바지' },
  { id: 28, name: '베이지 체크 패턴 재킷', price: '59,900', category: '자켓' },
  { id: 29, name: '니트 베스트', price: '22,900', category: '니트' },
  { id: 30, name: '플리츠 미니 스커트', price: '35,900', category: '스커트' },
  { id: 31, name: '트렌치 코트', price: '89,900', category: '자켓' },
  { id: 32, name: '와인 컬러 벨벳 자켓', price: '64,900', category: '자켓' },
  { id: 33, name: '코듀로이 팬츠', price: '39,900', category: '바지' },
  { id: 34, name: '실크 블랙 스카프', price: '15,900', category: '악세서리' },
  { id: 35, name: '옐로우 코튼 미니 드레스', price: '47,900', category: '원피스' },
  { id: 36, name: '그린 컬러 플로럴 프린트 블라우스', price: '39,900', category: '블라우스/남방' },
  { id: 37, name: '레드 플로럴 롱 스커트', price: '41,900', category: '스커트' },
  { id: 38, name: '라운드넥 블랙 스웨트셔츠', price: '32,900', category: '티셔츠' },
  { id: 39, name: '데님 오버롤', price: '49,900', category: '바지' },
  { id: 40, name: '니트 베이지 터틀넥', price: '43,900', category: '니트' },
  { id: 41, name: '화이트 스니커즈', price: '55,900', category: '신발' },
  { id: 42, name: '스웨이드 앵클 부츠', price: '62,900', category: '신발' },
  { id: 43, name: '그레이 트레이닝 팬츠', price: '29,900', category: '바지' },
  { id: 44, name: '브이넥 실크 드레스', price: '59,900', category: '원피스' },
  { id: 45, name: '멀티컬러 니트 가디건', price: '48,900', category: '니트' },
  { id: 46, name: '블루 슬리브리스 티', price: '22,900', category: '반팔티' },
  { id: 47, name: '린넨 반바지', price: '26,900', category: '바지' },
  { id: 48, name: '타이트 핏 레더 팬츠', price: '52,900', category: '바지' },
  { id: 49, name: '블랙 앵클부츠', price: '65,900', category: '신발' },
  { id: 50, name: '스포티 트랙자켓', price: '57,900', category: '자켓' }
];

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const product = productList.find(product => product.id === parseInt(id));

  const [liked, setLiked] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [size, setSize] = useState('S');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const [isGuest, setIsGuest] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const guestLogin = sessionStorage.getItem('guestLogin') === 'true';
    const userLoggedIn = sessionStorage.getItem('userLoggedIn') === 'true';

    if (guestLogin) setIsGuest(true);
    if (userLoggedIn) setIsLoggedIn(true);

    const storedWishlist = JSON.parse(sessionStorage.getItem('wishlistItems')) || [];
    const isLiked = storedWishlist.some(item => item.id === product.id);
    setLiked(isLiked);
  }, [product.id]);

  if (!product) {
    return <div>해당 제품을 찾을 수 없습니다.</div>;
  }

  const toggleLike = (e) => {
    e.stopPropagation(); // 부모 컴포넌트로의 이벤트 전파를 막음
    if (!isGuest && !isLoggedIn) {
      setShowLoginModal(true); // 로그인 모달을 표시
      return;
    }
  
    const storedWishlist = JSON.parse(sessionStorage.getItem('wishlistItems')) || [];
    if (liked) {
      const updatedWishlist = storedWishlist.filter(item => item.id !== product.id);
      sessionStorage.setItem('wishlistItems', JSON.stringify(updatedWishlist));
    } else {
      storedWishlist.push(product);
      sessionStorage.setItem('wishlistItems', JSON.stringify(storedWishlist));
    }
    setLiked(!liked);
  };

  const handleQuantityChange = (newQuantity) => {
    if (newQuantity >= 1) {
      setQuantity(newQuantity);
    }
  };

  const handleAddToCart = () => {
    if (!isGuest && !isLoggedIn) {
      setShowLoginModal(true); // 로그인 모달을 표시
      return;
    }

    const cartItems = JSON.parse(sessionStorage.getItem('cartItems')) || [];
    const existingItem = cartItems.find(item => item.id === product.id);
    
    if (existingItem) {
      existingItem.quantity += quantity;
      existingItem.size = size;
    } else {
      cartItems.push({ ...product, quantity, size });
    }

    sessionStorage.setItem('cartItems', JSON.stringify(cartItems));
    setIsModalOpen(true);
  };

  const handleBuyNow = () => {
    if (!isGuest && !isLoggedIn) {
      setShowLoginModal(true);
      return;
    }

    const selectedItem = [{ ...product, quantity, size }];
    sessionStorage.setItem('selectedItems', JSON.stringify(selectedItem));
    navigate('/payment'); 
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
  };

  const handleConfirmModal = () => {
    navigate('/cart');
  };

  const closeLoginModal = () => {
    setShowLoginModal(false);
    navigate('/login'); // 로그인 페이지로 이동
  };

  return (
    <div className="product-details-wrapper">
      <div className="product-details-page">
        <div className="product-details-page-image">
          <div className="product-details-page-placeholder-image"></div>
        </div>
        <div className="product-details-page-info">
          <div className="product-title">
            <h2>{product.name}</h2>
            <div className="wishlist-icon" onClick={toggleLike}>
              <FontAwesomeIcon icon={liked ? solidHeart : regularHeart} 
              style={{ color: liked ? '#FA5858' : 'black' }} />
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
  
          <div className="product-details-page-size">
            <span>사이즈 </span>
            <select value={size} onChange={(e) => setSize(e.target.value)}>
              <option value="S">S</option>
              <option value="M">M</option>
              <option value="L">L</option>
              <option value="XL">XL</option>
              <option value="FREE">FREE</option>
            </select>
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
            <button className="product-details-page-buy-now" onClick={handleBuyNow}>
              바로 구매하기
            </button>
          </div>
        </div>
      </div>
  
      {/* 상품정보 섹션을 페이지 상단에 더 가깝게 배치 */}
      <div className="product-details-page-additional-infos">
        <hr />
        <p><strong>상품정보</strong></p>
        <p>상품번호: {product.id}</p>
        <p>상품등록일: 20140612</p>
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
  
      {showLoginModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <p>로그인 또는 비회원 로그인이 필요합니다.</p>
            <button onClick={closeLoginModal}>확인</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductDetails;