import { useContext } from "react";
import { ProjectContext } from "./ProjectContext";

export const useProject = () => {
  const ctx = useContext(ProjectContext);
  if (!ctx) throw new Error("useProject deve ser usado dentro de um ProjectProvider");
  return ctx;
};