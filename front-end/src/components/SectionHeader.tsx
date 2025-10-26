import { Box, Text, Separator } from "@chakra-ui/react";
import type { Project } from "@/types/project";
import { InviteLinkButton } from "./InviteLinkButton";
import { useTeam } from "@/context/team/useTeam";

interface SectionHeaderProps {
  title: string;
  isTeamSection?: boolean;
  project?: Project;
}

export const SectionHeader = ({
  title,
  isTeamSection,
}: SectionHeaderProps) => {
  const { teamData } = useTeam(); // Use useTeam para obter teamUuid para invite

  return (
    <>
      <Box w={"100%"} mb={"24px"} mt={"24px"} pr={"32px"}>
        <Text textStyle={"2xl"} fontWeight="bold" mb={"20px"}>
          {title}
        </Text>
        <Box
          display={"flex"}
          flexDirection={"row"}
          gap="10px"
        >
          {isTeamSection && teamData && (
            // Renderiza o botão de convite para a equipe atual
            <InviteLinkButton teamUuid={teamData.uuid} />
          )}
        </Box>
        <Separator mt={"24px"} />
      </Box>
    </>
  );
};
