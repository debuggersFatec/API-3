import { axiosInstance } from "./axiosInstance";

type LoginPayload = { email: string; password: string };
type RegisterPayload = { name: string; email: string; password: string };
type ForgotPasswordPayload = { email: string };
type ResetPasswordPayload = { newPassword: string };


export const authService = {
  login: (payload: LoginPayload) =>
    axiosInstance.post("/auth/login", payload),
  register: (payload: RegisterPayload) =>
    axiosInstance.post("/auth/register", payload),
  forgotPassword: (payload: ForgotPasswordPayload) =>
    axiosInstance.post("/auth/recover-password", payload),
  validateResetToken: (token: string) =>
    axiosInstance.get(`/auth/reset-password/validate/${token}`),
  resetPassword: (token: string, payload: ResetPasswordPayload) =>
    axiosInstance.post(`/auth/reset-password/${token}`, payload),
  // ======================================
};

export default authService;