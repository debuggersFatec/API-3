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
};

export default teamServices;
