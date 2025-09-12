import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";

export const VencidasTab = () => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Vencidas" />
      <CheckList />
    </Box>
  );
};
