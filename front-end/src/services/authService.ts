import { axiosInstance } from "./axiosInstance";

type LoginPayload = { email: string; password: string };
type RegisterPayload = { name: string; email: string; password: string };

export const authService = {
  login: (payload: LoginPayload) =>
    axiosInstance.post("/auth/login", payload),
  register: (payload: RegisterPayload) =>
    axiosInstance.post("/auth/register", payload),
};

export default authService;
