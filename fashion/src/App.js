import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Component/Mainpage/Header';
import Footer from './Component/Mainpage/Footer'; 
import Signup from './Component/Singup/Signup';
import Login from './Component/Login/Login';
import FaAnalysis from './Component/Analysis/FaAnalysis';
import Completion from './Component/Singup/Completion';
import Board from './Component/Question/Board';
import Writing from './Component/Question/Writing';
import View from './Component/Question/View';
import Modify from './Component/Question/Modify';
import User from './Component/Login/User';
import Pass from './Component/Login/Pass';
import PassCheck from './Component/Login/PassCheck';
import RePass from './Component/Login/RePass';
import UserFind from './Component/Login/UserFind';
import MyPages from './Component/MyPage/MyPages'; 
import Baskets from './Component/Cart/Baskets';
import Payment from './Component/Cart/Payment'; 
import PayCompleted from './Component/Cart/PayCompleted'; 
import Order from './Component/MyPage/Order';  
import MyOrder from './Component/MyPage/MyOrder';
import Product from './Component/Eproduct/Product';  
import ProductDetails from './Component/Eproduct/ProductDetails';
import Manager from './Component/MyPage/Manager';
import Home from "./Component/Mainpage/Home"

function App() {
  return (
    <Router>
      <Header />
      <div className="main-content">
        <Routes>
          <Route path="/" element={<Home />} /> {/*메인 페이지 */}
          <Route path="/signup" element={<Signup />} /> {/* 회원가입 페이지 */}
          <Route path="/login" element={<Login />} /> {/* 로그인 페이지 */}
          <Route path="/analysis" element={<FaAnalysis />} /> {/* 패션 분석 페이지 */}
          <Route path="/qna" element={<Board />} /> {/* Q&A 보드 페이지 */}
          <Route path="/write" element={<Writing />} /> {/* Q&A 보드 게시글 작성 페이지 */}
          <Route path="/qna/:id" element={<View />} /> {/* 상세 페이지 View */}
          <Route path="/qna/modify/:id" element={<Modify />} /> {/* Modify 페이지 */}
          <Route path="/find-username" element={<User />} /> {/* 아이디 찾기 페이지 */}
          <Route path="/find-password" element={<Pass />} /> {/* 비밀번호 찾기 페이지 */}
          <Route path="/verify" element={<Completion />} /> {/* 회원가입 완료 페이지 */}
          <Route path="/passcheck" element={<PassCheck />} /> {/* 패스워드 체크 페이지 */}
          <Route path="/reset-password" element={<RePass />} /> {/* 비밀번호 재설정 페이지 */}
          <Route path="/userfind" element={<UserFind />} /> {/* 아이디 찾은거 보여주는 페이지 */}
          <Route path="/mypage" element={<MyPages />} /> {/* 마이페이지 */}
          <Route path="/cart" element={<Baskets />} /> {/* 장바구니 */}
          <Route path="/payment" element={<Payment />} /> {/* 결제 페이지 */}
          <Route path="/paycompleted" element={<PayCompleted />} /> {/* 결제 완료 페이지 */}
          <Route path="/order" element={<Order />} /> {/* 주문내역 페이지 */}
          <Route path="/myorder" element={<MyOrder />} /> {/* 마이페이지 주문 내역 */}
          <Route path="/products" element={<Product />} /> {/* 전체 상품 페이지 */}
          <Route path="/product/:id" element={<ProductDetails />} /> {/* 제품 상세 페이지 */}
          <Route path="/admin" element={<Manager />} /> {/* 관리자 페이지 */}
        </Routes>
      </div>
      <Footer /> {/* 푸터 컴포넌트 추가 */}
    </Router>
  );
}

export default App;