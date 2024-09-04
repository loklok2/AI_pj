import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Component/Mainpage/Header';
import Signup from './Component/Singup/Signup'; // 회원가입 페이지 컴포넌트
import Login from './Component/Login/Login'; // 로그인 페이지 컴포넌트
import FaAnalysis from './Component/Analysis/FaAnalysis'; // 패션 분석 페이지 컴포넌트
import Completion from './Component/Singup/Completion'; // 회원가입 완료 페이지 컴포넌트
import Board from './Component/Question/Board'; // Q&A 보드 페이지 컴포넌트

function App() {
  return (
    <Router>
      <Header /> {/* 헤더부분 */}
      <Routes>
        <Route path="/signup" element={<Signup />} /> {/* 회원가입 페이지 */}
        <Route path="/login" element={<Login />} /> {/* 로그인 페이지 */}
        <Route path="/analysis" element={<FaAnalysis />} /> {/* 패션 분석 페이지 */}
        <Route path="/completion" element={<Completion />} /> {/* 회원가입 완료 페이지 */}
        <Route path="/qna" element={<Board />} /> {/* Q&A 보드 페이지 */}
      </Routes>
    </Router>
  );
}

export default App;