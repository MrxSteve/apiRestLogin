import axios from 'axios';

const API = axios.create({
  baseURL: 'http://localhost:8080/api/auth', // Base URL del backend
});

export default API;