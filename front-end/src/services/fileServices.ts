import { axiosInstance } from "./axiosInstance";

export const fileService = {
  async uploadFile(taskUuid: string, file: File): Promise<void> {
    try {
      const response = await axiosInstance.post(
        `/tasks/${taskUuid}/files`,
        file,
        {
          headers: {
            "Content-Type": file.type,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Erro ao enviar arquivo:", error);
      throw error;
    }
  },

  // Novo: envia múltiplos arquivos usando FormData (multipart/form-data)
  async uploadFiles(taskUuid: string, files: File[] | FormData): Promise<void> {
    try {
      const formData = files instanceof FormData ? files : new FormData();

      if (!(files instanceof FormData)) {
        files.forEach((f) => formData.append("files", f, f.name));
      }

      // Não definir explicitamente Content-Type — o browser define o boundary
      // Adiciona Authorization explicitamente caso o interceptor não esteja aplicando
      const stored = localStorage.getItem("token");
      const authHeader: Record<string, string> = {};
      if (stored) {
        try {
          const decoded = atob(stored);
          authHeader.Authorization = `Bearer ${decoded}`;
        } catch {
          authHeader.Authorization = `Bearer ${stored}`;
        }
      }

      const response = await axiosInstance.post(
        `/tasks/${taskUuid}/files`,
        formData,
        {
          headers: {
            ...authHeader,
          },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Erro ao enviar arquivos:", error);
      throw error;
    }
  },
};
