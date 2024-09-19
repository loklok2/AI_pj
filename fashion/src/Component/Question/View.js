import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faMessage } from '@fortawesome/free-regular-svg-icons';
import '../../CSS/View.css'; 

const View = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [post, setPost] = useState(null);
  const [comments, setComments] = useState([]);
  const [images, setImages] = useState([]); // 이미지 상태 추가
  const [comment, setComment] = useState('');
  const [editingCommentId, setEditingCommentId] = useState(null);
  const [editingContent, setEditingContent] = useState('');

  useEffect(() => {
    // 게시글 데이터 가져오기
    fetch(`http://10.125.121.188:8080/api/qboard/${id}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('게시글을 불러오는 데 실패했습니다.');
        }
        return response.json();
      })
      .then(data => setPost(data))
      .catch(error => {
        console.error('Error fetching post:', error);
      });

    // 댓글 데이터 가져오기
    fetch(`http://10.125.121.188:8080/api/comments/qboard/${id}`)
      .then(response => {
        if (!response.ok) {
          throw new Error('댓글을 불러오는 데 실패했습니다.');
        }
        return response.json();
      })
      .then(data => setComments(data))
      .catch(error => {
        console.error('Error fetching comments:', error);
      });

    // 이미지 데이터 가져오기
    fetch(`http://10.125.121.188:8080/api/qboard/${id}/images`)
      .then(response => {
        if (!response.ok) {
          throw new Error('이미지를 불러오는 데 실패했습니다.');
        }
        return response.json();
      })
      .then(data => setImages(data))
      .catch(error => {
        console.error('Error fetching images:', error);
      });
  }, [id]);

  const handleCommentChange = (e) => {
    setComment(e.target.value);
  };

  const handleCommentSubmit = () => {
    if (comment.trim() === '') {
      alert('댓글을 입력해주세요.');
      return;
    }

    // 댓글 추가 요청
    fetch(`http://10.125.121.188:8080/api/comments/${id}`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ content: comment }),
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('댓글 작성에 실패했습니다.');
        }
        return response.json();
      })
      .then(newComment => {
        setComments([...comments, newComment]);
        setComment('');
      })
      .catch(error => {
        console.error('Error adding comment:', error);
      });
  };

  const handleDeleteComment = (commentId) => {
    // 댓글 삭제 요청
    fetch(`http://10.125.121.188:8080/api/comments/${commentId}`, {
      method: 'DELETE',
    })
      .then(response => {
        if (!response.ok) {
          throw new Error('댓글 삭제에 실패했습니다.');
        }
        setComments(comments.filter((comment) => comment.id !== commentId));
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
              cmt.id === editingCommentId ? { ...cmt, content: updatedComment.content } : cmt
            )
          );
          setEditingCommentId(null);
          setEditingContent('');
        })
        .catch(error => {
          console.error('Error editing comment:', error);
        });
    } else {
      // 수정 모드로 변경
      const commentToEdit = comments.find((cmt) => cmt.id === commentId);
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
        <div className="view-qna-question-content">
          {/* 이미지 렌더링 */}
          {images.length > 0 && (
            <div className="view-qna-images">
              {images.map((img) => (
                <img 
                  key={img.qimgId}
                  src={`http://10.125.121.188:8080${img.qimgPath}`} 
                  alt={`이미지 ${img.qimgId}`} 
                  className="post-image" 
                />
              ))}
            </div>
          )}
          <p>{post.content}</p>
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