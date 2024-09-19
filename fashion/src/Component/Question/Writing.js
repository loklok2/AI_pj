import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Writing.css'; 

const Writing = () => {
  const quillRef = useRef(null);
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [file, setFile] = useState(null);
  const [quillInstance, setQuillInstance] = useState(null);

  // 텍스트 에디터 초기화
  useEffect(() => {
    if (quillRef.current) {
      const quill = new Quill(quillRef.current, {
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
      setQuillInstance(quill); // Quill 인스턴스 저장
    }
  }, []);

  // 작성 버튼 클릭 시 동작
  const handleSubmitClick = async () => {
    if (!quillInstance) {
      console.error('Quill 인스턴스가 초기화되지 않았습니다.');
      return;
    }

    const content = quillInstance.root.innerHTML; // 에디터의 내용 가져오기

    if (!title || !category || !content) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    // 서버로 전송할 데이터 생성
    const formData = new FormData();
    formData.append('title', title);
    formData.append('category', category);
    formData.append('content', content);
    if (file) {
      formData.append('images', file); // 이미지 파일 추가
    }

    try {
      const response = await fetch('http://10.125.121.188:8080/api/qboard', {
        method: 'POST',
        body: formData,
      });

      if (response.ok) {
        alert('게시글이 작성되었습니다.');
        navigate('/qna');
      } else {
        throw new Error('게시글 작성에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error submitting post:', error);
      alert('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
    }
  };

  // 취소 버튼 클릭 시 동작
  const handleCancelClick = () => {
    navigate('/qna');
  };

  // 첨부 파일 변경 시 동작
  const handleFileChange = (e) => {
    setFile(e.target.files[0]);
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
            <td>
              <input 
                type="text" 
                placeholder="제목을 입력하세요." 
                className="writing-form-input"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </td>
          </tr>
          <tr>
            <th>카테고리</th>
            <td>
              <select 
                className="writing-form-select"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                <option value="">카테고리를 선택하세요.</option>
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
            <td>
              <input 
                type="file" 
                className="writing-form-file-input"
                onChange={handleFileChange}
              />
            </td>
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