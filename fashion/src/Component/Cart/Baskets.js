import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate 훅 임포트
import '../../CSS/Baskets.css'; 

const Baskets = () => {
  const navigate = useNavigate(); // useNavigate 훅 초기화

  // 장바구니 항목 상태 관리
  const [items, setItems] = useState([
    {
      id: 1,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
      quantity: 1, // 수량 추가
      isSelected: false, // 각 항목의 선택 상태
    },
    {
      id: 2,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
      quantity: 1, // 수량 추가
      isSelected: false, // 각 항목의 선택 상태
    },
    {
      id: 3,
      name: '엘레강스 블랙 튜브탑 with 핑크 리본 디테일',
      price: 29900,
      quantity: 1, // 수량 추가
      isSelected: false, // 각 항목의 선택 상태
    },
  ]);

  const [selectAll, setSelectAll] = useState(false); // 전체 선택 상태

  // 선택된 항목들의 총 가격을 계산하는 함수
  const totalPrice = items
    .filter(item => item.isSelected) // 선택된 항목만 필터링
    .reduce((acc, item) => acc + (item.price * item.quantity), 0); // 선택된 항목들의 가격 * 수량 합산

  const handlePayment = () => {
    navigate('/payment'); // 결제 페이지로 이동
  };

  // 전체 선택/해제 처리 함수
  const handleSelectAll = () => {
    const newSelectAll = !selectAll;
    setSelectAll(newSelectAll);
    setItems(items.map(item => ({ ...item, isSelected: newSelectAll })));
  };

  // 개별 항목 선택/해제 처리 함수
  const handleSelectItem = (id) => {
    setItems(
      items.map(item =>
        item.id === id ? { ...item, isSelected: !item.isSelected } : item
      )
    );
  };

  // 선택된 항목만 삭제 처리 함수
  const handleDeleteSelected = () => {
    const remainingItems = items.filter(item => !item.isSelected); // 선택되지 않은 항목만 필터링
    setItems(remainingItems); // 선택된 항목 삭제 후 상태 업데이트
  };

  // 수량 증가 함수
  const handleIncreaseQuantity = (id) => {
    setItems(items.map(item => 
      item.id === id ? { ...item, quantity: item.quantity + 1 } : item
    ));
  };

  // 수량 감소 함수 (수량이 1 이하로는 줄지 않도록 처리)
  const handleDecreaseQuantity = (id) => {
    setItems(items.map(item => 
      item.id === id && item.quantity > 1 ? { ...item, quantity: item.quantity - 1 } : item
    ));
  };

  // 개별 항목 삭제 함수
  const handleDeleteItem = (id) => {
    const remainingItems = items.filter(item => item.id !== id); // 해당 id를 가진 항목만 제외하고 필터링
    setItems(remainingItems); // 상태 업데이트
  };

  return (
    <div className="basket-container">
      <h1 className="basket-title">장바구니</h1>
      <p className="basket-description">
        판매자 설정에 따라, 개별 배송되는 상품이 있습니다.
      </p>

      <div className="basket-selection">
        <input
          type="checkbox"
          id="select-all"
          checked={selectAll}
          onChange={handleSelectAll} // 전체 선택 체크박스 클릭 시 처리
        />
        <label htmlFor="select-all">전체 선택</label>
      </div>

      {items.map((item) => (
        <div key={item.id} className="basket-item">
          <div className="basket-item-checkbox">
            <input
              type="checkbox"
              checked={item.isSelected}
              onChange={() => handleSelectItem(item.id)} // 개별 항목 선택 시 처리
            />
          </div>
          <div className="basket-item-image">
            <div className="placeholder-image"></div>
          </div>
          <div className="basket-item-info">
            <p>{item.name}</p>
            <p className="basket-item-price">{item.price.toLocaleString()}원</p>
          </div>
          <div className="basket-item-controls">
            <button className="quantity-btn" onClick={() => handleDecreaseQuantity(item.id)}>-</button>
            <span className="quantity-number">{item.quantity}</span> {/* 수량 표시 */}
            <button className="quantity-btn" onClick={() => handleIncreaseQuantity(item.id)}>+</button>
          </div>
          <div className="basket-item-total">
            <p>총 {(item.price * item.quantity).toLocaleString()}원</p> {/* 가격 * 수량 */}
          </div>
          <button className="delete-btn" onClick={() => handleDeleteItem(item.id)}>x</button> {/* 개별 항목 삭제 */}
        </div>
      ))}

      {/* 상품구매 금액 + 합계 섹션 추가 */}
      <div className="basket-total">
        <p>선택된 상품구매 금액 합계: <strong>{totalPrice.toLocaleString()}원</strong></p>
      </div>

      <div className="basket-actions">
        <button className="delete-all-btn" onClick={handleDeleteSelected}>선택된 항목 삭제</button> {/* 선택된 항목만 삭제 */}
        <button className="purchase-btn" onClick={handlePayment}>결제하기</button> {/* 결제 버튼 클릭 시 handlePayment 호출 */}
      </div>
    </div>
  );
};

export default Baskets;