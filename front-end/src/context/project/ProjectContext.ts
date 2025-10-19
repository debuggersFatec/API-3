import { createContext } from "react";
import type { Project } from "@/types/project";

export type ProjectContextValue = {
  project?: Project;
  isLoading: boolean;
  setProject: (p?: Project) => void;
  refreshProject: (projectUuid?: string) => Promise<void>;
  fetchProject: (projectUuid: string) => Promise<void>;
};

export const ProjectContext = createContext<ProjectContextValue | undefined>(undefined);