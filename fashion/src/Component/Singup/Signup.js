import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Signup.css';

const Signup = () => {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 창 상태 관리

    const handleSignup = () => {
        // 회원가입 처리 로직 추가 (폼 제출, 유효성 검사 등)
        // 성공적으로 회원가입이 완료되면 모달 창을 엽니다.
        setIsModalOpen(true);
    };

    const handleCloseModal = () => {
        // 모달 창을 닫고 로그인 화면으로 이동합니다.
        setIsModalOpen(false);
        navigate('/login');
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
                    <label htmlFor="username" className="form-label">이름</label>
                    <input
                        id="username"
                        className="form-input"
                        type="text"
                        placeholder="이름을 입력해주세요."
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="userId" className="form-label">아이디</label>
                    <input
                        id="userId"
                        className="form-input short-input"
                        type="text"
                        placeholder="아이디 입력(5-20)"
                    />
                    <button className="inline-button">중복 확인</button>
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
                    <button className="inline-button">중복 확인</button>
                </div>

                <div className="form-group">
                    <label htmlFor="address" className="form-label">주소</label>
                    <input
                        id="address"
                        className="form-input"
                        type="text"
                        placeholder="주소를 입력해주세요."
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

            {isModalOpen && (
                <div className="modal-overlay">
                    <div className="modal-content">
                        <h3>이메일 인증을 보냈습니다.</h3>
                        <p>인증 후 로그인을 이용 해주세요.</p>
                        <button className="close-button" onClick={handleCloseModal}>닫기</button>
                    </div>
                </div>
            )}
        </div>
    );
};

export default Signup;