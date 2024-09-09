import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Baskets.css';

const Baskets = () => {
  const navigate = useNavigate();

  // sessionStorage에서 장바구니 정보 가져오기
  const [items, setItems] = useState([]);
  const [showModal, setShowModal] = useState(false); // 모달 상태 추가

  useEffect(() => {
    const storedItems = JSON.parse(sessionStorage.getItem('cartItems')) || [];
    setItems(storedItems);
  }, []);

  const [selectAll, setSelectAll] = useState(false);

  const totalPrice = items
    .filter(item => item.isSelected)
    .reduce((acc, item) => {
      const price = parseInt(item.price.replace(/,/g, '')) || 0; // price에서 쉼표 제거 후 숫자로 변환
      const quantity = parseInt(item.quantity) || 1; // quantity가 undefined일 경우 기본값 1
      return acc + (price * quantity);
    }, 0);

  const handlePayment = () => {
    // 선택된 아이템만 sessionStorage에 저장
    const selectedItems = items.filter(item => item.isSelected);
    if (selectedItems.length === 0) {
      // 선택된 상품이 없으면 모달을 보여줌
      setShowModal(true);
    } else {
      sessionStorage.setItem('selectedItems', JSON.stringify(selectedItems));
      navigate('/payment');
    }
  };

  const handleSelectAll = () => {
    const newSelectAll = !selectAll;
    setSelectAll(newSelectAll);
    setItems(items.map(item => ({ ...item, isSelected: newSelectAll })));
  };

  const handleSelectItem = (id) => {
    setItems(
      items.map(item =>
        item.id === id ? { ...item, isSelected: !item.isSelected } : item
      )
    );
  };

  const handleDeleteSelected = () => {
    const remainingItems = items.filter(item => !item.isSelected);
    setItems(remainingItems);
    sessionStorage.setItem('cartItems', JSON.stringify(remainingItems));
  };

  const handleIncreaseQuantity = (id) => {
    const updatedItems = items.map(item => 
      item.id === id ? { ...item, quantity: item.quantity + 1 } : item
    );
    setItems(updatedItems);
    sessionStorage.setItem('cartItems', JSON.stringify(updatedItems)); // 수량 증가 후 저장
  };

  const handleDecreaseQuantity = (id) => {
    const updatedItems = items.map(item => 
      item.id === id && item.quantity > 1 ? { ...item, quantity: item.quantity - 1 } : item
    );
    setItems(updatedItems);
    sessionStorage.setItem('cartItems', JSON.stringify(updatedItems)); // 수량 감소 후 저장
  };

  const handleDeleteItem = (id) => {
    const remainingItems = items.filter(item => item.id !== id);
    setItems(remainingItems);
    sessionStorage.setItem('cartItems', JSON.stringify(remainingItems)); // 삭제 후 저장
  };

  const handleQuantityChange = (id, value) => {
    if (value >= 1) {
      const updatedItems = items.map(item =>
        item.id === id ? { ...item, quantity: value } : item
      );
      setItems(updatedItems);
      sessionStorage.setItem('cartItems', JSON.stringify(updatedItems)); // 수량 변경 후 저장
    }
  };

  const closeModal = () => {
    setShowModal(false); // 모달 닫기
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
          onChange={handleSelectAll}
        />
        <label htmlFor="select-all">전체 선택</label>
      </div>

      {items.map((item) => (
        <div key={item.id} className="basket-item">
          <div className="basket-item-checkbox">
            <input
              type="checkbox"
              checked={item.isSelected}
              onChange={() => handleSelectItem(item.id)}
            />
          </div>
          <div className="basket-item-info">
            <p>{item.name}</p>
            {/* 쉼표 제거 후 가격 출력 */}
            <p className="basket-item-price">{parseInt(item.price.replace(/,/g, '')).toLocaleString()}원</p>
          </div>
          <div className="basket-item-controls">
            <button className="quantity-btn" onClick={() => handleDecreaseQuantity(item.id)}>-</button>
            <input
              type="number"
              value={item.quantity !== undefined ? item.quantity : 1} // undefined 체크 및 기본값 설정
              min="1"
              onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value))}
              style={{ textAlign: 'center' }} 
            />
            <button className="quantity-btn" onClick={() => handleIncreaseQuantity(item.id)}>+</button>
          </div>
          <div className="basket-item-total">
            {/* 가격에서 쉼표 제거 후 숫자로 변환하여 총 가격 계산 */}
            <p>총 {(parseInt(item.price.replace(/,/g, '')) * parseInt(item.quantity)).toLocaleString()}원</p>
          </div>
          <button className="delete-btn" onClick={() => handleDeleteItem(item.id)}>x</button>
        </div>
      ))}

      <div className="basket-total">
        <p>총 금액 합계: <strong>{totalPrice.toLocaleString()}원</strong></p>
      </div>

      <div className="basket-actions">
        <button className="delete-all-btn" onClick={handleDeleteSelected}>장바구니 삭제</button>
        <button className="purchase-btn" onClick={handlePayment}>결제하기</button>
      </div>

      {/* 모달 */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
          <p className="modal-message">상품을 선택해주세요.</p>
            <button onClick={closeModal}>확인</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Baskets;