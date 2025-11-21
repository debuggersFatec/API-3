import { Provider } from "@/components/ui/provider";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { App } from "./App";
import { ColorModeProvider } from "./components/ui/color-mode";
import { AuthProvider } from "./context/auth/AuthContext";
import { Toaster } from "@/components/ui/toaster";
import { GoogleOAuthProvider } from "@react-oauth/google";

const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID;

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <Provider>
        <ColorModeProvider>
          <GoogleOAuthProvider clientId={clientId}>
            <BrowserRouter>
              <App />
              <Toaster />
            </BrowserRouter>
          </GoogleOAuthProvider>
        </ColorModeProvider>
      </Provider>
    </AuthProvider>
  </StrictMode>
);