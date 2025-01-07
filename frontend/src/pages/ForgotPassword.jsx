import React, { useState } from "react";
import { FormInput } from "../components/FormInput/FormInput";
import API, { PASS } from "../services/axios";
import Swal from "sweetalert2";
import "./styles/ForgotPassword.css";
import { Navbar } from "../components/NavBar/Navbar";

export const ForgotPassword = () => {
  const [email, setEmail] = useState("");

  const handleChange = (e) => {
    setEmail(e.target.value);
  };

  const navigateToLogin = () => {
    window.location.href = "/login";
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const response = await PASS.post("/forgot", { email });
      Swal.fire({
        icon: "info",
        title: "Correo enviado",
        text: response.data,
        confirmButtonText: "OK",
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "No se pudo enviar el correo. Por favor, intenta de nuevo más tarde.",
      });
    }
  };

  return (
    <div className="forgot-password-container">
        <Navbar />
      <form onSubmit={handleSubmit} className="forgot-password-form">
        <h2>Recuperar Contraseña</h2>
        <FormInput
          type="email"
          name="email"
          placeholder="Ingresa tu correo electronico"
          value={email}
          onChange={handleChange}
        />
        <button type="submit" className="forgot-password-button">
          Enviar
        </button>
      </form>
      <button className="forgot-password-button" onClick={navigateToLogin}>
        Volver
      </button>
    </div>
  );
};
