import { Box, Text, Button, Separator } from "@chakra-ui/react";
import { Filtergroup } from "./Filtergroup";

interface SectionHeaderProps {
  title: string;
}

export const SectionHeader = ({ title }: SectionHeaderProps) => {
  return (
    <Box w={"100%"} mb={"24px"} mt={"24px"} px={"32px"}>
      <Text textStyle={"2xl"} fontWeight="bold" mb={"20px"}>
        {title}
      </Text>
      <Box
        display={"flex"}
        flexDirection={"row"}
        justifyContent={"space-between"}
      >
        <Button variant={"outline"}>Criar nova tarefa</Button>
        <Filtergroup></Filtergroup>
      </Box>
      <Separator mt={'24px'}/>
    </Box>
  );
};
