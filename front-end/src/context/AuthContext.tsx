import React, { useState } from 'react';
import type { UserData } from './authUtils';
import { AuthContext } from './AuthContextInstance';
import axios from 'axios';

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUserState] = useState<UserData | null>(() => {
    const userJson = localStorage.getItem('user');
    return userJson ? JSON.parse(userJson) : null;
  });
  const [token, setTokenState] = useState<string | null>(() => {
    return localStorage.getItem('token');
  });

  const setToken = (newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem('token', newToken);
    } else {
      localStorage.removeItem('token');
    }
  };

  const setUser = (newUser: UserData | null) => {
    setUserState(newUser);
    if (newUser) {
      localStorage.setItem('user', JSON.stringify(newUser));
    } else {
      localStorage.removeItem('user');
    }
  };


  const refreshUser = async () => {
    if (!token) return;
    try {
      const response = await axios.get('http://localhost:8080/api/users/me', {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setUser(response.data);
    } catch (err) {
      console.error('Erro ao atualizar dados do usuário:', err);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider value={{ user, setUser, logout, token, setToken, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
};

