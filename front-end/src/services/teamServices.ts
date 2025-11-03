import type { Team } from "@/types/team";
import { axiosInstance } from "./axiosInstance";
import type { AxiosResponse } from "axios";
import type { UserRef } from "@/types/user";

export const teamServices = {
  async getTeam(teamUuid: string): Promise<Team> {
    try {
      const response = await axiosInstance.get(`/teams/${teamUuid}`);
      return response.data;
    } catch (error) {
      console.error("Erro ao buscar equipe:", error);
      throw error;
    }
  },

  async createTeam(teamName: string, member: UserRef): Promise<Team> {
    try {
      const response = await axiosInstance.post(`/teams`, {
        name: teamName,
        members: [member],
      });
      return response.data;
    } catch (error) {
      console.error("Erro ao criar equipe:", error);
      throw error;
    }
  },
  
  async getTeamById(teamUuid: string): Promise<AxiosResponse<Team>> {
    try {
      return await axiosInstance.get(`/teams/${teamUuid}`);
    } catch (error) {
      console.error("Erro ao buscar equipe (raw):", error);
      throw error;
    }
  },

  // NOVO: Método para entrar na equipe com token de convite
  async joinTeamWithInvite(token: string, authToken: string | null): Promise<Team> {
    if (!authToken) {
      throw new Error("Acesso negado: token de usuário não fornecido");
    }
    try {
      // O endpoint do backend é POST /api/teams/join/invite/{token}
      const response = await axiosInstance.post(`/teams/join/invite/${token}`, {}, {
        headers: {
          Authorization: `Bearer ${authToken}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Erro ao entrar na equipe com token de convite:", error);
      throw error;
    }
  },
  async leaveTeam(teamUuid: string): Promise<void> {    
    try {
      await axiosInstance.delete(`/teams/${teamUuid}/leave`);
    } catch (error) {
      console.error("Erro ao sair da equipe:", error);
      throw error;
    }
  },
};
export default teamServices;

