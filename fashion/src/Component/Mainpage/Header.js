import React from 'react';
import { Link } from 'react-router-dom';
import '../../CSS/Header.css';

const Header = () => {
  return (
    <div className="header">
      <div className="header-left">
        <h1 className="logo">
          <Link to="/" style={{ textDecoration: 'none', color: 'inherit' }}>
            TREND FLOW
          </Link>
        </h1>
        <nav className="sub-menu">
          <ul>
            <li><Link to="/products">전체상품</Link></li>
            <li><Link to="/analysis">패션 분석</Link></li>
          </ul>
        </nav>
      </div>
      <nav className="header-right">
        <ul>
          <li><Link to="/login">로그인</Link></li>
          <li><Link to="/signup">회원가입</Link></li> 
          <li><Link to="/qna">Q&A</Link></li>
          <li><Link to="/mypage">마이쇼핑</Link></li>
          <li><Link to="/cart">장바구니</Link></li>
        </ul>
      </nav>
    </div>
  );
}

export default Header;