import React from 'react';
import '../../CSS/Board.css';

const Board = () => {
  return (
    <div className="qna-board-container">
      <h2 className="qna-board-title">Q&A</h2>
      <p className="qna-board-description">상품에 대한 문의 및 사이트 이용 등 기타 문의를 받습니다.</p>
      <p className="qna-board-notice">*상품에 대한 답변의 업무 시간은 최대한 빠르게 답변드리겠습니다만, 급한 문의 시 연락 주세요.</p>
      
      <div className="qna-board-button-group">
        <button>전체문의</button>
        <button>상품문의</button>
        <button>기타문의</button>
      </div>

      <table className="qna-board-table">
        <thead>
          <tr>
            <th>No.</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
          </tr>
        </thead>
        <tbody>
          {/* Add your board content here */}
        </tbody>
      </table>

      <div className="qna-board-actions">
        <button>수정</button>
        <button>작성</button>
      </div>
    </div>
  );
};

export default Board;