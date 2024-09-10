import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; 
import '../../CSS/Product.css'; 
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faHeart as solidHeart } from '@fortawesome/free-solid-svg-icons';
import { faHeart as regularHeart } from '@fortawesome/free-regular-svg-icons';

const productList = [
  { id: 1, name: '로맨틱 핑크 플로럴 러플 블라우스', price: '39,900원', category: '블라우스/남방' },
  { id: 2, name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일', price: '29,900원', category: '반팔티' },
  { id: 3, name: '내추럴 크로셰 니트 볼레로', price: '34,900원', category: '니트' },
  { id: 4, name: '스트라이프 셔츠', price: '25,900원', category: '셔츠' },
  { id: 5, name: '클래식 데님 재킷', price: '45,900원', category: '자켓' },
  { id: 6, name: '화이트 레이스 블라우스', price: '29,900원', category: '블라우스/남방' },
  { id: 7, name: '블랙 슬림 슬랙스', price: '39,900원', category: '바지' },
  { id: 8, name: '스카이블루 셔츠', price: '32,900원', category: '셔츠' },
  { id: 9, name: '네이비 니트 스웨터', price: '27,900원', category: '니트' },
  { id: 10, name: '플로럴 프린트 스커트', price: '37,900원', category: '스커트' },
  { id: 11, name: '오버사이즈 브라운 카디건', price: '45,500원', category: '니트' },
  { id: 12, name: '라벤더 레이스 미디 드레스', price: '49,900원', category: '원피스' },
  { id: 13, name: '와이드 핏 데님 팬츠', price: '39,900원', category: '바지' },
  { id: 14, name: '벨트 디테일 레더 자켓', price: '79,900원', category: '자켓' },
  { id: 15, name: '캣프린트 실크 블라우스', price: '41,900원', category: '블라우스/남방' },
  { id: 16, name: '민트 컬러 스윙 스커트', price: '29,900원', category: '스커트' },
  { id: 17, name: '코튼 스트라이프 후디', price: '34,900원', category: '후드티' },
  { id: 18, name: '버건디 롱 슬리브 티셔츠', price: '19,900원', category: '긴팔티' },
  { id: 19, name: '플레어 니트 원피스', price: '53,900원', category: '원피스' },
  { id: 20, name: '슬림 핏 블랙 청바지', price: '44,900원', category: '바지' },
  { id: 21, name: '그레이 울 코트', price: '109,900원', category: '자켓' },
  { id: 22, name: '크림 컬러 하이웨이스트 치마', price: '27,900원', category: '스커트' },
  { id: 23, name: '패턴 니트 머플러', price: '18,900원', category: '악세서리' },
  { id: 24, name: '오렌지 컬러 프린트 티셔츠', price: '21,900원', category: '티셔츠' },
  { id: 25, name: '푸른빛 라운드넥 스웨터', price: '37,900원', category: '니트' },
  { id: 26, name: '퍼프소매 레드 블라우스', price: '42,900원', category: '블라우스/남방' },
  { id: 27, name: '피트니스 레깅스', price: '29,900원', category: '바지' },
  { id: 28, name: '베이지 체크 패턴 재킷', price: '59,900원', category: '자켓' },
  { id: 29, name: '니트 베스트', price: '22,900원', category: '니트' },
  { id: 30, name: '플리츠 미니 스커트', price: '35,900원', category: '스커트' },
  { id: 31, name: '트렌치 코트', price: '89,900원', category: '자켓' },
  { id: 32, name: '와인 컬러 벨벳 자켓', price: '64,900원', category: '자켓' },
  { id: 33, name: '코듀로이 팬츠', price: '39,900원', category: '바지' },
  { id: 34, name: '실크 블랙 스카프', price: '15,900원', category: '악세서리' },
  { id: 35, name: '옐로우 코튼 미니 드레스', price: '47,900원', category: '원피스' },
  { id: 36, name: '그린 컬러 플로럴 프린트 블라우스', price: '39,900원', category: '블라우스/남방' },
  { id: 37, name: '레드 플로럴 롱 스커트', price: '41,900원', category: '스커트' },
  { id: 38, name: '라운드넥 블랙 스웨트셔츠', price: '32,900원', category: '티셔츠' },
  { id: 39, name: '데님 오버롤', price: '49,900원', category: '바지' },
  { id: 40, name: '니트 베이지 터틀넥', price: '43,900원', category: '니트' },
  { id: 41, name: '화이트 스니커즈', price: '55,900원', category: '신발' },
  { id: 42, name: '스웨이드 앵클 부츠', price: '62,900원', category: '신발' },
  { id: 43, name: '그레이 트레이닝 팬츠', price: '29,900원', category: '바지' },
  { id: 44, name: '브이넥 실크 드레스', price: '59,900원', category: '원피스' },
  { id: 45, name: '멀티컬러 니트 가디건', price: '48,900원', category: '니트' },
  { id: 46, name: '블루 슬리브리스 티', price: '22,900원', category: '반팔티' },
  { id: 47, name: '린넨 반바지', price: '26,900원', category: '바지' },
  { id: 48, name: '타이트 핏 레더 팬츠', price: '52,900원', category: '바지' },
  { id: 49, name: '블랙 앵클부츠', price: '65,900원', category: '신발' },
  { id: 50, name: '스포티 트랙자켓', price: '57,900원', category: '자켓' }
];

const itemsPerPage = 25;
const categories = ['전체', '티셔츠', '자켓', '니트', '원피스', '바지', '셔츠', '후드티', '신발'];

const Product = () => {
  const [currentPage, setCurrentPage] = useState(1);
  const [wishlist, setWishlist] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState('전체');
  const [isGuest, setIsGuest] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false); // 로그인 모달 상태 추가
  const navigate = useNavigate();

  useEffect(() => {
    const guestLogin = sessionStorage.getItem('guestLogin') === 'true';
    const userLoggedIn = sessionStorage.getItem('userLoggedIn') === 'true';
    
    if (guestLogin || userLoggedIn) {
      setIsGuest(guestLogin);
      setIsLoggedIn(userLoggedIn);

      const storedWishlist = JSON.parse(sessionStorage.getItem('wishlistItems')) || [];
      setWishlist(storedWishlist);
    }
  }, []);

  const filteredProducts = selectedCategory === '전체'
    ? productList
    : productList.filter(product => product.category === selectedCategory);

  const totalPages = Math.ceil(filteredProducts.length / itemsPerPage);

  const toggleWishlist = (productId) => {
    if (!isGuest && !isLoggedIn) {
      setShowLoginModal(true); // 로그인 모달 표시
      return;
    }

    const updatedWishlist = [...wishlist];
    const index = updatedWishlist.findIndex(item => item.id === productId);

    if (index > -1) {
      updatedWishlist.splice(index, 1); // 이미 찜한 항목이면 제거
    } else {
      const product = productList.find(item => item.id === productId);
      updatedWishlist.push(product); // 찜하지 않은 항목이면 추가
    }

    setWishlist(updatedWishlist);
    sessionStorage.setItem('wishlistItems', JSON.stringify(updatedWishlist));
  };

  const handleNextPage = () => {
    if (currentPage < totalPages) {
      setCurrentPage(currentPage + 1);
    }
  };

  const handlePrevPage = () => {
    if (currentPage > 1) {
      setCurrentPage(currentPage - 1);
    }
  };

  const handleCategorySelect = (category) => {
    setSelectedCategory(category);
    setCurrentPage(1);
  };

  const handleProductClick = (id) => {
    navigate(`/product/${id}`);
  };

  const closeLoginModal = () => {
    setShowLoginModal(false);
    navigate('/login');
  };

  return (
    <div className="product-page">
      <PageTitle title="전체 상품" />
      <CategoryBar categories={categories} selectedCategory={selectedCategory} setSelectedCategory={handleCategorySelect} />

      <div className="product-grid">
        {filteredProducts.slice((currentPage - 1) * itemsPerPage, currentPage * itemsPerPage).map((product) => {
          const liked = wishlist.some(item => item.id === product.id);
          return (
            <div 
              key={product.id} 
              className="product-card" 
              onClick={() => handleProductClick(product.id)} // 제품 클릭 시 상세 페이지로 이동
            >
              <div className="placeholder-box"></div>
              <h2 className="product-name">{product.name}</h2>
              <div className="product-price-wrapper">
                <p className="product-price">{product.price}</p>
                <div className="wishlist-icon" onClick={(e) => { e.stopPropagation(); toggleWishlist(product.id); }}>
                  <FontAwesomeIcon 
                    icon={liked ? solidHeart : regularHeart} 
                    style={{ color: liked ? '#FA5858' : 'black' }} // 좋아요 시 빨간색, 아니면 검은색
                  />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      <div className="pagination">
        <button onClick={handlePrevPage} disabled={currentPage === 1}>이전</button>
        <span>{currentPage} / {totalPages}</span>
        <button onClick={handleNextPage} disabled={currentPage === totalPages}>다음</button>
      </div>

      {/* 로그인 모달 */}
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

// 타이틀 컴포넌트
const PageTitle = ({ title }) => <h1 className="page-title">{title}</h1>;

// 카테고리 바 컴포넌트
const CategoryBar = ({ categories, selectedCategory, setSelectedCategory }) => (
  <div className="category-bar">
    {categories.map((category, index) => (
      <span
        key={index}
        className={`category-item ${selectedCategory === category ? 'active' : ''}`}
        onClick={() => setSelectedCategory(category)}
      >
        {category}
      </span>
    ))}
  </div>
);

export default Product;