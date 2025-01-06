import React from 'react'
import { Link } from 'react-router-dom'
import './styles.css'

export const Navbar = () => {
    return (
        <nav className="navbar">
          <ul className="navbar-list">
            <li className="navbar-item">
              <Link to="/">Inicio</Link>
            </li>
            <li className="navbar-item">
              <Link to="/about">Acerca de</Link>
            </li>
            <li className="navbar-item">
              <Link to="/contact">Contacto</Link>
            </li>
            <li className="navbar-item">
              <Link to="/register">Regístrate</Link>
            </li>
          </ul>
        </nav>
    );
}
