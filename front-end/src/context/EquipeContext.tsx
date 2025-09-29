import { createContext, useContext } from "react";
import type { EquipeData } from "@/types/equipe";

export interface EquipeContextType {
  equipeData: EquipeData | undefined;
  setEquipeData: (data: EquipeData) => void;
  name: string;
  setName: (name: string) => void;
  isLoading: boolean;
  setIsLoading: (b: boolean) => void;
  fetchEquipe: () => Promise<void>;
  refreshEquipe: () => Promise<void>;
}

export const EquipeContext = createContext<EquipeContextType | undefined>(undefined);

export const useEquipe = () => {
  const ctx = useContext(EquipeContext);
  if (!ctx) throw new Error("useEquipe deve ser usado dentro de EquipeContext.Provider");
  return ctx;
};
