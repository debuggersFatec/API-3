import type { Priority } from "@/types/task";

export const formatPriority = (priority?: Priority | string): string => {
  if (!priority) return "—";
  const p = String(priority).toUpperCase();
  switch (p) {
    case "LOW":
      return "Baixa";
    case "MEDIUM":
      return "Média";
    case "HIGH":
      return "Alta";
    default:
      return p;
  }
};
