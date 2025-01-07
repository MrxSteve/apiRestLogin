import React, { useState } from "react";
import { FormInput } from "../components/FormInput/FormInput";
import API, { PASS } from "../services/axios";
import Swal from "sweetalert2";
import { useNavigate, useSearchParams } from "react-router-dom";
import "./styles/ResetPassword.css";

export const ResetPassword = () => {
  const [formData, setFormData] = useState({
    newPassword: "",
    confirmPassword: "",
  });
  const [searchParams] = useSearchParams();
  const token = searchParams.get("token");
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (formData.newPassword !== formData.confirmPassword) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "Las contraseñas no coinciden",
      });
      return;
    }

    try {
      const response = await PASS.post("/reset", {
        token,
        newPassword: formData.newPassword,
      });
      Swal.fire({
        icon: "success",
        title: "Contraseña Restablecida",
        text: response.data,
        confirmButtonText: "Iniciar Sesión",
      }).then(() => {
        navigate("/login");
      });
    } catch (error) {
      Swal.fire({
        icon: "error",
        title: "Error",
        text: "El token es inválido o ha expirado.",
      });
    }
  };

  return (
    <div className="reset-password-container">
      <form onSubmit={handleSubmit} className="reset-password-form">
        <h2>Restablecer Contraseña</h2>
        <FormInput
          type="password"
          name="newPassword"
          placeholder="Nueva contraseña"
          value={formData.newPassword}
          onChange={handleChange}
        />
        <FormInput
          type="password"
          name="confirmPassword"
          placeholder="Confirmar nueva contraseña"
          value={formData.confirmPassword}
          onChange={handleChange}
        />
        <button type="submit" className="reset-password-button">
          Restablecer
        </button>
      </form>
    </div>
  );
};

