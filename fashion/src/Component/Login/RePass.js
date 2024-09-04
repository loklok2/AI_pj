import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // useNavigate 추가
import '../../CSS/RePass.css'; // CSS 파일을 설정해주세요

const RePass = () => {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState(''); // 오류 메시지 상태 추가
  const navigate = useNavigate(); // navigate 변수 선언

  const handleSubmit = (e) => {
    e.preventDefault();
    if (newPassword === confirmPassword) {
      // 비밀번호 변경 로직 처리 (예: API 호출)
      alert('비밀번호가 성공적으로 변경되었습니다.');
      navigate('/login'); // 비밀번호 변경 후 로그인 화면으로 이동
    } else {
      setErrorMessage('비밀번호가 일치하지 않습니다.'); // 비밀번호 불일치 메시지 설정
    }
  };

  const handleConfirmPasswordChange = (e) => {
    const value = e.target.value;
    setConfirmPassword(value);

    if (value === '') {
      setErrorMessage(''); // 입력값이 지워졌을 때 오류 메시지 제거
    } else if (newPassword !== value) {
      setErrorMessage('비밀번호가 일치하지 않습니다.'); // 불일치 시 오류 메시지
    } else {
      setErrorMessage(''); // 일치 시 오류 메시지 제거
    }
  };

  return (
    <div className="repass-container">
      <div className="repass-box">
        <h2 className="repass-title">비밀번호 재설정</h2>
        <p className="repass-subtitle">8자 - 15자 이하로 입력해주세요.</p>
        <form onSubmit={handleSubmit}>
          <div className="repass-form-group">
            <label htmlFor="new-password" className="repass-label">새 비밀번호</label>
            <input
              type="password"
              id="new-password"
              className="repass-input"
              placeholder="새 비밀번호"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
            />
          </div>
          <div className="repass-form-group">
            <label htmlFor="confirm-password" className="repass-label">새 비밀번호 확인</label>
            <input
              type="password"
              id="confirm-password"
              className="repass-input"
              placeholder="새 비밀번호 확인"
              value={confirmPassword}
              onChange={handleConfirmPasswordChange}
            />
            {/* 비밀번호가 일치하지 않으면 오류 메시지 표시 */}
            {errorMessage && <p className="error-message">{errorMessage}</p>}
          </div>
          <button type="submit" className="repass-submit-button">변경하기</button>
        </form>
      </div>
    </div>
  );
};

export default RePass;