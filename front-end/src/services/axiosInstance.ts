import axios from "axios";

export const axiosInstance = axios.create({
  baseURL: "http://localhost:8080/api",
  timeout: 10000,
});

axiosInstance.interceptors.request.use((config) => {
  const stored = localStorage.getItem("token");
  if (stored) {
    try {
      const decoded = atob(stored);
      if (decoded) config.headers.Authorization = `Bearer ${decoded}`;
    } catch {
      // fallback if stored value is not base64
      config.headers.Authorization = `Bearer ${stored}`;
    }
  }
  return config;
});
