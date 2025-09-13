import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { EquipeTabs } from "./EquipeTabs";

interface Equipe {
  uuid: string;
  name: string;
}

export const EquipeDashboard = ({ equipe }: { equipe: Equipe }) => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title={equipe.name} />
      <EquipeTabs />
    </Box>
  );
};
