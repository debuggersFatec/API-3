import type { ProjectRef } from "@/types/project";
import { Grid, Box } from "@chakra-ui/react";
import { ProjectCard } from "./ProjectDisplayItem";
import { NewProjectCard } from "./NewProjectCard";

interface ProjectsDisplayProps {
  projects: ProjectRef[];
}

export const ProjectsDisplay = ({ projects }: ProjectsDisplayProps) => {
  return (
    <Grid
      templateColumns="repeat(auto-fit, minmax(260px, 1fr))"
      gap={{ base: 3, md: 4 }}
      justifyItems="center"
      alignItems="stretch"
      gridAutoRows="1fr"
      px={{ base: 4, md: 8 }}
      py={6}
      width="100%"
      maxW={{ base: "100%", lg: "1200px" }}
      mx="auto"
    >
      <Box w="100%" h="100%" maxW={{ base: "100%", md: "420px" }} mx="auto">
        <NewProjectCard />
      </Box>

      {projects.map((p) => (
        <Box key={p.uuid} w="100%" h="100%" maxW={{ base: "100%", md: "420px" }} mx="auto">
          <ProjectCard project={p} />
        </Box>
      ))}
    </Grid>
  );
};
