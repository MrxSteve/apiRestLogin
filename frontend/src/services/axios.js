import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api/auth', // Base URL del backend
});

export const PASS = axios.create({
  baseURL: 'http://localhost:8080/api/password', // Recuperar contraseña
})

export default API;
