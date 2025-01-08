import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Navbar } from "../components/NavBar/Navbar";
import API from "../services/axios";
import "./styles/Home.css";

export const Home = () => {
  const [username, setUsername] = useState(null);
  const navigate = useNavigate();

  // Extraer el nombre de usuario del token al cargar la página
  useEffect(() => {
    const token = localStorage.getItem("accessToken");
    const savedUsername = localStorage.getItem("username");
  
    if (savedUsername) {
      // Si el username ya está en el localStorage
      setUsername(savedUsername);
    } else if (token) {
      // Si hay un token, intenta decodificarlo
      const payload = JSON.parse(atob(token.split(".")[1]));
      setUsername(payload.sub);
    }
  }, []);

  // Función para cerrar sesión
  const handleLogout = async () => {
    try {
      const accessToken = localStorage.getItem("accessToken");
      await API.post(
        "/logout",
        null,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      );

      // Limpiar el localStorage y redirigir al login
      localStorage.clear();
      setUsername(null); // quitar nombre de usuario
      navigate("/login");
    } catch (error) {
      console.error("Error al cerrar sesión:", error);
    }
  };

  return (
    <div className="home">
      <Navbar />
      <div className="home-content">
        <h1>Bienvenido {username ? username : ""} !</h1>
        {username && ( // Solo muestra el botón si hay un usuario autenticado
          <button className="logout-button" onClick={handleLogout}>
            Cerrar Sesión
          </button>
        )}
      </div>
    </div>
  );
};
