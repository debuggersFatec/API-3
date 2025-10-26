import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "@/context/auth/useAuth";
import React from "react";

export const RequireAuth: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const { user, token } = useAuth();
  const location = useLocation();

  if (!user || !token) {
    // Preserve the full path and query (e.g. /join-team?token=...) so Login/Register
    // can redirect the user back to the invite URL after successful auth.
    const redirect = encodeURIComponent(location.pathname + location.search);
    return <Navigate to={`/login?redirect=${redirect}`} replace />;
  }
  return <>{children}</>;
};
