import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";
import { useAuth } from "@/context/useAuth";

export const MyTasks = () => {
  const { user } = useAuth();
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Minhas tarefas" />
      <CheckList tasks={user?.tasks} />
    </Box>
  );
};
