import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";

export const MyTasks = () => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Minhas tarefas" />
      <CheckList />
    </Box>
  );
};
