import React, { useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Writing.css'; 

const Writing = () => {
  const quillRef = useRef(null);
  const navigate = useNavigate();  // 페이지 이동을 위한 hook

  // 텍스트 에디터 초기화
  useEffect(() => {
    if (quillRef.current) {
      if (!quillRef.current.__quill) {
        new Quill(quillRef.current, {
          theme: 'snow',
          placeholder: '내용을 입력하세요...',
          modules: {
            toolbar: [
              [{ header: [1, 2, 3, 4, 5, 6, false] }, { font: [] }],
              [{ list: 'ordered' }, { list: 'bullet' }],
              ['bold', 'italic', 'underline', 'strike'],
              [{ align: [] }],
              [{ color: [] }, { background: [] }],
              ['link', 'image', 'video'],
              ['clean']
            ],
          },
        });
      }
    }
  }, []);

  // 작성 버튼 클릭 시 동작
  const handleSubmitClick = () => {
    // 여기서 서버에 데이터 전송 등의 로직 추가 가능
    // 예: axios.post('/api/qna', { title, category, content });

    // 작성 완료 후 Q&A 게시판으로 이동
    navigate('/qna');
  };

  // 취소 버튼 클릭 시 동작
  const handleCancelClick = () => {
    navigate('/qna');  // Q&A 게시판으로 이동
  };

  return (
    <div className="writing-form-container">
      <h2 className="writing-form-title">Q&A</h2>
      <p className="writing-form-description">
        *문의에 대한 답변의 응답 시간은 최대한 빠르게 관리자들이 답변드리려고 합니다. 급한 문의 시 연락주세요.
      </p>

      <table className="writing-form-table">
        <tbody>
          <tr>
            <th>제목</th>
            <td><input type="text" placeholder="제목을 입력하세요." className="writing-form-input" /></td>
          </tr>
          <tr>
            <th>카테고리</th>
            <td>
              <select className="writing-form-select">
                <option>카테고리를 선택하세요.</option>
                <option value="product">상품문의</option>
                <option value="etc">기타문의</option>
              </select>
            </td>
          </tr>
          <tr>
            <th>내용</th>
            <td>
              <div ref={quillRef} className="writing-form-editor" />
            </td>
          </tr>
          <tr>
            <th>첨부파일</th>
            <td><input type="file" className="writing-form-file-input" /></td>
          </tr>
        </tbody>
      </table>

      <div className="writing-form-actions">
        <button className="writing-form-submit-button" onClick={handleCancelClick}>취소</button>
        <button className="writing-form-submit-button" onClick={handleSubmitClick}>작성</button>
      </div>
    </div>
  );
};

export default Writing;