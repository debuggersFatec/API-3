import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";
import type { TaskUser } from "@/types/task";


interface CompletasTabProps {
  tasks?: TaskUser[];
}

export const CompletasTab = ({ tasks }: CompletasTabProps) => {
  const completas = tasks
    ? tasks.filter((t) => (t.status || "").toLowerCase() === "completed")
    : [];

  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Completas" />
      <CheckList tasks={completas} hideStatusFilter />
    </Box>
  );
};
