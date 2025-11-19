import axios from "axios";
import type { AxiosInstance } from "axios";

const attachAuthInterceptor = (instance: AxiosInstance) => {
  instance.interceptors.request.use((config) => {
    const stored = localStorage.getItem("token");
    if (stored) {
      try {
        const decoded = atob(stored);
        if (decoded) config.headers.Authorization = `Bearer ${decoded}`;
      } catch {
        config.headers.Authorization = `Bearer ${stored}`;
      }
    }
    return config;
  });
  return instance;
};

export const axiosRegisterInstance = attachAuthInterceptor(
  axios.create({
    baseURL: "http://localhost:8082/api",
    timeout: 10000,
  })
);

export const axiosInstance = attachAuthInterceptor(
  axios.create({
    baseURL: "http://localhost:8080/api",
    timeout: 10000,
  })
);

export const axiosAuthInstance = attachAuthInterceptor(
  axios.create({
    baseURL: "http://localhost:8081/api",
    timeout: 10000,
  })
);
