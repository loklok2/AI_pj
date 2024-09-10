import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Board.css';

const mockData = [
  { id: 1, category: '상품문의', title: '상품 문의 1', writer: 'User1', date: '2023-09-01' },
  { id: 2, category: '상품문의', title: '상품 문의 2', writer: 'User2', date: '2023-09-02' },
  { id: 3, category: '상품문의', title: '상품 문의 3', writer: 'User3', date: '2023-09-03' },
  { id: 4, category: '상품문의', title: '상품 문의 4', writer: 'User4', date: '2023-09-04' },
  { id: 5, category: '상품문의', title: '상품 문의 5', writer: 'User5', date: '2023-09-05' },
  { id: 6, category: '상품문의', title: '상품 문의 6', writer: 'User6', date: '2023-09-06' },
  { id: 7, category: '상품문의', title: '상품 문의 7', writer: 'User7', date: '2023-09-07' },
  { id: 8, category: '상품문의', title: '상품 문의 8', writer: 'User8', date: '2023-09-08' },
  { id: 9, category: '기타문의', title: '기타 문의 9', writer: 'User9', date: '2023-09-09' },
  { id: 10, category: '기타문의', title: '기타 문의 10', writer: 'User10', date: '2023-09-10' },
  { id: 11, category: '기타문의', title: '기타 문의 11', writer: 'User11', date: '2023-09-11' },
  { id: 12, category: '기타문의', title: '기타 문의 12', writer: 'User12', date: '2023-09-12' },
  { id: 13, category: '기타문의', title: '기타 문의 13', writer: 'User13', date: '2023-09-13' },
  { id: 14, category: '기타문의', title: '기타 문의 14', writer: 'User14', date: '2023-09-14' },
  { id: 15, category: '기타문의', title: '기타 문의 15', writer: 'User15', date: '2023-09-15' },
  { id: 16, category: '기타문의', title: '기타 문의 15', writer: 'User16', date: '2023-09-15' },
  { id: 17, category: '기타문의', title: '기타 문의 15', writer: 'User17', date: '2023-09-15' },
  { id: 18, category: '기타문의', title: '기타 문의 15', writer: 'User18', date: '2023-09-15' },
  { id: 19, category: '기타문의', title: '기타 문의 15', writer: 'User19', date: '2023-09-15' },
];

const Board = () => {
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState(1);
  const [categoryFilter, setCategoryFilter] = useState('전체'); // 카테고리 필터 상태 추가
  const [isGuest, setIsGuest] = useState(false); // 비회원 로그인 상태 관리
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태 관리
  const [showModal, setShowModal] = useState(false); // 모달 창 상태 추가
  const itemsPerPage = 15;

  useEffect(() => {
    const guestLogin = sessionStorage.getItem('guestLogin') === 'true';
    const userLoggedIn = sessionStorage.getItem('userLoggedIn') === 'true';

    if (guestLogin) setIsGuest(true);
    if (userLoggedIn) setIsLoggedIn(true);
  }, []);

  // 페이지 변경 함수
  const handlePageChange = (pageNumber) => {
    if (pageNumber >= 1 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber);
    }
  };

  // 카테고리 필터 적용된 데이터
  const filteredData =
    categoryFilter === '전체'
      ? mockData
      : mockData.filter((item) => item.category === categoryFilter);

  // 데이터 페이징 처리
  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = filteredData.slice(indexOfFirstItem, indexOfLastItem);

  const totalPages = Math.ceil(filteredData.length / itemsPerPage);

  const handleWriteClick = () => {
    if (!isGuest && !isLoggedIn) {
      setShowModal(true); // 모달 창 표시
      return;
    }
    navigate('/write');
  };

  const handleRowClick = (id) => {
    if (!isGuest && !isLoggedIn) {
      setShowModal(true); // 모달 창 표시
      return;
    }
    navigate(`/qna/${id}`);
  };

  const closeModal = () => {
    setShowModal(false);
    navigate('/login'); // 모달 닫은 후 로그인 페이지로 이동
  };

  return (
    <div className="qna-board-container">
      <h2 className="qna-board-title">Q&A</h2>
      <p className="qna-board-description">
        상품에 대한 문의 및 사이트 이용 등 기타 문의를 받습니다.
      </p>
      <p className="qna-board-notice">
        *상품에 대한 답변의 업무 시간은 최대한 빠르게 답변드리겠습니다만, 급한 문의 시 연락 주세요.
      </p>

      <div className="qna-board-button-group">
        <button onClick={() => setCategoryFilter('전체')}>전체문의</button>
        <button onClick={() => setCategoryFilter('상품문의')}>상품문의</button>
        <button onClick={() => setCategoryFilter('기타문의')}>기타문의</button>
      </div>

      <div className={`qna-board-table-wrapper ${!isGuest && !isLoggedIn ? 'blurred' : ''}`}>
        <table className="qna-board-table">
          <thead>
            <tr>
              <th>번호</th>
              <th>카테고리</th>
              <th>제목</th>
              <th>작성자</th>
              <th>작성일</th>
            </tr>
          </thead>
          <tbody>
            {currentItems.map((item, index) => (
              <tr
                key={item.id}
                onClick={() => handleRowClick(item.id)}
                style={{ cursor: 'pointer' }}
              >
                <td>{item.id}</td>
                <td>{item.category}</td>
                <td>{item.title}</td>
                <td>{item.writer}</td>
                <td>{item.date}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <div className="qna-board-actions">
        <button onClick={handleWriteClick}>작성</button>
      </div>

      {/* 페이지 네이션 */}
      <div className="qna-board-pagination">
        <button
          onClick={() => handlePageChange(currentPage - 1)}
          disabled={currentPage === 1}
          className="prev-next-btn"
        >
          이전
        </button>
        {Array.from({ length: totalPages }, (_, index) => (
          <button
            key={index + 1}
            onClick={() => handlePageChange(index + 1)}
            className={`pagination-btn ${currentPage === index + 1 ? 'active' : ''}`}
          >
            {index + 1}
          </button>
        ))}
        <button
          onClick={() => handlePageChange(currentPage + 1)}
          disabled={currentPage === totalPages}
          className="prev-next-btn"
        >
          다음
        </button>
      </div>

      {/* 모달 창 */}
      {showModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <p>로그인 또는 비회원 로그인이 필요합니다.</p>
            <button onClick={closeModal}>확인</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Board;