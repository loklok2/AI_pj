import React, { useState } from 'react';
import axios from 'axios';

interface QboardDTO {
  title: string;
  content: string;
  category: string;
}

const UpdateQboard: React.FC = () => {
  const [qboard, setQboard] = useState<QboardDTO>({
    title: '',
    content: '',
    category: ''
  });
  const [newImages, setNewImages] = useState<File[]>([]);
  const [deletedImageIds, setDeletedImageIds] = useState<number[]>([]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    const formData = new FormData();
    formData.append('qboard', JSON.stringify(qboard));
    
    newImages.forEach((image) => {
      formData.append('newImages', image);
    });
    
    deletedImageIds.forEach((id) => {
      formData.append('deletedImageIds', id.toString());
    });

    try {
      const response = await axios.put(
        `http://localhost:8080/api/qboards/${qboardId}`,
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
            'Authorization': `Bearer ${yourJWTToken}`
          }
        }
      );
      console.log('Updated Qboard:', response.data);
    } catch (error) {
      console.error('Error updating Qboard:', error);
    }
  };

  // 입력 필드 변경 핸들러
  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setQboard({ ...qboard, [e.target.name]: e.target.value });
  };

  // 이미지 파일 선택 핸들러
  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      setNewImages(Array.from(e.target.files));
    }
  };

  // 삭제할 이미지 ID 추가 핸들러
  const handleImageDelete = (imageId: number) => {
    setDeletedImageIds([...deletedImageIds, imageId]);
  };

  return (
    <form onSubmit={handleSubmit}>
      <input
        type="text"
        name="title"
        value={qboard.title}
        onChange={handleInputChange}
        placeholder="제목"
      />
      <textarea
        name="content"
        value={qboard.content}
        onChange={handleInputChange}
        placeholder="내용"
      />
      <input
        type="text"
        name="category"
        value={qboard.category}
        onChange={handleInputChange}
        placeholder="카테고리"
      />
      <input
        type="file"
        multiple
        onChange={handleImageChange}
      />
      {/* 기존 이미지 표시 및 삭제 버튼 (구현 필요) */}
      <button type="submit">수정하기</button>
    </form>
  );
};

export default UpdateQboard;