
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
};
