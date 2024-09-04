import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import '../../CSS/Board.css';

// 목업 데이터 15개로 확장
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
];

const Board = () => {
  const navigate = useNavigate();
  const [currentPage, setCurrentPage] = useState(1);
  const [categoryFilter, setCategoryFilter] = useState('전체'); // 카테고리 필터 상태 추가
  const itemsPerPage = 10;

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
    navigate('/write');
  };

  const handleRowClick = (id) => {
    navigate(`/qna/${id}`);
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

      <table className="qna-board-table">
        <thead>
          <tr>
            <th>아이디</th>
            <th>카테고리</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {currentItems.map((item, index) => (
            <tr key={item.id} onClick={() => handleRowClick(item.id)} style={{ cursor: 'pointer' }}>
              <td>{item.id}</td>
              <td>{item.category}</td>
              <td>{item.title}</td>
              <td>{item.writer}</td>
              <td>{item.date}</td>
            </tr>
          ))}
        </tbody>
      </table>

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
            className={`pagination-btn ${
              currentPage === index + 1 ? 'active' : ''
            }`}
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
    </div>
  );
};

export default Board;