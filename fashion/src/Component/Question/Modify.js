import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Modify.css'; 

// mock 데이터를 불러온다고 가정
const mockData = {
  id: 1,
  title: '수정할 제목',
  category: 'product',
  content: '수정할 내용입니다.',
};

const Modify = () => {
  const { id } = useParams(); // 게시글 id를 받아옴
  const navigate = useNavigate();
  const quillRef = useRef(null);
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [content, setContent] = useState('');

  useEffect(() => {
    // 서버에서 데이터를 받아오는 로직 대신 mock 데이터 사용
    // 실제로는 API 호출을 통해 데이터를 불러오면 됩니다.
    const postData = mockData;

    setTitle(postData.title);
    setCategory(postData.category);
    setContent(postData.content);

    // Quill 에디터 초기화 및 기존 내용 설정
    if (quillRef.current) {
      const quillInstance = new Quill(quillRef.current, {
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

      // Quill 에디터에 기존 내용 넣기
      quillInstance.clipboard.dangerouslyPasteHTML(postData.content);
    }
  }, [id]);

  const handleSubmitClick = () => {
    const quillInstance = quillRef.current.__quill;
    const updatedContent = quillInstance.root.innerHTML; // 수정된 내용 가져오기

    // 수정한 데이터를 서버에 전송하는 로직 (예: axios.post('/api/qna', { title, category, content }))
    // 예: axios.post(`/api/qna/${id}`, { title, category, content: updatedContent })

    // 수정 완료 후 해당 게시글의 View 페이지로 이동
    navigate(`/qna/${id}`);
  };

  const handleCancelClick = () => {
    navigate('/qna');  // Q&A 게시판으로 이동
  };

  return (
    <div className="modify-form-container">
      <h2 className="modify-form-title">Q&A 수정</h2>
      <p className="modify-form-description">
        *문의에 대한 답변의 응답 시간은 최대한 빠르게 관리자들이 답변드리려고 합니다. 급한 문의 시 연락주세요.
      </p>

      <table className="modify-form-table">
        <tbody>
          <tr>
            <th>제목</th>
            <td>
              <input 
                type="text" 
                value={title} 
                onChange={(e) => setTitle(e.target.value)} 
                placeholder="제목을 입력하세요." 
                className="modify-form-input" 
              />
            </td>
          </tr>
          <tr>
            <th>카테고리</th>
            <td>
              <select 
                className="modify-form-select" 
                value={category} 
                onChange={(e) => setCategory(e.target.value)}
              >
                <option value="product">상품문의</option>
                <option value="etc">기타문의</option>
              </select>
            </td>
          </tr>
          <tr>
            <th>내용</th>
            <td>
              <div ref={quillRef} className="modify-form-editor" />
            </td>
          </tr>
          <tr>
            <th>첨부파일</th>
            <td><input type="file" className="modify-form-file-input" /></td>
          </tr>
        </tbody>
      </table>

      <div className="modify-form-actions">
        <button className="modify-form-submit-button" onClick={handleCancelClick}>취소</button>
        <button className="modify-form-submit-button" onClick={handleSubmitClick}>수정</button>
      </div>
    </div>
  );
};

export default Modify;