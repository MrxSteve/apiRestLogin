import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { FormInput } from "../components/FormInput/FormInput";
import { SocialLoginButton } from "../components/SocialLoginButton/SocialLoginButton";
import "./styles/Login.css";
import API from "../services/axios";
import { Navbar } from "../components/NavBar/Navbar";

const Login = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState(null);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await API.post("/login", formData);

      if (response.status === 200) {
        const data = response.data;
        localStorage.setItem("accessToken", data.accessToken);
        localStorage.setItem("refreshToken", data.refreshToken);
        navigate("/"); l
      } else {
        const errorData = response.data;
        setError(errorData.message || "Error al iniciar sesión");
      }
    } catch (err) {
      setError("Error al conectar con el servidor");
    }
  };

  const handleGoogleLogin = () => {
    window.location.href = "http://localhost:8080/oauth2/authorization/google";
  };

  return (
    <div className="login-container">
      <Navbar />
      <form onSubmit={handleSubmit} className="login-form">
        <h2 className="h1register">Iniciar Sesión</h2>
        <FormInput
          type="text"
          name="username"
          placeholder="Nombre de usuario"
          value={formData.username}
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
          Iniciar Sesión
        </button>
        <div className="social-login-container">
          <SocialLoginButton provider="google" onClick={handleGoogleLogin} />
        </div>
      </form>
      {error && <p className="error-message">{error}</p>}
      <p>
        ¿Olvidaste tu contraseña?{" "}
        <Link to="/reset-password" className="forgot-password">
          Haz clic aquí
        </Link>
      </p>
      <p>
        ¿No tienes una cuenta?{" "}
        <Link to="/register" className="register-link">
          Regístrate aquí
        </Link>
      </p>
    </div>
  );
};

export default Login;
