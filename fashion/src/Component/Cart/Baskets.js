import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Baskets.css';

const Baskets = () => {
  const navigate = useNavigate();
  const userId = sessionStorage.getItem('userId');

  const [items, setItems] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [selectAll, setSelectAll] = useState(false);
  const [isGuest, setIsGuest] = useState(false);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [showLoginModal, setShowLoginModal] = useState(false);

  useEffect(() => {
    const guestLogin = sessionStorage.getItem('guestLogin') === 'true';
    const userLoggedIn = sessionStorage.getItem('userLoggedIn') === 'true';

    if (guestLogin || userLoggedIn) {
      setIsGuest(guestLogin);
      setIsLoggedIn(userLoggedIn);

      if (userLoggedIn) {
        fetch(`/api/cart/${userId}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
          },
        })
          .then(response => response.json())
          .then(data => setItems(data.map(item => ({ ...item, isSelected: false }))))
          .catch(error => console.error('장바구니 가져오기 실패:', error));
      } else {
        const storedItems = JSON.parse(sessionStorage.getItem('cartItems')) || [];
        setItems(storedItems.map(item => ({ ...item, isSelected: false })));
      }
    } else {
      setShowLoginModal(true);
    }
  }, [navigate, userId]);

  const totalPrice = items
    .filter(item => item.isSelected)
    .reduce((acc, item) => {
      const price = parseInt(item.price, 10) || 0;
      const quantity = parseInt(item.quantity, 10) || 1;
      return acc + (price * quantity);
    }, 0);

  const handlePayment = () => {
    const selectedItems = items.filter(item => item.isSelected);
    if (selectedItems.length === 0) {
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
    const updatedItems = items.map(item =>
      item.id === id ? { ...item, isSelected: !item.isSelected } : item
    );
    setItems(updatedItems);

    // 전체 선택 체크박스 상태 업데이트
    const allSelected = updatedItems.every(item => item.isSelected);
    setSelectAll(allSelected);
  };

  const handleDeleteSelected = () => {
    const remainingItems = items.filter(item => !item.isSelected);
    setItems(remainingItems);

    if (isLoggedIn) {
      fetch(`/api/cart/${userId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
        },
        body: JSON.stringify({ items: remainingItems }),
      })
        .catch(error => console.error('장바구니 업데이트 실패:', error));
    } else {
      sessionStorage.setItem('cartItems', JSON.stringify(remainingItems));
    }
  };

  const handleIncreaseQuantity = (id) => {
    const updatedItems = items.map(item => 
      item.id === id ? { ...item, quantity: item.quantity + 1 } : item
    );
    setItems(updatedItems);

    if (isLoggedIn) {
      fetch(`/api/cart/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
        },
        body: JSON.stringify(updatedItems),
      })
        .catch(error => console.error('장바구니 업데이트 실패:', error));
    } else {
      sessionStorage.setItem('cartItems', JSON.stringify(updatedItems));
    }
  };

  const handleDecreaseQuantity = (id) => {
    const updatedItems = items.map(item => 
      item.id === id && item.quantity > 1 ? { ...item, quantity: item.quantity - 1 } : item
    );
    setItems(updatedItems);

    if (isLoggedIn) {
      fetch(`/api/cart/${userId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
        },
        body: JSON.stringify(updatedItems),
      })
        .catch(error => console.error('장바구니 업데이트 실패:', error));
    } else {
      sessionStorage.setItem('cartItems', JSON.stringify(updatedItems));
    }
  };

  const handleDeleteItem = (id) => {
    const remainingItems = items.filter(item => item.id !== id);
    setItems(remainingItems);

    if (isLoggedIn) {
      fetch(`/api/cart/${userId}`, {
        method: 'DELETE',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
        },
        body: JSON.stringify({ items: remainingItems }),
      })
        .catch(error => console.error('장바구니 업데이트 실패:', error));
    } else {
      sessionStorage.setItem('cartItems', JSON.stringify(remainingItems));
    }
  };

  const handleQuantityChange = (id, value) => {
    if (value >= 1) {
      const updatedItems = items.map(item =>
        item.id === id ? { ...item, quantity: value } : item
      );
      setItems(updatedItems);

      if (isLoggedIn) {
        fetch(`/api/cart/${userId}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('token')}`,
          },
          body: JSON.stringify(updatedItems),
        })
          .catch(error => console.error('장바구니 업데이트 실패:', error));
      } else {
        sessionStorage.setItem('cartItems', JSON.stringify(updatedItems));
      }
    }
  };

  const closeModal = () => {
    setShowModal(false);
  };

  const closeLoginModal = () => {
    setShowLoginModal(false);
    navigate('/login');
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
            <p className="basket-item-price">{parseInt(item.price, 10).toLocaleString()}원</p>
          </div>
          <div className="basket-item-controls">
            <button className="quantity-btn" onClick={() => handleDecreaseQuantity(item.id)}>-</button>
            <input
              type="number"
              value={item.quantity !== undefined ? item.quantity : 1}
              min="1"
              onChange={(e) => handleQuantityChange(item.id, parseInt(e.target.value))}
              style={{ textAlign: 'center' }} 
            />
            <button className="quantity-btn" onClick={() => handleIncreaseQuantity(item.id)}>+</button>
          </div>
          <div className="basket-item-total">
            <p>총 {(parseInt(item.price, 10) * parseInt(item.quantity)).toLocaleString()}원</p>
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

      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <p className="modal-message">상품을 선택해주세요.</p>
            <button onClick={closeModal}>확인</button>
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

export default Baskets;