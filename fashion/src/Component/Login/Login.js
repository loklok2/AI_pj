import React from 'react';
import '../../CSS/Login.css';

const Login = () => {
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
                        />
                    </div>
                    <div className="custom-form-group">
                        <label htmlFor="password" className="custom-form-label-password">비밀번호</label>
                        <input
                            id="password"
                            className="custom-form-input"
                            type="password"
                            placeholder="비밀번호 입력"
                        />
                    </div>
                    <div className="custom-submit-button-container">
                        <button className="custom-submit-button">로그인</button>
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