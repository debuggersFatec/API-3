import { Box, Text } from "@chakra-ui/react";
import type { SimpleTaskProps } from "./QuadroDisplay";
import { AvatarUser } from "./AvatarUser";

interface QuadroItemProps {
  task: SimpleTaskProps;
}

export const QuadroItem = ({ task }: QuadroItemProps) => {
  return (
    <Box
      w={"226px"}
      h={"40px"}
      border={"1px solid #71717a"}
      display={"flex"}
      justifyContent={"space-between"}
      alignItems={"center"}
      px={"8px"}
      mb={"8px"}
      borderRadius={"5px"}
    >
      <Text fontSize={"14px"}>{task.title}</Text>
      <AvatarUser
        name={task.responsavel.name}
        imageUrl={task.responsavel.img}
        size="2xs"
      />
    </Box>
  );
};
