
import { axiosInstance } from './axiosInstance';
import type { AxiosResponse } from 'axios';
import type { User } from '../types/user';

export const userService = {

  async getUserById(userUuid: string, token: string | null): Promise<User> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    try {
      const response = await axiosInstance.get(`/users/${userUuid}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Erro ao buscar usuário:", error);
      throw new Error("Erro ao buscar usuário");
    }
  },

  async getCurrentUser(): Promise<AxiosResponse<unknown>> {
    try {
      const response = await axiosInstance.get(`/users/me`);
      return response;
    } catch (error) {
      console.error("Erro ao buscar usuário atual:", error);
      throw error;
    }
  },

  async updateUser(
    name: string,
    img: string
  ): Promise<void> {
    try {
      await axiosInstance.put(`/users/me`, { name, img });
    } catch (error) {
      console.error("Erro ao atualizar usuário:", error);
      throw error;
    }
  }
};
