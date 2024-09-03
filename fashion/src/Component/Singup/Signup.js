import React from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Signup.css';

const Signup = () => {
    const navigate = useNavigate();

    const handleSignup = () => {
        // 여기서 폼 제출, 유효성 검사 등을 처리할 수 있습니다.
        // 회원가입이 성공적으로 완료된 후 완료 페이지로 이동합니다.
        navigate('/completion');
    };

    return (
        <div className="signup-container">
            <div className="signup-header">
                <h2 className="signup-title">회원가입</h2>
                <p className="signup-description">모든 입력은 필수 사항 입니다.</p>
                <p className="signup-note">* 현재 입력된 사용자의 정보는 어떠한 상업적 이용이 없음을 알려드립니다.</p>
            </div>
            <div className="signup-form-container">
                <div className="form-group">
                    <label htmlFor="username" className="form-label">아이디</label>
                    <input
                        id="username"
                        className="form-input short-input"
                        type="text"
                        placeholder="아이디 입력(5-20)"
                    />
                    <button className="inline-button">중복확인</button>
                </div>
                <div className="form-group">
                    <label htmlFor="password" className="form-label">비밀번호</label>
                    <input
                        id="password"
                        className="form-input"
                        type="password"
                        placeholder="비밀번호를 입력해주세요.(8자 이상)"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="phone" className="form-label">전화번호</label>
                    <input
                        id="phone"
                        className="form-input"
                        type="text"
                        placeholder="전화번호를 입력해주세요.(ex.01011112222)"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="email" className="form-label">이메일</label>
                    <input
                        id="email"
                        className="form-input short-input"
                        type="email"
                        placeholder="이메일을 입력해주세요."
                    />
                    <button className="inline-button">인증하기</button>
                </div>
                <div className="form-group">
                    <label htmlFor="verification" className="form-label">인증 번호</label>
                    <input
                        id="verification"
                        className="form-input"
                        type="text"
                        placeholder="인증 번호를 입력해주세요."
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="birthdate" className="form-label">생년월일</label>
                    <input
                        id="birthdate"
                        className="form-input"
                        type="text"
                        placeholder="생년월일을 입력해주세요.(YYYY-MM-DD)"
                    />
                </div>
                <div className="form-group">
                    <label htmlFor="style" className="form-label">선호하는 스타일</label>
                    <select id="style" className="form-select">
                        <option>선호하는 스타일 선택해주세요.</option>
                        <option>캐주얼</option>
                        <option>스포츠</option>
                        <option>포멀</option>
                    </select>
                </div>
            </div>
            <div className="submit-button-container">
                <button className="submit-button" onClick={handleSignup}>가입하기</button>
            </div>
        </div>
    );
};

export default Signup;