import React from 'react'
import './FormInput.css'

export const FormInput = ({ type, name, placeholder, value, onChange }) => {
    return (
        <input
          className="form-input"
          type={type}
          name={name}
          placeholder={placeholder}
          value={value}
          onChange={onChange}
          required
        />
      );
}
