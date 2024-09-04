import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Pass.css'; 

const Pass = () => {

    const navigate = useNavigate(); 
  
    const handleSubmit = () => {
      navigate('/login'); 
    };
  return (
    <div className="pass-container">
      <div className="pass-box">
        <h2 className="pass-title">비밀번호 찾기</h2>
        <p className="pass-subtitle">
          가입 당시 아이디와 이메일을 입력해주세요.
        </p>
        <div className="pass-form-group">
          <label htmlFor="pass-id" className="pass-label">아이디</label>
          <input type="text" id="pass-id" className="pass-input" placeholder="아이디 입력" />
        </div>
        <div className="pass-form-group">
          <label htmlFor="pass-email" className="pass-label">이메일</label>
          <input type="email" id="pass-email" className="pass-input" placeholder="이메일 입력" />
        </div>
        <button className="pass-submit-button"onClick={handleSubmit}>확인</button>
      </div>
    </div>
  );
}

export default Pass;