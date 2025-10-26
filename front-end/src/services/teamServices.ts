import type { Team } from "@/types/team";
import { axiosInstance } from "./axiosInstance";
import type { UserRef } from "@/types/user";

export const teamServices = {
  async getTeam(teamUuid: string, token: string | null): Promise<Team> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    try {
      const response = await axiosInstance.get(`/teams/${teamUuid}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Erro ao buscar equipe:", error);
      throw error;
    }
  },
  async createTeam(
    teamName: string,
    member: UserRef,
    token: string | null
  ): Promise<Team> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    try {
      const response = await axiosInstance.post(
        `/teams`,
        {
          name: teamName,
          members: [member],
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Erro ao criar equipe:", error);
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
};