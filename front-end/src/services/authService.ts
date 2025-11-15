import { axiosAuthInstance } from "./axiosInstance";

type LoginPayload = { email: string; password: string };
type RegisterPayload = { name: string; email: string; password: string };
type ForgotPasswordPayload = { email: string };
type ResetPasswordPayload = { newPassword: string };


export const authService = {
  login: (payload: LoginPayload) =>
    axiosAuthInstance.post("/auth/login", payload),
  register: (payload: RegisterPayload) =>
    axiosAuthInstance.post("/auth/register", payload),
  forgotPassword: (payload: ForgotPasswordPayload) =>
    axiosAuthInstance.post("/auth/recover-password", payload),
  validateResetToken: (token: string) =>
    axiosAuthInstance.get(`/auth/reset-password/validate/${token}`),
  resetPassword: (token: string, payload: ResetPasswordPayload) =>
    axiosAuthInstance.post(`/auth/reset-password/${token}`, payload),
  // ======================================
};

export default authService;