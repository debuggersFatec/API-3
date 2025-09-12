import { Box, Text } from "@chakra-ui/react";
import { QuadroItem } from "./QuadroItem";

export interface SimpleTaskProps {
  uuid: string;
  title: string;
  due_date: string;
  status: string;
  prioridade: string;
  equipe_uuid: string;
  responsavel: {
    uuid: string;
    name: string;
    img: string;
  };
}

interface QuadroDisplayProps {
  title: string;
  tasks: SimpleTaskProps[];
}

export const QuadroDisplay = ({ title, tasks }: QuadroDisplayProps) => {
  return (
    <Box
      border={"1px solid #71717a"}
      maxW={"250px"}
      minH={"350px"}
      p={"12px"}
      borderRadius={"5px"}
    >
      <Text fontSize={"14px"} color={"gray.500"} mb={"24px"}>
        {title}
      </Text>
      {tasks.map((task) => (
        <QuadroItem key={task.uuid} task={task} />
      ))}
    </Box>
  );
};
