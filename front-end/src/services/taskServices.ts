import { axiosInstance } from "./axiosInstance";
import type { Task } from "../types/task";

export const taskService = {

  async getTaskById(taskUuid: string, token: string | null): Promise<Task> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    try {
      const response = await axiosInstance.get(`/tasks/${taskUuid}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      return response.data;
    } catch (error) {
      console.error("Erro ao buscar task:", error);
      throw error;
    }
  },

  async createTask(taskData: Task, token: string | null): Promise<Task> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    const response = await axiosInstance.post('/tasks', taskData, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  },

  async updateTask(taskUuid: string, taskData: Task, token: string | null): Promise<Task> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    const response = await axiosInstance.put(`/tasks/${taskUuid}`, taskData, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
    return response.data;
  },

  async deleteTask(taskUuid: string, token: string | null): Promise<void> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    await axiosInstance.delete(`/tasks/${taskUuid}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  async createComment(taskUuid: string, content: string, token: string | null): Promise<void> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    await axiosInstance.post(`/tasks/${taskUuid}/comments`, { content }, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },

  async deleteComment(
    taskId: string,
    commentId: string,
    token: string | null
  ): Promise<void> {
    if (!token) {
      throw new Error("Acesso negado: token não fornecido");
    }
    await axiosInstance.delete(`/tasks/${taskId}/comments/${commentId}`, {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });
  },
};
