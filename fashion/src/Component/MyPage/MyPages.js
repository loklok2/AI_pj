import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faUser } from '@fortawesome/free-solid-svg-icons';
import '../../CSS/MyPages.css';  

const MyPages = () => {
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
          <p className="mypages-user-status">회원정보가 없습니다. <span>회원정보 입력</span>을 눌러주세요.</p>
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

      <div className="mypages-order-buttons">
        <button className="mypages-btn">장바구니 목록</button>
        <button className="mypages-btn">주문 내역 목록</button>
      </div>
    </div>
  );
}

export default MyPages;