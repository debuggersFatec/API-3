import { axiosInstance } from './axiosInstance';
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
};