import type { Status } from "@/types/task";

export const formatStatus = (status?: Status | string): string => {
  if (!status) return "—";
  const s = String(status).toUpperCase();
  switch (s) {
    case "NOT_STARTED":
      return "Não iniciada";
    case "IN_PROGRESS":
      return "Em progresso";
    case "COMPLETED":
      return "Concluída";
    case "DELETED":
      return "Removida";
    default:
      return s;
  }
};
