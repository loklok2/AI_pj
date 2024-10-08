import React, { useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Quill from 'quill';
import 'quill/dist/quill.snow.css';
import '../../CSS/Modify.css';
import { fetchAPI } from '../../hook/api'; // API 호출하는 함수

const Modify = () => {
  const { id } = useParams(); // 게시글 id를 받아옴
  const quillRef = useRef(null); // Quill 에디터를 담을 ref
  const quillInstanceRef = useRef(null); // Quill 인스턴스를 관리할 ref
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [boardType, setBoardType] = useState('');
  const [files, setFiles] = useState([]); // 다중 파일을 위해 배열로 변경
  const [content, setContent] = useState('');
  const [quillInstance, setQuillInstance] = useState(null);
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
        const imgURL = process.env.REACT_APP_URL + imageUrl
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
  useEffect(() => {
    // 게시글 데이터를 불러오는 API 호출
    fetchAPI(`/qboards/${id}`)
      .then(data => {
        setTitle(data.title);
        setBoardType(data.boardType);
        setContent(data.content);
        // Quill 에디터 초기화 및 기존 내용 설정
        if (quillRef.current) {
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
                  ['link', 'image', 'video'],
                  ['clean']
                ],
                handlers: {
                  image: handleImageUpload
                },
              },
            },
          });
          quill.clipboard.dangerouslyPasteHTML(data.content);
          setQuillInstance(quill);
          quillInstanceRef.current = quill; // Quill 인스턴스를 ref에 저장

        }
      })
      .catch(error => console.error('게시글을 불러오는 중 오류가 발생했습니다.', error));
  }, [id]);

  const handleSubmitClick = async () => {
    const quillInstance = quillInstanceRef.current; // ref에서 Quill 인스턴스 가져오기

    if (!quillInstance) {
      console.error('Quill 에디터가 초기화되지 않았습니다.');
      return;
    }
    let updatedContent = quillInstance.root.innerHTML;


    // 수정한 데이터를 서버에 전송하는 로직

    const qboardContent = {
      title: title,
      content: updatedContent,
      boardType: boardType,
    };

    try {
      const response = await fetchAPI(`/qboards/${id}`, {
        method: 'PUT',
        body: JSON.stringify(qboardContent),
      });
      console.log(response)
      if (response) {
        alert('게시글이 수정되었습니다.');
        navigate(`/qna/${id}`);
      } else {
        throw new Error('게시글 수정에 실패했습니다.');
      }
    } catch (error) {
      console.error('게시글 수정 중 오류가 발생했습니다.', error);
      alert('서버에 문제가 발생했습니다. 나중에 다시 시도해주세요.');
    }
  };

  const handleCancelClick = () => {
    navigate('/qna');
  };

  // 첨부 파일 변경 시 동작
  const handleFileChange = (e) => {
    const selectedFiles = Array.from(e.target.files);
    setFiles((prevFiles) => [...prevFiles, ...selectedFiles]);
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
        </tbody>
      </table>

      <div className="writing-form-actions">
        <button className="writing-form-submit-button" onClick={handleCancelClick}>취소</button>
        <button className="modify-form-submit-button" onClick={handleSubmitClick}>수정</button>
      </div>
    </div>
  );
};

export default Modify;