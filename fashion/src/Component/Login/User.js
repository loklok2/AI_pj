import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/User.css'; 

const User = () => {
    const navigate = useNavigate(); 
  
    const handleSubmit = () => {
      navigate('/login'); 
    };
  
    return (
      <div className="user-container">
        <div className="user-box">
          <h2 className="user-title">아이디 찾기</h2>
          <p className="user-subtitle">
            가입 당시의 이름과 이메일을 입력해주세요.
          </p>
          <div className="user-form-group">
            <label htmlFor="user-id" className="user-label">이름</label>
            <input type="text" id="user-id" className="user-input" placeholder="이름을 입력해주세요." />
          </div>
          <div className="user-form-group">
            <label htmlFor="user-email" className="user-label">이메일</label>
            <input type="email" id="user-email" className="user-input" placeholder="이메일을 입력해주세요." />
          </div>
          <button className="user-submit-button" onClick={handleSubmit}>확인</button>
        </div>
      </div>
    );
  }
  
  export default User;