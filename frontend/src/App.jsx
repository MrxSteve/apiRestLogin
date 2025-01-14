import React from 'react'
import './App.css'
import { Routes, Route, BrowserRouter as Router } from 'react-router-dom'
import { Home } from './pages/Home'
import { Register } from './pages/Register';
import Login from './pages/Login';
import { ForgotPassword } from './pages/ForgotPassword';
import { ResetPassword } from './pages/ResetPassword';
import { OAuth2Callback } from './pages/OAuth2Callback';

function App() {

  return (
    <Router>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} /> 
        <Route path="/forgot-password" element={<ForgotPassword />} />
        <Route path="/reset-password" element={<ResetPassword />} />
        <Route path="/oauth2/callback" element={<OAuth2Callback />} />
      </Routes>
    </Router>
  );
}

export default App
