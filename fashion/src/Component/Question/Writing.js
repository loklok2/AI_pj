import React, { useEffect, useRef, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import ReactQuill from "react-quill";
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Writing.css';
import { fetchAPI } from '../../hook/api'; // API 호출하는 함수

const Writing = () => {
  const quillRef = useRef(null); // Quill 에디터를 담을 ref
  const quillInstanceRef = useRef(null); // Quill 인스턴스를 관리할 ref
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [boardType, setBoardType] = useState('');
  const [files, setFiles] = useState([]);

  // 이미지 업로드 핸들러
  const handleImageUpload = () => {
    const input = document.createElement('input');
    input.setAttribute('type', 'file');
    input.setAttribute('accept', 'image/*');
    input.click();

    input.onchange = async () => {
      const file = input.files[0];
      const formData = new FormData();
      formData.append('image', file);

      try {
        const response = await fetch(`${process.env.REACT_APP_API_URL}/qboards/uploadImage`, {
          method: 'POST',
          body: formData,
          headers: {
            Authorization: localStorage.getItem('accessToken'), // 인증 토큰
          },
        });

        if (!response.ok) {
          throw new Error(`Failed to upload image. Status: ${response.status}`);
        }

        const imageUrl = await response.text(); // 서버에서 받은 이미지 URL
        const imgURL = process.env.REACT_APP_URL +imageUrl
        console.log('Image upload successful:', imageUrl);

        // quillInstance가 초기화된 경우에만 삽입
        if (quillInstanceRef.current) {
          const range = quillInstanceRef.current.getSelection(); // 현재 커서 위치 가져오기
          if (range) {
            quillInstanceRef.current.insertEmbed(range.index, 'image', imgURL); // 이미지 URL 삽입
          } else {
            console.error('Quill 인스턴스의 커서 위치를 가져올 수 없습니다.');
          }
        } else {
          console.error('Quill 인스턴스가 아직 초기화되지 않았습니다.');
        }
      } catch (error) {
        console.error('Error uploading image:', error);
        alert('이미지 업로드 중 오류가 발생했습니다.');
      }
    };
  };

  // Quill 에디터 초기화
  useEffect(() => {
    if (!quillInstanceRef.current && quillRef.current) {
      const quill = new Quill(quillRef.current, {
        theme: 'snow',
        placeholder: '내용을 입력하세요...',
        modules: {
          toolbar: {
            container: [
              [{ header: [1, 2, 3, 4, 5, 6, false] }],
              [{ list: 'ordered' }, { list: 'bullet' }],
              ['bold', 'italic', 'underline', 'strike'],
              [{ align: [] }],
              [{ color: [] }, { background: [] }],
              ['link', 'image'],
              ['clean']
            ],
            handlers: {
              image: handleImageUpload, // 이미지 업로드 핸들러 추가
            },
          },
        },
      });
      quillInstanceRef.current = quill; // Quill 인스턴스를 ref에 저장
    }

    // cleanup: 페이지가 사라질 때 Quill 인스턴스를 정리
    return () => {
      if (quillInstanceRef.current) {
        quillInstanceRef.current = null; // Quill 인스턴스 해제
      }
    };
  }, []);

  // 게시글 작성 버튼 클릭 핸들러
  const handleSubmitClick = async () => {
    const quillInstance = quillInstanceRef.current; // ref에서 Quill 인스턴스 가져오기

    if (!quillInstance) {
      console.error('Quill 인스턴스가 초기화되지 않았습니다.');
      return;
    }

    let content = quillInstance.root.innerHTML;

    if (!title || !content.trim() || !boardType) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    const qboardContent = {
      title: title,
      content: content,
      boardType: boardType,
    };

    try {
      console.log(qboardContent)
      const data = await fetchAPI('/qboards/create', {
        method: 'POST',
        body: JSON.stringify(qboardContent),
      });
      if (data) {
        alert('게시글이 작성되었습니다.');
        navigate('/qna');
      } else {
        const errorData = await data;
        console.error('Error response from server:', errorData);
        alert('게시글 작성에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error submitting post:', error);
      alert('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
    }
  };

  const handleCancelClick = () => {
    navigate('/qna');
  };

  // 파일 선택 핸들러
  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles((prevFiles) => [...prevFiles, ...selectedFiles]);
  };

  // 파일 삭제 핸들러
  const handleRemoveFile = (index) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
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
                value={boardType}
                onChange={(e) => setBoardType(e.target.value)}
              >
                <option value="">카테고리를 선택하세요.</option>
                <option value="ProductQnA">상품문의</option>
                <option value="EtcQnA">기타문의</option>
              </select>
            </td>
          </tr>
          <tr>
            <th>내용</th>
            <td>
              <div ref={quillRef} className="writing-form-editor" />
            </td>
          </tr>
          {/* <tr>
            <th>첨부파일</th>
            <td style={{ display: "flex", alignItems: "center" }}>
              <div className="custom-file-input">
                <button className="file-select-button" onClick={() => document.getElementById('file-upload').click()}>
                  파일 선택
                </button>
                <input
                  id="file-upload"
                  type="file"
                  className="writing-form-file-input"
                  multiple
                  onChange={handleFileChange}
                  style={{ display: 'none' }}
                />
              </div>
              <div className="file-preview-container" style={{ marginLeft: '10px', display: 'flex', flexWrap: 'wrap', gap: '10px' }}>
                {files.map((file, index) => (
                  <div key={file.name} className="file-preview-item" style={{ display: 'flex', alignItems: 'center' }}>
                    <span>{file.name}</span>
                    <button onClick={() => handleRemoveFile(index)} className="remove-file-button" style={{ marginLeft: '5px' }}>x</button>
                  </div>
                ))}
              </div>
            </td>
          </tr> */}
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
