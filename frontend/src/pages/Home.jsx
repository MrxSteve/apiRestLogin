import React from 'react'
import { Navbar } from '../components/NavBar/Navbar';
import './styles/Home.css'

export const Home = () => {
    return (
        <div className="home">
          <div className="home-content">
            <h1>Bienvenido a la Aplicación</h1>
            <Navbar />
            <p>Explora las opciones en el menú para continuar.</p>
          </div>
        </div>
      );
}
