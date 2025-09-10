import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";

export const MyTasks = () => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Minhas tarefas" />
    </Box>
  );
};
