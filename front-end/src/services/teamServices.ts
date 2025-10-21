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
};
