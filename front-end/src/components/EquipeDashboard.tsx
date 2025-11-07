import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { EquipeTabs } from "./EquipeTabs";
import { useTeam } from "@/context/team/useTeam";
import { useProject } from "@/context/project/useProject";
import type { TeamRef } from "@/types/team";
import { ProjectsDisplay } from "./ProjectsDisplay";

export const EquipeDashboard = ({
  team,
  setActiveTab,
}: {
  team: TeamRef;
  isActive: boolean;
  setActiveTab: (tab: string) => void;
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
          setActiveTab={setActiveTab}
        />
        {project === undefined && teamData ? (
          <ProjectsDisplay projects={teamData.projects || []} />
        ) : (
          <EquipeTabs 
            setActiveTab={setActiveTab}
          />
        )}
      </Box>
    </>
  );
};
