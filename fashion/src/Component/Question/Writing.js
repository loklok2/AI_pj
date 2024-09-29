import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Modify.css'; 

const Modify = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const quillRef = useRef(null);
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('');
  const [files, setFiles] = useState([]);
  const [quillInstance, setQuillInstance] = useState(null);

  useEffect(() => {
    // Fetch the existing post data
    fetch(`http://10.125.121.188:8080/api/qboards/${id}`)
      .then(response => response.json())
      .then(data => {
        console.log('불러온 게시글 데이터:', data); // 불러온 데이터 콘솔에 출력
        setTitle(data.title);
        setCategory(data.boardType);
        if (quillRef.current) {
          const quill = new Quill(quillRef.current, {
            theme: 'snow',
            placeholder: '내용을 입력하세요...',
            modules: {
              toolbar: {
                container: [
                  [{ header: [1, 2, 3, 4, 5, 6, false] }, { font: [] }],
                  [{ list: 'ordered' }, { list: 'bullet' }],
                  ['bold', 'italic', 'underline', 'strike'],
                  [{ align: [] }],
                  [{ color: [] }, { background: [] }],
                  ['link', 'image', 'video'],
                  ['clean']
                ],
                handlers: {
                  image: () => {
                    const input = document.createElement('input');
                    input.setAttribute('type', 'file');
                    input.setAttribute('accept', 'image/*');
                    input.setAttribute('multiple', true);
                    input.click();

                    input.onchange = () => {
                      const selectedFiles = Array.from(input.files);
                      if (selectedFiles.length > 0) {
                        setFiles((prevFiles) => [...prevFiles, ...selectedFiles]);
                        selectedFiles.forEach((file) => {
                          const reader = new FileReader();
                          reader.onload = () => {
                            const range = quill.getSelection();
                            quill.insertEmbed(range.index, 'image', reader.result);
                          };
                          reader.readAsDataURL(file);
                        });
                      }
                    };
                  }
                },
              },
            },
          });
          quill.clipboard.dangerouslyPasteHTML(data.content);
          setQuillInstance(quill);
        }
      })
      .catch(error => console.error('게시글을 불러오는 중 오류가 발생했습니다.', error));
  }, [id]);

  const handleSubmitClick = async () => {
    if (!quillInstance) {
      console.error('Quill 인스턴스가 초기화되지 않았습니다.');
      return;
    }
  
    let content = quillInstance.root.innerHTML;

    if (!title || !content.trim() || !category) {
      alert('모든 필드를 입력해주세요.');
      return;
    }

    // 이미지 태그를 찾아서 Base64로 변환하는 작업
    const images = quillInstance.root.querySelectorAll('img');
    const base64Promises = Array.from(images).map((img) => {
      return new Promise((resolve, reject) => {
        const src = img.getAttribute('src');

        if (src.startsWith('data:image')) {
          resolve();
        } else {
          fetch(src)
            .then((response) => response.blob())
            .then((blob) => {
              const reader = new FileReader();
              reader.onload = () => {
                img.setAttribute('src', reader.result);
                resolve();
              };
              reader.onerror = reject;
              reader.readAsDataURL(blob);
            });
        }
      });
    });

    try {
      await Promise.all(base64Promises);
      content = quillInstance.root.innerHTML;
    } catch (error) {
      console.error('이미지 처리 중 오류 발생:', error);
      alert('이미지 처리 중 오류가 발생했습니다. 다시 시도해주세요.');
      return;
    }

    const data = {
      title: title,
      content: content,
      boardType: category,
    };

    try {
      const response = await fetch(`http://10.125.121.188:8080/api/qboards/${id}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${localStorage.getItem('accessToken')}`,
        },
        body: JSON.stringify(data),
      });

      if (response.ok) {
        alert('게시글이 수정되었습니다.');
        navigate(`/qna/${id}`);
      } else {
        const errorData = await response.json();
        console.error('Error response from server:', errorData);
        alert('게시글 수정에 실패했습니다.');
      }
    } catch (error) {
      console.error('게시글 수정 중 오류가 발생했습니다.', error);
      alert('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
    }
  };

  const handleCancelClick = () => {
    navigate('/qna');
  };

  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles((prevFiles) => [...prevFiles, ...selectedFiles]);
  };

  const handleRemoveFile = (index) => {
    setFiles((prevFiles) => prevFiles.filter((_, i) => i !== index));
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
                <option value="">카테고리를 선택하세요.</option>
                <option value="ProductQnA">상품문의</option>
                <option value="EtcQnA">기타문의</option>
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
            <td style={{ display: 'flex', alignItems: 'center' }}>
              <div className="custom-file-input">
                <button
                  className="file-select-button"
                  onClick={() => document.getElementById('file-upload').click()}
                >
                  파일 선택
                </button>
                <input
                  id="file-upload"
                  type="file"
                  className="modify-form-file-input"
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