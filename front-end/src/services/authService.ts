import { axiosInstance } from "./axiosInstance";

type LoginPayload = { email: string; password: string };
type RegisterPayload = { name: string; email: string; password: string };
type ForgotPasswordPayload = { email: string };
type ResetPasswordPayload = { novaSenha: string };


export const authService = {
  login: (payload: LoginPayload) =>
    axiosInstance.post("/auth/login", payload),
  register: (payload: RegisterPayload) =>
    axiosInstance.post("/auth/register", payload),
  forgotPassword: (payload: ForgotPasswordPayload) =>
    axiosInstance.post("/auth/recuperar-senha", payload),
  validateResetToken: (token: string) =>
    axiosInstance.get(`/auth/resetar-senha/validar/${token}`),
  resetPassword: (token: string, payload: ResetPasswordPayload) =>
    axiosInstance.post(`/auth/resetar-senha/${token}`, payload),
  // ======================================
};

export default authService;