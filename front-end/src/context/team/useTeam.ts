import { useContext } from "react";
import { TeamContext } from "./TeamContext";

export const useTeam = () => {
  const ctx = useContext(TeamContext);
  if (!ctx) {
    throw new Error("useTeam deve ser usado dentro de um TeamProvider");
  }
  return ctx;
};