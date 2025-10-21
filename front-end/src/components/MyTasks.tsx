import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";
import { useAuth } from "@/context/auth/useAuth";

export const MyTasks = () => {
  const { user } = useAuth();
  const filteredTasks =
    user?.tasks?.filter((task) => task.status !== "deleted") || [];
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Minhas tarefas" />
      <CheckList tasks={filteredTasks} />
    </Box>
  );
};
