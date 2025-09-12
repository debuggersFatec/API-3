import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";

export const CompletasTab = () => {
  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Completas" />
      <CheckList />
    </Box>
  );
};
