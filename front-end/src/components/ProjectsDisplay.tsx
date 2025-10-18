import type { ProjectRef } from "@/types/project";
import { Grid } from "@chakra-ui/react";
import { ProjectCard } from "./ProjectDisplayItem";
import { NewProjectCard } from "./NewProjectCard";

interface ProjectsDisplayProps {
  projects: ProjectRef[];
}

export const ProjectsDisplay = ({ projects }: ProjectsDisplayProps) => {
  return (
    <Grid>
      <NewProjectCard />
      {projects.map((p) => (
        <ProjectCard key={p.uuid} project={p} />
      ))}
    </Grid>
  );
};
