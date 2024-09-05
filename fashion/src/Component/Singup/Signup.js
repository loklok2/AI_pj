import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Signup.css';

const Signup = () => {
    const navigate = useNavigate();
    const [isModalOpen, setIsModalOpen] = useState(false); // 모달 창 상태 관리
    const [formData, setFormData] = useState({
        username: '',   // 사용자 아이디
        password: '',   // 비밀번호
        email: '',      // 이메일
        birthDate: '',  // 생년월일
        address: '',    // 주소
        phone: '',      // 전화번호
        style: '',      // 선호하는 스타일 <= MemberSignupRequestDTO에는 없던데 스타일은 어떤식으로 처리 되는지..
        // nickname: ''  // 닉네임 (추후 사용 예정)
    });
    const [errorMessage, setErrorMessage] = useState(''); // 에러 메시지 상태 관리

    // 입력 값 상태 변경
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.id]: e.target.value });
    };

    // 백엔드 회원가입 API 호출
    const handleSignup = async () => {
        try {
            const response = await fetch('http://10.125.121.188:8080/api/auth/signup', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData),  // 사용자가 입력한 데이터를 그대로 JSON으로 변환하여 백엔드로 전송
            });

            if (response.ok) {
                // 회원가입 성공 시 모달 창 열기
                setIsModalOpen(true);
            } else {
                const errorData = await response.json();
                setErrorMessage(errorData.message || '회원가입에 실패했습니다. 다시 시도해주세요.');
            }
        } catch (error) {
            setErrorMessage('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
        }
    };

    const handleDuplicateCheck = async (field, value) => {
        // 중복 확인 로직 처리
        try {
            const response = await fetch(`http://10.125.121.188:8080/api/auth/check-duplicate?field=${field}&value=${value}`);
            if (response.ok) {
                alert(`${field === 'username' ? '아이디' : '이메일'} 중복 확인 완료`);
            } else {
                alert(`${field === 'username' ? '아이디' : '이메일'}이 이미 사용 중입니다.`);
            }
        } catch (error) {
            alert('중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.');
        }
    };

    const handleCloseModal = () => {
        setIsModalOpen(false);
        navigate('/login'); // 로그인 화면으로 이동
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
                        value={formData.username}
                        onChange={handleChange}
                    />
                    <button 
                        className="inline-button" 
                        onClick={() => handleDuplicateCheck('username', formData.username)}>
                        중복 확인
                    </button>
                </div>

                <div className="form-group">
                    <label htmlFor="password" className="form-label">비밀번호</label>
                    <input
                        id="password"
                        className="form-input"
                        type="password"
                        placeholder="비밀번호를 입력해주세요.(8자 이상)"
                        value={formData.password}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="email" className="form-label">이메일</label>
                    <input
                        id="email"
                        className="form-input short-input"
                        type="email"
                        placeholder="이메일을 입력해주세요."
                        value={formData.email}
                        onChange={handleChange}
                    />
                    <button 
                        className="inline-button" 
                        onClick={() => handleDuplicateCheck('email', formData.email)}>
                        중복 확인
                    </button>
                </div>

                <div className="form-group">
                    <label htmlFor="phone" className="form-label">전화번호</label>
                    <input
                        id="phone"
                        className="form-input"
                        type="text"
                        placeholder="전화번호를 입력해주세요.(ex.01011112222)"
                        value={formData.phone}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="address" className="form-label">주소</label>
                    <input
                        id="address"
                        className="form-input"
                        type="text"
                        placeholder="주소를 입력해주세요."
                        value={formData.address}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="birthDate" className="form-label">생년월일</label>
                    <input
                        id="birthDate"
                        className="form-input"
                        type="text"
                        placeholder="생년월일을 입력해주세요.(ex.19801101)"
                        value={formData.birthDate}
                        onChange={handleChange}
                    />
                </div>

                <div className="form-group">
                    <label htmlFor="style" className="form-label">선호하는 스타일</label>
                    <select
                        id="style"
                        className="form-select"
                        value={formData.style}
                        onChange={handleChange}
                    >
                        <option value="">선호하는 스타일 선택해주세요.</option>
                        <option value="캐주얼">캐주얼</option>
                        <option value="스포츠">스포츠</option>
                        <option value="포멀">포멀</option>
                    </select>
                </div>
            </div>

            {errorMessage && <p className="error-message">{errorMessage}</p>}

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