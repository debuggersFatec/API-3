import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "@/context/auth/useAuth";
import React from "react";

export const RequireAuth: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, token } = useAuth();
  const location = useLocation();

  if (!user || !token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }
  return <>{children}</>;
};