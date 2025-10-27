import { createContext } from "react";
import type { Project } from "@/types/project";

export type ProjectContextValue = {
  project?: Project;
  isLoading: boolean;
  setProject: (p?: Project) => void;
  refreshProject: (projectUuid?: string) => Promise<void>;
  /**
   * Fetch project by uuid and return the full Project if fetched and accessible,
   * otherwise return undefined.
   */
  fetchProject: (projectUuid: string) => Promise<Project | undefined>;
};

export const ProjectContext = createContext<ProjectContextValue | undefined>(undefined);