import { Box, Text, Separator, Flex } from "@chakra-ui/react";
import type { Project } from "@/types/project";
import { InviteLinkButton } from "./InviteLinkButton";
import { useTeam } from "@/context/team/useTeam";
import { ModalLeaveTeam } from "./ModalLeaveTeam";

interface SectionHeaderProps {
  title: string;
  isTeamSection?: boolean;
  project?: Project;
  setActiveTab: (tab: string) => void;
}

export const SectionHeader = ({
  title,
  isTeamSection,
  setActiveTab,
}: SectionHeaderProps) => {
  const { teamData } = useTeam(); // Use useTeam para obter teamUuid para invite

  return (
    <>
      <Box w={"100%"} mb={"24px"} mt={"24px"} pr={"32px"}>
        <Flex mb={"10px"} alignItems="center">
          <Text textStyle={"2xl"} fontWeight="bold" mr={4}>
            {title}
          </Text>
          {isTeamSection && (
            <ModalLeaveTeam setActiveTab={setActiveTab} />
          )}
        </Flex>

        <Box display={"flex"} flexDirection={"row"} gap="10px">
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
