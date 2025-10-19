// src/context/TeamContext.tsx

import { createContext } from "react";
import type { Team } from "@/types/team";

export type TeamContextValue = {
  teamData?: Team;
  name: string;
  isLoading: boolean;
  fetchTeam: (uuid: string) => Promise<void>;
  refreshTeam: () => Promise<void>;
};

export const TeamContext = createContext<TeamContextValue | undefined>(undefined);