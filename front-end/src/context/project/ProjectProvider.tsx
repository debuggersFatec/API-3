import { useState, useCallback } from "react";
import { useAuth } from "@/context/auth/useAuth";
import { ProjectContext } from "./ProjectContext";
import type { Project } from "@/types/project";
import { axiosInstance } from "@/services";
import { toast } from "@/utils/toast";

export const ProjectProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const { token, user } = useAuth();

  const [project, setProject] = useState<Project>();
  const [isLoading, setIsLoading] = useState(false);

  const fetchProject = useCallback(
    async (projectUuid: string) => {
      const showLoading = !project || project.uuid !== projectUuid;
      if (showLoading) setIsLoading(true);
      try {
        const { data } = await axiosInstance.get(`/projects/${projectUuid}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : undefined,
        });

        const projectData: Project = data;

        const isMember =
          (projectData.members && projectData.members.some((m) => m.uuid === user?.uuid)) ||
          (!!projectData.team_uuid && !!user?.teams && user.teams.some((t) => t.uuid === projectData.team_uuid));

        if (!isMember) {
          // Notify and do not set project in context
          toast("error", "Você não tem permissão para acessar este projeto.");
          return undefined;
        }

        setProject(projectData);
        return projectData;
      } catch (error) {
        console.error("Erro ao buscar projeto:", error);
        toast("error", "Erro ao buscar projeto.");
        return undefined;
      } finally {
        if (showLoading) setIsLoading(false);
      }
    },
    [token, project, user]
  );

  const refreshProject = useCallback(
    async (projectUuid?: string) => {
      const id = projectUuid ?? project?.uuid;
      if (!id) return;

      const showLoading = !project || project.uuid !== id;
      if (showLoading) setIsLoading(true);
      try {
        const { data } = await axiosInstance.get(`/projects/${id}`, {
          headers: token ? { Authorization: `Bearer ${token}` } : undefined,
        });
        setProject(data);
      } catch (err) {
        console.error("Erro ao atualizar projeto:", err);
      } finally {
        if (showLoading) setIsLoading(false);
      }
    },
    [project, token]
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
