import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMessage } from '@fortawesome/free-regular-svg-icons';
import '../../CSS/View.css'; 

const mockData = [
  { id: 1, category: '상품문의', title: '상품 문의 1', writer: 'User1', date: '2023-09-01', content: '이 상품의 배송 기간이 어떻게 되나요?' },
  { id: 2, category: '상품문의', title: '상품 문의 2', writer: 'User2', date: '2023-09-02', content: '이 상품에 대한 설명이 더 필요합니다.' },
  { id: 3, category: '상품문의', title: '상품 문의 3', writer: 'User3', date: '2023-09-03', content: '이 상품의 색상이 실제와 동일한가요?' },
  { id: 4, category: '상품문의', title: '상품 문의 4', writer: 'User4', date: '2023-09-04', content: '할인이 언제까지 지속되나요?' },
  { id: 5, category: '상품문의', title: '상품 문의 5', writer: 'User5', date: '2023-09-05', content: '이 상품은 재고가 충분한가요?' },
  { id: 6, category: '상품문의', title: '상품 문의 6', writer: 'User6', date: '2023-09-06', content: '제품의 크기가 어떻게 되나요?' },
  { id: 7, category: '상품문의', title: '상품 문의 7', writer: 'User7', date: '2023-09-07', content: '이 상품의 환불 규정은 어떻게 되나요?' },
  { id: 8, category: '상품문의', title: '상품 문의 8', writer: 'User8', date: '2023-09-08', content: '이 제품의 세부 사항을 알려주세요.' },
  { id: 9, category: '기타문의', title: '기타 문의 9', writer: 'User9', date: '2023-09-09', content: '기타 문의 사항이 있습니다.' },
  { id: 10, category: '기타문의', title: '기타 문의 10', writer: 'User10', date: '2023-09-10', content: '기타 문의 10의 내용입니다.' },
];

const View = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const post = mockData.find((item) => item.id === parseInt(id));

  const [comment, setComment] = useState('');
  const [comments, setComments] = useState([]);
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState('');

  const handleCommentChange = (e) => {
    setComment(e.target.value);
  };

  const handleCommentSubmit = () => {
    if (comment.trim() === '') {
      alert('댓글을 입력해주세요.');
      return;
    }

    const newComment = { id: Date.now(), content: comment, writer: '사용자' };
    setComments([...comments, newComment]);
    setComment('');
  };

  const handleDeleteComment = (id) => {
    setComments(comments.filter((comment) => comment.id !== id));
  };

  const handleEditComment = (id) => {
    if (editingCommentId === id) {
      // 수정 중인 댓글을 저장
      setComments(
        comments.map((cmt) =>
          cmt.id === editingCommentId ? { ...cmt, content: editingContent } : cmt
        )
      );
      setEditingCommentId(null);
      setEditingContent('');
    } else {
      // 수정 모드로 변경
      const commentToEdit = comments.find((cmt) => cmt.id === id);
      setEditingCommentId(id);
      setEditingContent(commentToEdit.content);
    }
  };

  const goBackToList = () => {
    navigate('/qna');
  };

  const handleEdit = () => {
    navigate(`/qna/modify/${id}`);
  };

  if (!post) {
    return <div>게시글을 찾을 수 없습니다.</div>;
  }

  return (
    <div className="view-qna-container">
      <h2 className="view-qna-title">Q&A</h2>
      <p className="view-qna-notice">
        *상품에 대한 답변의 업무 시간은 최대한 빠르게 답변드리겠습니다만, 급한 문의 시 연락 주세요.
      </p>

      <div className="view-qna-list-button-container">
        <button className="view-qna-list-btn" onClick={goBackToList}>
          목록
        </button>
      </div>

      <div className="view-qna-question-box">
        <div className="view-qna-question-header">
          <strong>{post.title}</strong>
        </div>
        <div className="view-qna-question-details">
          <p>
            사용자 아이디: <strong>{post.writer}</strong> <span className="spacer"></span> 작성일자:{' '}
            <strong>{post.date}</strong>
          </p>
          <div className="view-qna-question-actions">
            <button className="view-qna-edit-btn" onClick={handleEdit}>수정</button>
            <button className="view-qna-delete-btn">삭제</button>
          </div>
        </div>
        <div className="view-qna-question-content">{post.content}</div>
      </div>

      <div className="view-qna-comment-section">
        <div className="view-qna-comment-header">
          <FontAwesomeIcon icon={faMessage} className="comment-icon" />
          <span>댓글</span>
        </div>
        <div className="view-qna-comment-box">
          <textarea
            className="view-qna-comment-input"
            value={comment}
            onChange={handleCommentChange}
            placeholder="댓글을 작성해주세요."
          />
          <button className="view-qna-submit-comment-btn" onClick={handleCommentSubmit}>
            등록
          </button>
        </div>
      </div>

      <div className="view-qna-admin-response">
        {comments.map((cmt) => (
          <div key={cmt.id} className="view-qna-admin-reply">
            <p>
              <strong>{cmt.writer}</strong>
            </p>
            {editingCommentId === cmt.id ? (
              <>
                <textarea
                  className="view-qna-edit-comment-input"
                  value={editingContent}
                  onChange={(e) => setEditingContent(e.target.value)}
                  rows={3}
                  style={{ width: '100%' }}
                />
              </>
            ) : (
              <p>{cmt.content}</p>
            )}
            <div className="view-qna-comment-actions" style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button className="view-qna-edit-btn" onClick={() => handleEditComment(cmt.id)}>
                {editingCommentId === cmt.id ? '저장' : '수정'}
              </button>
              <button className="view-qna-delete-btn" onClick={() => handleDeleteComment(cmt.id)}>
                삭제
              </button>
            </div>
            <hr className="comment-divider" />
          </div>
        ))}
      </div>
    </div>
  );
};

export default View;