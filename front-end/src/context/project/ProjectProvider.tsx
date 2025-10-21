import { useState, useCallback } from "react";
import { useAuth } from "@/context/auth/useAuth";
import { ProjectContext } from "./ProjectContext";
import type { Project } from "@/types/project";
import { axiosInstance } from "@/services";

export const ProjectProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const { token } = useAuth();

  const [project, setProject] = useState<Project>();
  const [isLoading, setIsLoading] = useState(false);

  const fetchProject = useCallback(
    async (projectUuid: string) => {
      setIsLoading(true);
      try {
        const { data } = await axiosInstance.get(`/projects/${projectUuid}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : undefined,
        });
        setProject(data);
      } catch (error) {
        console.error("Erro ao buscar projeto:", error);
      } finally {
        setIsLoading(false);
      }
    },
    [token]
  );

  const refreshProject = useCallback(
    async (projectUuid?: string) => {
      const id = projectUuid ?? project?.uuid;
      if (!id) return;

      setIsLoading(true);
      try {
        const { data } = await axiosInstance.get(`/projects/${id}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : undefined,
        });
        setProject(data);
      } catch (err) {
        console.error("Erro ao atualizar projeto:", err);
      } finally {
        setIsLoading(false);
      }
    },
    [project?.uuid, token]
  );

  return (
    <ProjectContext.Provider
      value={{
        project,
        isLoading,
        setProject,
        refreshProject,
        fetchProject,
      }}
    >
      {children}
    </ProjectContext.Provider>
  );
};
