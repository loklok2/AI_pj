import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMessage } from '@fortawesome/free-regular-svg-icons';
import '../../CSS/View.css';
import { fetchAPI } from '../../hook/api';

const View = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [comment, setComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState('');

  useEffect(() => {
    // 게시글과 댓글 데이터를 함께 가져오기
    const fetchQboard = async () => {
      const response = await fetch(`${process.env.REACT_APP_API_URL}/qboards/${id}`, {
        headers: {
          'Authorization': localStorage.getItem('accessToken')
        },
      })
      if (response.ok) {
        const data = await response.json()
        console.log(data)
        setPost(data)
        setComment(data.comment || [])
      } else if (response.status == 403 || 401) {
        alert("게시물에 접근 권한이 없습니다.")
        navigate('/qna')
      }
    }
    fetchQboard()
  }, [id]);

  const handleCommentChange = (e) => {
    setComment(e.target.value);
  };

  const handleCommentSubmit = async () => {
    if (comment.trim() === '') {
      alert('댓글을 입력해주세요.');
      return;
    }
    // const commentData = { 
    //   qboardId: id, 
    //   userId : localStorage.getItem('userId'),
    //   username : localStorage.getItem('username'),
    //   content: comment 
    // }
    // 댓글 추가 요청
    await fetchAPI(`/comments/${id}`, {
      method: 'POST',
      body: JSON.stringify({ qboardId: id, content: comment }), // qboardId와 content 포함
    })
      .then(response => {
        console.log(response)
        if (!response) {
          throw new Error('댓글 작성에 실패했습니다.');
        }
        return response
      })
      .then(newComment => {
        setComments([...comments, newComment]);
        setComment('');
      })
      .catch(error => {
        console.error('Error adding comment:', error);
      });
  };
  const handleDelete = () => {
    if (localStorage.getItem('username') === 'admin' || localStorage.getItem('userId') == post.userId) {
      if (window.confirm("삭제하시겠습니까?")) {
        fetchAPI(`/qboards/${id}`, {
          method: 'DELETE',
        })
        navigate('/qna')
      }
    } else {
      alert('삭제 권한이 없습니다.')
    }
  }
  const handleDeleteComment = (commentId) => {
    // 댓글 삭제 요청
    fetch(`http://10.125.121.188:8080/api/comments/${commentId}`, {
      method: 'DELETE',
      headers: {
        'Authorization': localStorage.getItem('accessToken'),
      },
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('댓글 삭제에 실패했습니다.');
        }
        setComments(comments.filter((comment) => comment.commentId !== commentId));
      })
      .catch(error => {
        console.error('Error deleting comment:', error);
      });
  };

  const handleEditComment = (commentId) => {
    if (editingCommentId === commentId) {
      // 댓글 수정 저장
      fetch(`http://10.125.121.188:8080/api/comments/${commentId}`, {
        method: 'PUT',
        headers: {
          'Authorization': localStorage.getItem('accessToken'),
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ content: editingContent }),
      })
        .then(response => {
          if (!response.ok) {
            throw new Error('댓글 수정에 실패했습니다.');
          }
          return response.json();
        })
        .then(updatedComment => {
          setComments(
            comments.map((cmt) =>
              cmt.commentId === editingCommentId ? { ...cmt, content: updatedComment.content } : cmt
            )
          );
          setEditingCommentId(null);
          setEditingContent('');
        })
        .catch(error => {
          console.error('Error editing comment:', error);
        });
    } else {
      const commentToEdit = comments.find((cmt) => cmt.commentId === commentId);
      setEditingCommentId(commentId);
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

  // boardType에 따라 한글로 표시
  const boardTypeLabel = post.boardType === 'ProductQnA' ? '상품문의' : '기타문의';

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
            카테고리: <strong>{boardTypeLabel}</strong>
            <span className="spacer"></span>
            사용자 아이디: <strong>{post.userId || '알 수 없음'}</strong>
            <span className="spacer"></span>
            작성일자: <strong>{new Date(post.createDate).toLocaleDateString() || '알 수 없음'}</strong>
          </p>
          <div className="view-qna-question-actions">
            <button className="view-qna-edit-btn" onClick={handleEdit}>수정</button>
            <button className="view-qna-delete-btn" onClick={handleDelete}>삭제</button>
          </div>
        </div>
        <div className="view-qna-question-content">
          <div
            className="quill-content"
            dangerouslySetInnerHTML={{ __html: post.content }} // HTML을 직접 렌더링
          />
        </div>
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
        {comments.map((cmt, index) => (
          <div key={cmt.commentId} className="view-qna-admin-reply">
            {/* {index === 0 && (
              <p style={{ fontWeight: 'bold', color: '#333' }}>
                관리자 아이디: {cmt.userId || '알 수 없음'}
              </p>
            )} */}
            <p>
              <strong>{cmt.username}</strong>
            </p>
            {editingCommentId === cmt.commentId ? (
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
              <p>
                {cmt.content.split('\n').map((line, index) => (
                  <React.Fragment key={index}>
                    {line}
                    <br />
                  </React.Fragment>
                ))}
              </p>
            )}
            <div className="view-qna-comment-actions" style={{ display: 'flex', gap: '10px', justifyContent: 'flex-end' }}>
              <button className="view-qna-edit-btn" onClick={() => handleEditComment(cmt.commentId)}>
                {editingCommentId === cmt.commentId ? '저장' : '수정'}
              </button>
              <button className="view-qna-delete-btn" onClick={() => handleDeleteComment(cmt.commentId)}>
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