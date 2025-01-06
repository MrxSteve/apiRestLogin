import React, { useState } from 'react';
import { FormInput } from '../components/FormInput/FormInput';
import { Navbar } from '../components/NavBar/Navbar';
import API from '../services/axios';
import { Link } from 'react-router-dom';
import { SocialLoginButton } from '../components/SocialLoginButton/SocialLoginButton';
import Swal from 'sweetalert2';
import './styles/Register.css';

export const Register = () => {
  const [formData, setFormData] = useState({
    username: "",
    email: "",
    password: "",
  });

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    API.post("/register", formData)
      .then((res) => {
        Swal.fire({
          title: 'Éxito',
          text: 'Se ha enviado un correo de confirmación.',
          icon: 'success',
          confirmButtonText: 'Aceptar'
        });
      })
      .catch((err) => {
        console.error(err);
        Swal.fire({
          title: 'Error',
          text: 'Hubo un problema al registrar el usuario.',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      });
  };

  return (
    <div className="register-container">
      <Navbar />
      <form onSubmit={handleSubmit}>
      <h1 className="h1register">Registro</h1>
        <FormInput
          type="text"
          name="username"
          placeholder="Nombre de usuario"
          value={formData.username}
          onChange={handleChange}
        />
        <FormInput
          type="email"
          name="email"
          placeholder="Correo electrónico"
          value={formData.email}
          onChange={handleChange}
        />
        <FormInput
          type="password"
          name="password"
          placeholder="Contraseña"
          value={formData.password}
          onChange={handleChange}
        />
        <button type="submit" className="register-button">
          Registrarse
        </button>
        <div className="social-login-container">
        <SocialLoginButton provider="google" onClick={handleGoogleLogin} />
      </div>
      </form>
      
      <p>
        ¿Ya tienes una cuenta? <Link to="/login">Inicia sesión</Link>
      </p>
    </div>
  );
};