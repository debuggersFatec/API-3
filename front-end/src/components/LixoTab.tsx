import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";

export const LixoTab = () => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Lixo" />
      <CheckList />
    </Box>
  );
};
