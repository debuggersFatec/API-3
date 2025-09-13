import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";

interface Equipe {
  uuid: string;
  name: string;
}

export const EquipeDashboard = ({ equipe }: { equipe: Equipe }) => {
  console.log("EquipeDashboard renderizado com equipe:", equipe);
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title={equipe.name} />
    </Box>
  );
};
