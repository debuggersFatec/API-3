import { Header } from "@/components/Header";
import { Sidebar } from "@/components/Sidebar";
import { useNavigate } from "react-router-dom";
import { useEffect } from "react";
import { useAuth } from "@/context/useAuth";

export const Dashboard = () => {
  const navigate = useNavigate();
  const { user, token } = useAuth();

  useEffect(() => {
    if (user === null || token === null) {
      navigate("/login");
    }
  }, [user, token, navigate]);

  return (
    console.log("Dashboard render with user:", user),
    <>
      <Header />
      <Sidebar />
    </>
  );
};
