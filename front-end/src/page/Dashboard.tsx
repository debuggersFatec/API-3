import { Header } from "@/components/Header";
import { Sidebar } from "@/components/Sidebar";
import { useAuth } from "@/context/AuthContext";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";

export const Dashboard = () => {
  const navigate = useNavigate();
  const { user, token } = useAuth();

  useEffect(() => {
    if (user === null || token === null) {
      navigate("/login");
    }
  }, [user, token, navigate]);

  return (
    <>
      <Header />
      <Sidebar />
    </>
  );
};
