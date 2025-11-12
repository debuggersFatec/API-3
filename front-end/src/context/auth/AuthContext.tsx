import React, { useState } from "react";

import { AuthContext } from "./AuthContextInstance";
import { userService } from "@/services/userServices";
import type { User } from "@/types/user";
import { normalizeUser } from "./authUtils";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUserState] = useState<User | null>(() => {
    const userJson = localStorage.getItem("user");
    if (!userJson) return null;
    try {
      return normalizeUser(JSON.parse(userJson));
    } catch (e) {
      console.warn("Falha ao ler user do localStorage:", e);
      return null;
    }
  });
  const [token, setTokenState] = useState<string | null>(() => {
    const stored = localStorage.getItem("token");
    if (!stored) return null;
    try {
      return atob(stored);
    } catch {
      return stored;
    }
  });

  const setToken = (newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      try {
        localStorage.setItem("token", btoa(newToken));
      } catch {
        localStorage.setItem("token", newToken);
      }
    } else {
      localStorage.removeItem("token");
    }
  };

  // Accept unknown envelopes and normalize here so callers don't need to remember
  const setUser = (incoming: unknown) => {
    if (!incoming) {
      setUserState(null);
      try {
        localStorage.removeItem("user");
      } catch (e) {
        console.warn("Falha ao remover user do localStorage:", e);
      }
      return;
    }

    // Normalize any API envelope or user-like object
    try {
      const normalized = normalizeUser(incoming);
      setUserState(normalized);
      try {
        localStorage.setItem("user", JSON.stringify(normalized));
      } catch (e) {
        console.warn("Falha ao salvar user no localStorage:", e);
      }
    } catch (e) {
      console.warn("Falha ao normalizar user antes de salvar:", e);
      // Fallback: clear user to avoid inconsistent state
      setUserState(null);
      try {
        localStorage.removeItem("user");
      } catch (err) {
        console.warn("Falha ao remover user do localStorage:", err);
      }
    }
  };

  const refreshUser = async () => {
    if (!token) return;
    try {
      const response = await userService.getCurrentUser();
      // Use central normalizer to ensure fields like notificationsRecent are present
      const userNormalized = normalizeUser(response.data);
      setUser(userNormalized);
    } catch (err) {
      console.error("Erro ao atualizar dados do usuário:", err);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider
      value={{ user, setUser, logout, token, setToken, refreshUser }}
    >
      {children}
    </AuthContext.Provider>
  );
};
