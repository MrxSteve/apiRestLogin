import React, { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export const OAuth2Callback = () => {
  const navigate = useNavigate();

  useEffect(() => {
    // Obtener los parámetros de la URL
    const params = new URLSearchParams(window.location.search);
    const accessToken = params.get("accessToken");
    const refreshToken = params.get("refreshToken");
    const username = params.get("username");

    if (accessToken && refreshToken && username) {
      // Guardar los tokens en el localStorage
      localStorage.setItem("accessToken", accessToken);
      localStorage.setItem("refreshToken", refreshToken);
      localStorage.setItem("username", username);

      // Redirigir 
      navigate("/");
    } else {
      // Redirigir 
      navigate("/");
    }
  }, [navigate]);

  return (
    <div>
      <p>Redirigiendo...</p>
    </div>
  );
};
