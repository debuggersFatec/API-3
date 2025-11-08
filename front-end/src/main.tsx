import { Provider } from "@/components/ui/provider";
import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { App } from "./App";
import { ColorModeProvider } from "./components/ui/color-mode";
import { AuthProvider } from "./context/auth/AuthContext";
import { useAutoRefreshUser } from "./hooks/useAutoRefreshUser";

export const AutoRefreshUser = () => {
  useAutoRefreshUser();
  return null;
};
import { Toaster } from "@/components/ui/toaster";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    <AuthProvider>
      <Provider>
        <ColorModeProvider>
          <BrowserRouter>
            <AutoRefreshUser />
            <App />
            <Toaster />
          </BrowserRouter>
        </ColorModeProvider>
      </Provider>
    </AuthProvider>
  </StrictMode>
);
