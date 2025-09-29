import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";
import type { tasksUser } from "@/context/authUtils";

interface CompletasTabProps {
  tasks?: tasksUser[];
}

export const CompletasTab = ({ tasks }: CompletasTabProps) => {
  const completas = tasks
    ? tasks.filter((t) => (t.status || "").toLowerCase() === "completed")
    : [];

  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Completas" />
      <CheckList tasks={completas} />
    </Box>
  );
};
