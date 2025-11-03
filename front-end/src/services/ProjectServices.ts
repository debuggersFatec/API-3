import type { Project } from "@/types/project";
import { axiosInstance } from "./axiosInstance";

export const projectServices = {
  async createProject(
    projectName: string,
    teamUuid: string,
    token: string | null
  ): Promise<void> {
    if (!token) {
      throw new Error("Token não fornecido");
    }
    try {
      await axiosInstance.post(
        `/projects/team/${teamUuid}`,
        { name: projectName },
        { headers: { Authorization: `Bearer ${token}` } }
      );
    } catch (error) {
      console.error("Erro ao criar projeto:", error);
      throw error;
    }
  },

  async getProjectByUuid(projectUuid: string): Promise<Project> {
    try {
      const response = await axiosInstance.get(`/projects/${projectUuid}`);
      return response.data;
    } catch (error) {
      console.error("Erro ao buscar projeto:", error);
      throw error;
    }
  },

  async activeProject(
    projectUuid: string,
    token: string | null
  ): Promise<void> {
    if (!token) {
      throw new Error("Token não fornecido");
    }

    try {
      await axiosInstance.post(`/projects/${projectUuid}/activate`, {
        headers: { Authorization: `Bearer ${token}` },
      });
    } catch (error) {
      console.error("Erro ao ativar projeto:", error);
      throw error;
    }
  },

  async desactiveProject(
    projectUuid: string,
    token: string | null
  ): Promise<void> {
    if (!token) {
      throw new Error("Token não fornecido");
    }

    try {
      await axiosInstance.post(`/projects/${projectUuid}/archive`, {
        headers: { Authorization: `Bearer ${token}` },
      });
    } catch (error) {
      console.error("Erro ao desativar projeto:", error);
      throw error;
    }
  },

  async addMemberToProject(
    projectUuid: string,
    memberUuid: string
  ): Promise<void> {
    try {
      await axiosInstance.post(
        `/projects/${projectUuid}/members/${memberUuid}`
      );
    } catch (error) {
      console.error("Erro ao adicionar membro ao projeto:", error);
      throw error;
    }
  },

  async leaveProject(projectUuid: string): Promise<void> {
    try {
      await axiosInstance.delete(`/projects/${projectUuid}/leave`);
    } catch (error) {
      console.error("Erro ao remover membro do projeto:", error);
      throw error;
    }
  },
};
