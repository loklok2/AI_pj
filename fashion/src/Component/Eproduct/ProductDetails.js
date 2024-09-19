import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import '../../CSS/ProductDetails.css'; 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as solidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as regularHeart } from '@fortawesome/free-regular-svg-icons';

const ProductDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [product, setProduct] = useState(null);
  const [liked, setLiked] = useState(false);
  const [quantity, setQuantity] = useState(1);
  const [size, setSize] = useState('S');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  const [isGuest, setIsGuest] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);

  useEffect(() => {
    const fetchProductDetails = async () => {
      try {
        const response = await fetch(`http://10.125.121.188:8080/api/products/${id}`);
        if (!response.ok) {
          throw new Error('상품 정보를 가져오는데 실패했습니다');
        }
        const data = await response.json();
        setProduct(data); // 상품 정보를 상태에 저장

        // 찜 목록에 있는지 확인
        const storedWishlist = JSON.parse(sessionStorage.getItem('wishlistItems')) || [];
        const isLiked = storedWishlist.some(item => item.productId === data.productId);
        setLiked(isLiked);
      } catch (error) {
        console.error('상품 정보를 가져오는데 실패했습니다:', error);
      }
    };

    fetchProductDetails();

    const guestLogin = sessionStorage.getItem('guestLogin') === 'true';
    const userLoggedIn = sessionStorage.getItem('userLoggedIn') === 'true';

    if (guestLogin) setIsGuest(true);
    if (userLoggedIn) setIsLoggedIn(true);
  }, [id]);

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
      const updatedWishlist = storedWishlist.filter(item => item.productId !== product.productId);
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
    const existingItem = cartItems.find(item => item.productId === product.productId);
    
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

  // 이미지 URL 생성
  const imageUrl = product.images && product.images.length > 0
    ? `http://10.125.121.188:8080${product.images[0]}` // 이미지가 있으면 첫 번째 이미지 사용
    : 'default-image-path.jpg'; // 기본 이미지 경로 설정 (없을 경우)

  return (
    <div className="product-details-wrapper">
      <div className="product-details-page">
        <div className="product-details-page-image">
          <img src={imageUrl} alt={product.name} className="product-image" />
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
            <strong>{product.price.toLocaleString()}</strong>
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
  
      {/* 상품정보 */}
      <div className="product-details-page-additional-infos">
        <hr />
        <p><strong>상품정보</strong></p>
        <p>상품번호: {product.productId}</p>
        <p>상품등록일: 20140612</p>
      </div>

      {/* 배송 정보 */}
      <div className="product-details-page-additional-infos">
        <hr />
        <p><strong>배송 정보</strong></p>
        <p>천재지변, 택배사 사정 등으로 인한 배송 지연 시 개별 안내 드립니다.</p>
        <p>배송 중 상품이 손상되었거나 분실된 경우, 고객센터로 즉시 연락해 주시기 바랍니다.</p>
        <p>제주도 및 도서산간 지역도 추가 배송비 없이 배송됩니다. 단, 배송이 1-2일 추가 소요될 수 있습니다.</p>
      </div>

      {/* 교환 및 반품 안내 */}
      <div className="product-details-page-additional-infoss">
        <hr />
        <p><strong>교환 및 반품 안내</strong></p>
        <p>배송 받은 날로부터 7일 이내에 교환 및 반품 가능 (단, 미사용 상태 및 태그가 부착된 상태이어야 함)</p>
        <p>제품 불량이나 오배송의 경우, 교환 및 반품 배송비는 판매자가 부담합니다.</p>
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