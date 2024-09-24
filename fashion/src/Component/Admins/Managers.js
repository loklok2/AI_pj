import React, { useState, useEffect } from 'react';
import Admheader from '../Admins/Admheader';
import '../../CSS/Managers.css';

const Managers = () => {
  const [users, setUsers] = useState([]);
  const [selectedUsers, setSelectedUsers] = useState([]);
  const [posts, setPosts] = useState([]);
  const [selectedPosts, setSelectedPosts] = useState([]);
  const [products, setProducts] = useState([]);
  const [selectedProducts, setSelectedProducts] = useState([]);
  const [editIndex, setEditIndex] = useState(null);
  const [originalProduct, setOriginalProduct] = useState(null);
  const [newProduct, setNewProduct] = useState({ image: '', name: '', info: '', price: '', size: '', date: '', style: '' });

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

  useEffect(() => {
    fetchUsers();
    fetchPosts();
    fetchProducts();
  }, []);

  const fetchUsers = async () => {
    try {
      console.log('Fetching users from /api/admin/members');
      const response = await fetch('http://10.125.121.188:8080/api/admin/members');
      const data = await response.json();
      console.log('Fetched Users:', data);
      setUsers(data);
    } catch (error) {
      console.error('Error fetching users:', error);
    }
  };

  const fetchPosts = async () => {
    try {
      console.log('Fetching posts from /api/admin/qboards');
      const response = await fetch('http://10.125.121.188:8080/api/admin/qboards');
      const data = await response.json();
  
      if (Array.isArray(data)) {
        // 필요한 게시글 정보를 추출하여 상태 업데이트
        const postsData = data.map(post => ({
          qboardId: post.qboardId,
          category: post.boardType || '기타 문의', // boardType이 없는 경우 기본값으로 '일반'을 사용
          title: post.title,
          author: post.member ? post.member.name : '알 수 없음', // 작성자 정보 설정
          date: post.createDate.split('T')[0], // 날짜 포맷 설정
          content: post.content, // 게시글 내용
        }));
        console.log('Fetched Posts:', postsData);
        setPosts(postsData);
      } else {
        console.error('Unexpected data format:', data);
      }
    } catch (error) {
      console.error('Error fetching posts:', error);
    }
  };


  const fetchProducts = async () => {
    try {
      console.log('Fetching products from /api/admin/products');
      const response = await fetch('http://10.125.121.188:8080/api/admin/products');

      if (!response.ok) {
        const errorText = await response.text();
        console.error(`Failed to fetch products: ${response.status} - ${errorText}`);
        return;
      }

      const responseText = await response.text();
      console.log('Raw response text:', responseText);

      try {
        const data = JSON.parse(responseText);
        
        if (Array.isArray(data)) {
          const updatedProducts = data.map((product) => ({
            ...product,
            style: product.attributeNames[0], // 스타일 필드 설정
            date: product.createDate.split('T')[0], // 날짜 형식 변환
          }));
          setProducts(updatedProducts);
        } else {
          console.error('Unexpected data format:', data);
        }

      } catch (jsonError) {
        console.error('Error parsing JSON:', jsonError, 'Response text:', responseText);
      }

    } catch (error) {
      console.error('Error fetching products:', error);
    }
  };

  const handleCheckboxChange = (id) => {
    setSelectedUsers((prevState) =>
      prevState.includes(id) ? prevState.filter(userId => userId !== id) : [...prevState, id]
    );
  };

  const handleDeleteUsers = async () => {
    try {
      console.log('Deleting selected users:', selectedUsers);
      await Promise.all(selectedUsers.map(id => fetch(`http://10.125.121.188:8080/api/admin/members/${id}`, { method: 'DELETE' })));
      setUsers(users.filter(user => !selectedUsers.includes(user.userId)));
      setSelectedUsers([]);
    } catch (error) {
      console.error('Error deleting users:', error);
    }
  };

  const handlePostCheckboxChange = (id) => {
    setSelectedPosts((prevState) =>
      prevState.includes(id) ? prevState.filter(postId => postId !== id) : [...prevState, id]
    );
  };

  const handleDeletePosts = async () => {
    try {
      console.log('Deleting selected posts:', selectedPosts);
      await Promise.all(selectedPosts.map(id => fetch(`http://10.125.121.188:8080/api/admin/qboards/${id}`, { method: 'DELETE' })));
      setPosts(posts.filter(post => !selectedPosts.includes(post.qboardId)));
      setSelectedPosts([]);
    } catch (error) {
      console.error('Error deleting posts:', error);
    }
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

  const handleAddProduct = async () => {
    try {
      console.log('Adding a new product:', newProduct);
      const response = await fetch('http://10.125.121.188:8080/api/admin/products', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(newProduct),
      });

      if (response.ok) {
        const addedProduct = await response.json();
        setProducts([addedProduct, ...products]);
        setNewProduct({ image: '', name: '', info: '', price: '', size: '', date: '', style: '' });
      } else {
        console.error('Failed to add product');
      }
    } catch (error) {
      console.error('Error adding product:', error);
    }
  };

  const handleEditProduct = (index) => {
    setEditIndex(index);
    setOriginalProduct(products[index]);
  };

  const handleUpdateProduct = async () => {
    try {
      console.log(`Updating product with ID: ${products[editIndex].productId}`, products[editIndex]);
      const response = await fetch(`http://10.125.121.188:8080/api/admin/products/${products[editIndex].productId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(products[editIndex]),
      });

      if (response.ok) {
        setEditIndex(null);
        setOriginalProduct(null);
      } else {
        console.error('Failed to update product');
      }
    } catch (error) {
      console.error('Error updating product:', error);
    }
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

  const handleDeleteProducts = async () => {
    try {
      console.log('Deleting selected products:', selectedProducts.map(index => products[index].productId));
      await Promise.all(selectedProducts.map(index => fetch(`http://10.125.121.188:8080/api/admin/products/${products[index].productId}`, { method: 'DELETE' })));
      setProducts(products.filter((_, index) => !selectedProducts.includes(index)));
      setSelectedProducts([]);
    } catch (error) {
      console.error('Error deleting products:', error);
    }
  };

  const handleProductCheckboxChange = (index) => {
    setSelectedProducts(prevState => 
      prevState.includes(index) ? prevState.filter(productIndex => productIndex !== index) : [...prevState, index]
    );
  };

  const handleImageUpload = (e) => {
    const file = e.target.files[0];
    if (file) {
      setNewProduct((prevState) => ({
        ...prevState,
        image: file.name
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
                <th></th>
                <th>이름</th>
                <th>번호</th>
                <th>이메일</th>
                <th>전화번호</th>
                <th>주소</th>
                <th>선호 스타일</th>
              </tr>
            </thead>
            <tbody>
              {users.map(user => (
                <tr key={user.userId}>
                  <td><input type="checkbox" checked={selectedUsers.includes(user.userId)} onChange={() => handleCheckboxChange(user.userId)} /></td>
                  <td>{user.name}</td>
                  <td>{user.userId}</td>
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
                <th></th>
                <th>게시글 번호</th>
                <th>카테고리</th>
                <th>제목</th>
                <th>작성자</th>
                <th>작성일</th>
              </tr>
            </thead>
            <tbody>
              {posts.map(post => (
                <tr key={post.qboardId}>
                  <td><input type="checkbox" checked={selectedPosts.includes(post.qboardId)} onChange={() => handlePostCheckboxChange(post.qboardId)} /></td>
                  <td>{post.qboardId}</td>
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
                <th></th>
                <th>상품명</th>
                <th>정보</th>
                <th>가격</th>
                <th>등록 날짜</th>
                <th>스타일</th>
                <th>수정</th>
              </tr>
            </thead>
            <tbody>
              {/* 새 상품 입력 행 */}
              <tr>
                <td></td>
                <td><input type="text" name="name" value={newProduct.name} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="info" value={newProduct.info} onChange={handleNewProductChange} /></td>
                <td><input type="text" name="price" value={newProduct.price} onChange={handleNewProductChange} /></td>
                <td><input type="date" name="date" value={newProduct.date} onChange={handleNewProductChange} /></td>
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
                <tr key={product.productId}>
                  <td><input type="checkbox" checked={selectedProducts.includes(index)} onChange={() => handleProductCheckboxChange(index)} /></td>
                  <td>{editIndex === index ? <input type="text" name="name" value={product.name} onChange={(e) => handleProductChange(e, index)} /> : product.name}</td>
                  <td>{editIndex === index ? <input type="text" name="info" value={product.info} onChange={(e) => handleProductChange(e, index)} /> : product.info}</td>
                  <td>{editIndex === index ? <input type="text" name="price" value={product.price} onChange={(e) => handleProductChange(e, index)} /> : product.price}</td>
                  <td>{editIndex === index ? <input type="date" name="date" value={product.date} onChange={(e) => handleProductChange(e, index)} /> : product.date}</td>
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
};

export default Managers;