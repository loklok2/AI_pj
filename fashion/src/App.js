import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Header from './Component/Mainpage/Header';
import Signup from './Component/Singup/Signup'; // 회원가입 페이지 컴포넌트
import Login from './Component/Login/Login'; // 로그인 페이지 컴포넌트
import FaAnalysis from './Component/Analysis/FaAnalysis'; // 패션 분석 페이지 컴포넌트
import Completion from './Component/Singup/Completion'; // 회원가입 완료 페이지 컴포넌트
import Board from './Component/Question/Board'; // Q&A 보드 페이지 컴포넌트
import Writing from './Component/Question/Writing'; // Writing 페이지 컴포넌트
import View from './Component/Question/View'; // View 컴포넌트 추가
import Modify from './Component/Question/Modify'; // Modify 컴포넌트 추가
import User from './Component/Login/User'; // 아이디 찾기 컴포넌트
import Pass from './Component/Login/Pass'; // 비밀번호 찾기 컴포넌트
import PassCheck from './Component/Login/PassCheck'; // 패스워드 체크
import RePass from './Component/Login/RePass'; // 비밀번호 재설정 컴포넌트 추가

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
        <Route path="/write" element={<Writing />} /> {/* Q&A 보드 게시글 작성 페이지 */}
        <Route path="/qna/:id" element={<View />} /> {/* 상세 페이지 View */}
        <Route path="/qna/modify/:id" element={<Modify />} /> {/* Modify 페이지 */}
        <Route path="/find-username" element={<User />} /> {/* 아이디 찾기 페이지 */}
        <Route path="/find-password" element={<Pass />} /> {/* 비밀번호 찾기 페이지 */}
        <Route path="/verify" element={<Completion />} /> {/* 회원가입 완료 페이지 */}
        <Route path="/passcheck" element={<PassCheck />} /> {/* 패스워드 체크 페이지 */}
        <Route path="/reset-password" element={<RePass />} /> {/* 비밀번호 재설정 페이지 */}
      </Routes>
    </Router>
  );
}

export default App;