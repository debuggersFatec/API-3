import { Header } from "@/components/Header";
import { Sidebar } from "@/components/Sidebar";
import { useAuth } from "@/context/auth/useAuth";
import { TeamProvider } from "@/context/team/TeamProvider";
import { ProjectProvider } from "@/context/project/ProjectProvider";

export const Dashboard = () => {
  const { user } = useAuth();

  console.log("User in Dashboard:", user);

  return (
    <TeamProvider>
      <ProjectProvider>
        <Header />
        <Sidebar />
      </ProjectProvider>
    </TeamProvider>
  );
};
