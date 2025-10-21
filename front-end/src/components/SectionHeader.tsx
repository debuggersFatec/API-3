import { Box, Text, Separator, Dialog } from "@chakra-ui/react";
import { ModalNewTask } from "./ModalNewTask";
import type { Project } from "@/types/project";

interface SectionHeaderProps {
  title: string;
  isTeamSection?: boolean;
  project?: Project;
}

export const SectionHeader = ({
  title,
  isTeamSection,
  project,
}: SectionHeaderProps) => {
  return (
    <>
      <Box w={"100%"} mb={"24px"} mt={"24px"} px={"32px"}>
        <Text textStyle={"2xl"} fontWeight="bold" mb={"20px"}>
          {title}
        </Text>
        <Box
          display={"flex"}
          flexDirection={"row"}
          justifyContent={"space-between"}
        >
          {isTeamSection && project && (
            <Dialog.Root placement={"center"}>
              <Dialog.Trigger asChild></Dialog.Trigger>
              <ModalNewTask />
            </Dialog.Root>
          )}
        </Box>
        <Separator mt={"24px"} />
      </Box>
    </>
  );
};
