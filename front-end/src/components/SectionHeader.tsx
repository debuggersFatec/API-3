import { Box, Text, Separator, Dialog } from "@chakra-ui/react";
import { Filtergroup } from "./Filtergroup";
import { ModalNewTask } from "./ModalNewTask";

interface SectionHeaderProps {
  title: string;
  isTeamSection?: boolean;
}

export const SectionHeader = ({ title, isTeamSection }: SectionHeaderProps) => {
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
          {isTeamSection && (
            <Dialog.Root placement={"center"}>
              <Dialog.Trigger asChild></Dialog.Trigger>
              <ModalNewTask />
            </Dialog.Root>
          )}
          <Filtergroup />
        </Box>
        <Separator mt={"24px"} />
      </Box>
    </>
  );
};
