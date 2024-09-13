import React, { useState } from 'react'; 
import Admheader from '../Admins/Admheader'; 
import '../../CSS/Managers.css';

const Managers = () => {
  const [users, setUsers] = useState([
    { name: '홍길동', id: 'user001', email: 'user001@example.com', phone: '010-1234-5678', address: '서울시 강남구', style: '캐주얼' },
    { name: '김철수', id: 'user002', email: 'user002@example.com', phone: '010-2345-6789', address: '부산시 해운대구', style: '스포티' },
    { name: '이영희', id: 'user003', email: 'user003@example.com', phone: '010-3456-7890', address: '대구시 북구', style: '포멀' },
    { name: '홍길동', id: 'user001', email: 'user001@example.com', phone: '010-1234-5678', address: '서울시 강남구', style: '캐주얼' },
    { name: '김철수', id: 'user002', email: 'user002@example.com', phone: '010-2345-6789', address: '부산시 해운대구', style: '스포티' },
    { name: '홍길동', id: 'user001', email: 'user001@example.com', phone: '010-1234-5678', address: '서울시 강남구', style: '캐주얼' },
    { name: '김철수', id: 'user002', email: 'user002@example.com', phone: '010-2345-6789', address: '부산시 해운대구', style: '스포티' }
  ]);

  const [selectedUsers, setSelectedUsers] = useState([]);
  const [posts, setPosts] = useState([
    { id: 1, category: '일반', title: '배송 문의', author: 'user001', date: '2024-09-10' },
    { id: 2, category: '제품', title: '상품 품질 관련', author: 'user002', date: '2024-09-11' },
    { id: 3, category: '결제', title: '환불 요청', author: 'user003', date: '2024-09-12' },
    { id: 3, category: '결제', title: '환불 요청', author: 'user003', date: '2024-09-12' },
    { id: 3, category: '결제', title: '환불 요청', author: 'user003', date: '2024-09-12' },
    { id: 3, category: '결제', title: '환불 요청', author: 'user003', date: '2024-09-12' },
  ]);

  const [selectedPosts, setSelectedPosts] = useState([]);
  const [products, setProducts] = useState([
    { image: 'tshirt.png', name: '티셔츠', info: '면 소재', price: '₩20,000', size: 'M, L, XL', date: '2024-09-01', style: '캐주얼' },
    { image: 'jeans.png', name: '청바지', info: '데님 소재', price: '₩40,000', size: '30, 32, 34', date: '2024-09-02', style: '포멀' },
    { image: 'shoes.png', name: '운동화', info: '합성 가죽', price: '₩60,000', size: '250, 260, 270', date: '2024-09-03', style: '스포티' },
    { image: 'tshirt.png', name: '티셔츠', info: '면 소재', price: '₩20,000', size: 'M, L, XL', date: '2024-09-01', style: '캐주얼' },
    { image: 'jeans.png', name: '청바지', info: '데님 소재', price: '₩40,000', size: '30, 32, 34', date: '2024-09-02', style: '포멀' },
    { image: 'tshirt.png', name: '티셔츠', info: '면 소재', price: '₩20,000', size: 'M, L, XL', date: '2024-09-01', style: '캐주얼' },
    { image: 'jeans.png', name: '청바지', info: '데님 소재', price: '₩40,000', size: '30, 32, 34', date: '2024-09-02', style: '포멀' },
  ]);

  const [selectedProducts, setSelectedProducts] = useState([]);
  const [editIndex, setEditIndex] = useState(null);
  const [originalProduct, setOriginalProduct] = useState(null);
  const [newProduct, setNewProduct] = useState({ image: '', name: '', info: '', price: '', size: '', date: '', style: '' });

  const handleCheckboxChange = (id) => {
    setSelectedUsers(prevState => 
      prevState.includes(id) ? prevState.filter(userId => userId !== id) : [...prevState, id]
    );
  };

  const handleDeleteUsers = () => {
    setUsers(users.filter(user => !selectedUsers.includes(user.id)));
    setSelectedUsers([]);
  };

  const handlePostCheckboxChange = (id) => {
    setSelectedPosts(prevState => 
      prevState.includes(id) ? prevState.filter(postId => postId !== id) : [...prevState, id]
    );
  };

  const handleDeletePosts = () => {
    setPosts(posts.filter(post => !selectedPosts.includes(post.id)));
    setSelectedPosts([]);
  };

  const handleProductChange = (e, index) => {
    const { name, value } = e.target;
    setProducts(prevState => {
      const updatedProducts = [...prevState];
      updatedProducts[index] = { ...updatedProducts[index], [name]: value };
      return updatedProducts;
    });
  };

  const handleNewProductChange = (e) => {
    const { name, value } = e.target;
    setNewProduct(prevState => ({ ...prevState, [name]: value }));
  };

  const handleAddProduct = () => {
    setProducts([newProduct, ...products]);
    setNewProduct({ image: '', name: '', info: '', price: '', size: '', date: '', style: '' });
  };

  const handleEditProduct = (index) => {
    setEditIndex(index);
    setOriginalProduct(products[index]);
  };

  const handleUpdateProduct = () => {
    setEditIndex(null);
    setOriginalProduct(null);
  };

  const handleCancelEdit = () => {
    setProducts(prevState => {
      const updatedProducts = [...prevState];
      updatedProducts[editIndex] = originalProduct;
      return updatedProducts;
    });
    setEditIndex(null);
    setOriginalProduct(null);
  };

  const handleDeleteProducts = () => {
    setProducts(products.filter((_, index) => !selectedProducts.includes(index)));
    setSelectedProducts([]);
  };

  const handleProductCheckboxChange = (index) => {
    setSelectedProducts(prevState => 
      prevState.includes(index) ? prevState.filter(productIndex => productIndex !== index) : [...prevState, index]
    );
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0]; // 파일 하나만 선택
    if (file) {
      setNewProduct((prevState) => ({
        ...prevState,
        image: file.name // 이미지 파일 이름 저장
      }));
    }
  };

  const handleImageEditUpload = (e, index) => {
    const file = e.target.files[0];
    if (file) {
      setProducts((prevState) => {
        const updatedProducts = [...prevState];
        updatedProducts[index] = { ...updatedProducts[index], image: file.name };
        return updatedProducts;
      });
    }
  };

  const styleOptions = [
    { value: '클래식', label: '클래식' },
    { value: '매니시', label: '매니시' },
    { value: '페미니', label: '페미니' },
    { value: '에스닉', label: '에스닉' },
    { value: '컨템포러리', label: '컨템포러리' },
    { value: '내추럴', label: '내추럴' },
    { value: '젠더리스', label: '젠더리스' },
    { value: '스포티', label: '스포티' },
    { value: '서브컬처', label: '서브컬처' },
    { value: '캐주얼', label: '캐주얼' },
  ];

  return (
    <div>
      <Admheader />
      <div className="managers-dashboard-container">
        <h1 className="managers-dashboard-title">기타 관리</h1>
        <p className="managers-dashboard-welcome-text">사용자의 정보를 관리하는 페이지 입니다.</p>

        {/* 회원 관리 섹션 */}
        <div className="managers-dashboard-order">
          <div className="managers-dashboard-order-header">
            <h2>회원 관리</h2>
            <div className="managers-dashboard-order-actions">
              <button className="delete-button" onClick={handleDeleteUsers}>삭제</button>
            </div>
          </div>
        </div>
        <div className="table-container" style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <table className="managers-dashboard-order-table">
            <thead style={{ position: 'sticky', top: 0, backgroundColor: 'white' }}>
              <tr>
                <th></th> {/* 체크박스 칸 */}
                <th>이름</th>
                <th>아이디</th>
                <th>이메일</th>
                <th>전화번호</th>
                <th>주소</th>
                <th>선호 스타일</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.id}>
                  <td><input type="checkbox" checked={selectedUsers.includes(user.id)} onChange={() => handleCheckboxChange(user.id)} /></td>
                  <td>{user.name}</td>
                  <td>{user.id}</td>
                  <td>{user.email}</td>
                  <td>{user.phone}</td>
                  <td>{user.address}</td>
                  <td>{user.style}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 게시글 관리 섹션 */}
        <div className="managers-dashboard-order">
          <div className="managers-dashboard-order-header">
            <h2>Q&A 게시글</h2>
            <div className="managers-dashboard-order-actions">
              <button className="delete-button" onClick={handleDeletePosts}>삭제</button>
            </div>
          </div>
        </div>
        <div className="table-container" style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <table className="managers-dashboard-order-table">
            <thead style={{ position: 'sticky', top: 0, backgroundColor: 'white' }}>
              <tr>
                <th></th> {/* 체크박스 칸 */}
                <th>게시글 번호</th>
                <th>카테고리</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              {posts.map(post => (
                <tr key={post.id}>
                  <td><input type="checkbox" checked={selectedPosts.includes(post.id)} onChange={() => handlePostCheckboxChange(post.id)} /></td>
                  <td>{post.id}</td>
                  <td>{post.category}</td>
                  <td>{post.title}</td>
                  <td>{post.author}</td>
                  <td>{post.date}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* 상품 관리 섹션 */}
        <div className="managers-dashboard-order">
          <div className="managers-dashboard-order-header">
            <h2>상품 관리</h2>
            <div className="managers-dashboard-order-actions">
              {editIndex !== null ? (
                <>
                  <button className="save-button" onClick={handleUpdateProduct}>수정</button>
                  <button className="cancel-button" onClick={handleCancelEdit}>취소</button>
                </>
              ) : (
                <>
                  <button className="add-button" onClick={handleAddProduct}>추가</button>
                  <button className="delete-button" onClick={handleDeleteProducts}>삭제</button>
                </>
              )}
            </div>
          </div>
        </div>
        <div className="table-container" style={{ maxHeight: '300px', overflowY: 'auto' }}>
          <table className="managers-dashboard-order-table">
            <thead style={{ position: 'sticky', top: 0, backgroundColor: 'white' }}>
              <tr>
                <th></th> {/* 체크박스 칸 */}
                <th>상품 이미지</th> {/* 이미지 필드 추가 */}
                <th>상품명</th>
                <th>정보</th>
                <th>가격</th>
                <th>사이즈</th>
                <th>등록 날짜</th>
                <th>스타일</th>
                <th>수정</th>
              </tr>
            </thead>
            <tbody>
              {/* 새 상품 입력 행 */}
              <tr>
                <td></td>
                <td>
                  <label htmlFor="image-upload">첨부</label>
                  <input
                    type="file"
                    id="image-upload"
                    style={{ display: 'none' }} // 기본 파일 선택 버튼 숨기기
                    onChange={handleImageUpload}
                  />
                  {newProduct.image && <span>{newProduct.image}</span>} {/* 선택된 이미지 파일 이름 표시 */}
                </td>
                <td><input type="text" name="name" value={newProduct.name} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="info" value={newProduct.info} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="price" value={newProduct.price} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="size" value={newProduct.size} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="date" value={newProduct.date} onChange={handleNewProductChange} /></td>
                <td>
                  <select name="style" value={newProduct.style} onChange={handleNewProductChange}>
                    {styleOptions.map(option => (
                      <option key={option.value} value={option.value}>{option.label}</option>
                    ))}
                  </select>
                </td>
              </tr>
              {/* 기존 상품 목록 */}
              {products.map((product, index) => (
                <tr key={index}>
                  <td><input type="checkbox" checked={selectedProducts.includes(index)} onChange={() => handleProductCheckboxChange(index)} /></td>
                  <td>
                    {editIndex === index ? (
                      <>
                        <label htmlFor={`image-upload-${index}`}>이미지 선택</label>
                        <input
                          type="file"
                          id={`image-upload-${index}`}
                          style={{ display: 'none' }}
                          onChange={(e) => handleImageEditUpload(e, index)}
                        />
                        {product.image && <span>{product.image}</span>}
                      </>
                    ) : (
                      product.image
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <input type="text" name="name" value={product.name} onChange={(e) => handleProductChange(e, index)} />
                    ) : (
                      product.name
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <input type="text" name="info" value={product.info} onChange={(e) => handleProductChange(e, index)} />
                    ) : (
                      product.info
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <input type="text" name="price" value={product.price} onChange={(e) => handleProductChange(e, index)} />
                    ) : (
                      product.price
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <input type="text" name="size" value={product.size} onChange={(e) => handleProductChange(e, index)} />
                    ) : (
                      product.size
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <input type="text" name="date" value={product.date} onChange={(e) => handleProductChange(e, index)} />
                    ) : (
                      product.date
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <select name="style" value={product.style} onChange={(e) => handleProductChange(e, index)}>
                        {styleOptions.map(option => (
                          <option key={option.value} value={option.value}>{option.label}</option>
                        ))}
                      </select>
                    ) : (
                      product.style
                    )}
                  </td>
                  <td>
                    {editIndex === index ? (
                      <button onClick={handleCancelEdit}>취소</button>
                    ) : (
                      <button onClick={() => handleEditProduct(index)}>수정</button>
                    )}
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}

export default Managers;