
import { Route, Routes, useLocation } from "react-router-dom";
import { Dashboard } from "./page/Dashboard";
import { Login } from "./page/Login";
import Register from "./page/Register";


export const App = () => {
  const location = useLocation();
  console.debug("[router] pathname:", location.pathname);
  return (
    <Routes>
      <Route path="/" element={<Dashboard/>} />
      <Route path="/login" element={<Login/>} />
      <Route path="/register" element={<Register/>} />
    </Routes>
  )
}
