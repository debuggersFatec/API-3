import { Route, Routes, useLocation } from "react-router-dom";
import { Dashboard } from "./page/Dashboard";
import { Login } from "./page/Login";
import Register from "./page/Register";
import { RequireAuth } from "./components/RequireAuth";
import { JoinTeamPage } from "./page/JoinTeamPage";

export const App = () => {
  const location = useLocation();
  console.debug("[router] pathname:", location.pathname);
  return (
    <Routes>
      <Route
        path="/"
        element={
          <RequireAuth>
            <Dashboard />
          </RequireAuth>
        }
      />
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
  <Route path="join-team" element={<JoinTeamPage />} />
    </Routes>
  );
};
