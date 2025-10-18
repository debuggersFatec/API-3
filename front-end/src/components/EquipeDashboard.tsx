import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { EquipeTabs } from "./EquipeTabs";
import { useTeam } from "@/context/team/useTeam";
import { useProject } from "@/context/project/useProject";
import type { TeamRef } from "@/types/team";
import { ProjectsDisplay } from "./ProjectsDisplay";

export const EquipeDashboard = ({
  team,
}: {
  team: TeamRef;
  isActive: boolean;
}) => {
  const { teamData } = useTeam();
  const { project } = useProject();
  const { isLoading } = useTeam();

  if (isLoading) return <div>Carregando...</div>;

  return (
    <>
      <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
        <SectionHeader
          title={team.name}
          isTeamSection={true}
          project={project}
        />
        {project === undefined && teamData ? (
          <ProjectsDisplay projects={teamData.projects || []} />
        ) : (
          <EquipeTabs />
        )}
      </Box>
    </>
  );
};
