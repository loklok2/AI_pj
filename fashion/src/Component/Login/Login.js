import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom'; // 로그인 성공 시 리다이렉트를 위해 사용
import '../../CSS/Login.css';

const Login = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate(); // 페이지 이동을 위한 네비게이트 함수

    const handleLogin = async () => {
        try {
            const response = await fetch('http://10.125.121.188:8080/api/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({ username, password }), // 사용자가 입력한 아이디와 비밀번호를 JSON 형태로 전달
            });

            if (response.ok) {
                const data = await response.json();
                // 로그인 성공: 토큰을 localStorage에 저장하고 리다이렉트 처리
                localStorage.setItem('accessToken', data.accessToken);
                localStorage.setItem('refreshToken', data.refreshToken);
                navigate('/dashboard'); // 로그인 성공 후 대시보드 페이지로 이동
            } else {
                const errorData = await response.json();
                setError(errorData.message || '로그인에 실패했습니다. 다시 시도해주세요.');
            }
        } catch (error) {
            setError('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
        }
    };

    return (
        <div className="custom-login-outer">
            <div className="custom-login-container">
                <div className="custom-login-header">
                    <h2 className="custom-login-title">로그인</h2>
                </div>
                <div className="custom-login-form-container">
                    <div className="custom-form-group">
                        <label htmlFor="username" className="custom-form-label">아이디</label>
                        <input
                            id="username"
                            className="custom-form-input"
                            type="text"
                            placeholder="아이디 입력"
                            value={username} // 상태 값으로 연결
                            onChange={(e) => setUsername(e.target.value)} // 아이디 입력 시 상태 업데이트
                        />
                    </div>
                    <div className="custom-form-group">
                        <label htmlFor="password" className="custom-form-label-password">비밀번호</label>
                        <input
                            id="password"
                            className="custom-form-input"
                            type="password"
                            placeholder="비밀번호 입력"
                            value={password} // 상태 값으로 연결
                            onChange={(e) => setPassword(e.target.value)} // 비밀번호 입력 시 상태 업데이트
                        />
                    </div>
                    {error && <div className="custom-error-message">{error}</div>} {/* 에러 메시지 표시 */}
                    <div className="custom-submit-button-container">
                        <button className="custom-submit-button" onClick={handleLogin}>로그인</button>
                    </div>
                </div>
                <div className="custom-signup-redirect">
                    <span>아직 계정이 없으신가요? </span>
                    <a href="/signup" className="custom-signup-link">가입하기</a>
                </div>
                <div className="custom-signup-redirects">
                    <span>계정을 잊으버리셨나요?</span>
                    <a href="/find-username" className="custom-signup-link"> 아이디 찾기 </a>
                    <a href="/find-password" className="custom-signup-link">/ 비밀번호 찾기</a>
                </div>
            </div>
        </div>
    );
};

export default Login;