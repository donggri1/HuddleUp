import React, { useState } from 'react';
import './LoginPage.css';

function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await fetch('http://localhost:9000/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (response.ok) {
        const data = await response.json();
        // JWT 토큰을 localStorage에 저장
        localStorage.setItem('token', data.token);
        // 로그인 성공 후 메인 페이지로 이동
        window.location.href = '/';
      } else {
        alert('로그인에 실패했습니다.');
      }
    } catch (error) {
      console.error('Error:', error);
      alert('로그인 중 오류가 발생했습니다.');
    }
  };

  return (
    <div className="login-container">
      <div className="login-box">
        <div className="logo">
          <h1>HuddleUp</h1>
        </div>
        <form onSubmit={handleSubmit}>
          <div className="form-group">
            <input
              type="text"
              placeholder="사용자 이름"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <input
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="login-button">
            로그인
          </button>
        </form>
        <div className="divider">
          <span>또는</span>
        </div>
        <div className="forgot-password">
          <a href="/forgot-password">비밀번호를 잊으셨나요?</a>
        </div>
      </div>
      <div className="signup-box">
        <p>
          계정이 없으신가요? <a href="/signup">가입하기</a>
        </p>
      </div>
    </div>
  );
}

export default LoginPage; 