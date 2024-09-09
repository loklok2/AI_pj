import React, { useEffect, useState } from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser, faTimes, faAngleLeft, faAngleRight } from '@fortawesome/free-solid-svg-icons';  // X, 좌우 화살표 아이콘 추가
import { useNavigate } from 'react-router-dom';
import '../../CSS/MyPages.css';

const MyPages = () => {
  const navigate = useNavigate();

  const handleCartClick = () => {
    navigate('/cart'); // 장바구니 페이지로 이동
  };

  const handleOrdersClick = () => {
    navigate('/myorder'); // 주문 내역 페이지로 이동
  };

  // 상태 변수로 찜 목록과 장바구니 목록을 관리
  const [wishlistItems, setWishlistItems] = useState([]);
  const [cartItems, setCartItems] = useState([]);

  // 페이지 관리 상태
  const [cartPage, setCartPage] = useState(1);   // 장바구니 페이지 상태
  const [wishlistPage, setWishlistPage] = useState(1); // 찜 목록 페이지 상태
  const itemsPerPage = 5; // 페이지당 표시할 항목 수

  useEffect(() => {
    // sessionStorage에서 찜 목록과 장바구니 목록을 가져옴
    const storedWishlist = JSON.parse(sessionStorage.getItem('wishlistItems')) || [];
    const storedCartItems = JSON.parse(sessionStorage.getItem('cartItems')) || [];
    
    setWishlistItems(storedWishlist);
    setCartItems(storedCartItems);
  }, []);

  // 상품 클릭 시 상세 페이지로 이동하는 함수
  const handleProductClick = (id) => {
    navigate(`/product/${id}`); // 클릭된 상품의 id를 URL로 전달
  };

  // 찜 목록에서 항목을 삭제하는 함수
  const handleRemoveWishlistItem = (id) => {
    const updatedWishlist = wishlistItems.filter(item => item.id !== id);
    setWishlistItems(updatedWishlist);
    sessionStorage.setItem('wishlistItems', JSON.stringify(updatedWishlist));
  };

  // 장바구니 목록에서 항목을 삭제하는 함수
  const handleRemoveCartItem = (id) => {
    const updatedCartItems = cartItems.filter(item => item.id !== id);
    setCartItems(updatedCartItems);
    sessionStorage.setItem('cartItems', JSON.stringify(updatedCartItems));
  };

  // 장바구니 페이지 네비게이션 관련 함수
  const handleNextCartPage = () => {
    setCartPage((prev) => prev + 1);
  };

  const handlePreviousCartPage = () => {
    setCartPage((prev) => (prev > 1 ? prev - 1 : prev));
  };

  // 찜 목록 페이지 네비게이션 관련 함수
  const handleNextWishlistPage = () => {
    setWishlistPage((prev) => prev + 1);
  };

  const handlePreviousWishlistPage = () => {
    setWishlistPage((prev) => (prev > 1 ? prev - 1 : prev));
  };

  // 페이지별로 아이템을 나눔
  const indexOfLastCartItem = cartPage * itemsPerPage;
  const indexOfFirstCartItem = indexOfLastCartItem - itemsPerPage;
  const currentCartItems = cartItems.slice(indexOfFirstCartItem, indexOfLastCartItem);

  const indexOfLastWishlistItem = wishlistPage * itemsPerPage;
  const indexOfFirstWishlistItem = indexOfLastWishlistItem - itemsPerPage;
  const currentWishlistItems = wishlistItems.slice(indexOfFirstWishlistItem, indexOfLastWishlistItem);

  return (
    <div className="mypages-container">
      <h2 className="mypages-title">마이페이지</h2>
      <p className="mypages-subtitle">나의 정보 및 장바구니, 주문 내역을 확인해보세요.</p>

      {/* 사용자 정보 섹션 */}
      <div className="mypages-user-info">
        <div className="mypages-user-avatar">
          <FontAwesomeIcon icon={faUser} size="2x" />
        </div>
        <div className="mypages-user-details">
          <p className="mypages-user-message">저희 사이트에 오신 것을 환영합니다.</p>
          <p className="mypages-user-status"><span>로그인</span> 후 이용 가능합니다.</p>
        </div>
      </div>

      {/* 주문 상태 진행 섹션 */}
      <div className="mypages-section-header">
        <h3 className="mypages-section-title">진행 중인 주문</h3>
        <p className="mypages-section-subtitle">최근 30일 내의 진행 중인 주문 정보가 표시됩니다.</p>
      </div>
      <div className="mypages-section-divider"></div>

      <div className="mypages-order-status">
        <div className="mypages-status-item">
          <div className="mypages-status-icon"></div>
          <p>발송준비</p>
        </div>
        <div className="mypages-status-line"></div> {/* 상태 아이템 간 선 */}
        <div className="mypages-status-item">
          <div className="mypages-status-icon"></div>
          <p>배송시작</p>
        </div>
        <div className="mypages-status-line"></div> {/* 상태 아이템 간 선 */}
        <div className="mypages-status-item">
          <div className="mypages-status-icon"></div>
          <p>배송중</p>
        </div>
        <div className="mypages-status-line"></div> {/* 상태 아이템 간 선 */}
        <div className="mypages-status-item">
          <div className="mypages-status-icon"></div>
          <p>도착예정</p>
        </div>
      </div>

      {/* 장바구니 및 주문 내역 버튼 */}
      <div className="mypages-order-buttons">
        <button className="mypages-btn" onClick={handleCartClick}>
          장바구니 목록 
        </button>
        <button className="mypages-btn" onClick={handleOrdersClick}>
          주문/배송 조회
        </button>
      </div>

      {/* 장바구니 섹션 */}
      <div className="mypages-section-header">
        <h3 className="mypages-section-title">장바구니</h3>
        <p className="mypages-section-subtitle">최근 30일 내의 진행 중인 장바구니 목록입니다.</p>
      </div>
      <div className="mypages-section-divider"></div>
      
      <div className="cart-items-container">
        <FontAwesomeIcon icon={faAngleLeft} onClick={handlePreviousCartPage} className="pagination-arrow left-arrow" />
        
        <div className="mypages-cart-items">
          {currentCartItems.length > 0 ? (
            currentCartItems.map(item => (
              <div key={item.id} className="mypages-item" onClick={() => handleProductClick(item.id)}>
                <div className="mypages-item-placeholder"></div> {/* 이미지 자리 */}
                <div className="mypages-item-details">
                  <p className="mypages-item-name">{item.name}</p>
                  <p className="mypages-item-price">{item.price}원</p>
                </div>
                <FontAwesomeIcon
                  icon={faTimes}
                  className="mypages-item-remove"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemoveCartItem(item.id);
                  }}
                />
              </div>
            ))
          ) : (
            <p>장바구니에 담긴 상품이 없습니다.</p>
          )}
        </div>

        <FontAwesomeIcon icon={faAngleRight} onClick={handleNextCartPage} className="pagination-arrow right-arrow" />
      </div>

      {/* 찜 목록 섹션 */}
      <div className="mypages-section-header">
        <h3 className="mypages-section-title">찜 목록</h3>
        <p className="mypages-section-subtitle">최근 30일 내의 찜 목록입니다.</p>
      </div>
      <div className="mypages-section-divider"></div>
      
      <div className="wishlist-items-container">
        <FontAwesomeIcon icon={faAngleLeft} onClick={handlePreviousWishlistPage} className="pagination-arrow left-arrow" />
        
        <div className="mypages-wishlist-items">
          {currentWishlistItems.length > 0 ? (
            currentWishlistItems.map(item => (
              <div key={item.id} className="mypages-item" onClick={() => handleProductClick(item.id)}>
                <div className="mypages-item-placeholder"></div>
                <div className="mypages-item-details">
                  <p className="mypages-item-name">{item.name}</p>
                  <p className="mypages-item-price">{item.price}원</p>
                </div>
                <FontAwesomeIcon
                  icon={faTimes}
                  className="mypages-item-remove"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleRemoveWishlistItem(item.id);
                  }}
                />
              </div>
            ))
          ) : (
            <p>찜한 상품이 없습니다.</p>
          )}
        </div>

        <FontAwesomeIcon icon={faAngleRight} onClick={handleNextWishlistPage} className="pagination-arrow right-arrow" />
      </div>

      {/* 추천 상품 섹션 */}
      <div className="mypages-section-header">
          <h3 className="mypages-section-title">추천 상품</h3>
          <p className="mypages-section-subtitle">AI 기반으로 추천해주는 상품입니다.</p>
      </div>
      <div className="mypages-section-divider"></div>
      <div className="mypages-recommended-items">
        <div className="mypages-item"></div>
        <div className="mypages-item"></div>
        <div className="mypages-item"></div>
        <div className="mypages-item"></div>
        <div className="mypages-item"></div>
      </div>
    </div>
  );
};

export default MyPages;