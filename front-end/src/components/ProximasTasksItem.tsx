import { Box, Text } from "@chakra-ui/react";
import { parseISO, isBefore, startOfToday } from "date-fns";

interface ProximasTasksItemProps {
  task: {
    uuid: string;
    responsavel: { name: string };
    due_date: string;
  };
}

export const ProximasTasksItem = ({ task }: ProximasTasksItemProps) => {
  const hoje = startOfToday();
  const dataDaTask = parseISO(task.due_date);
  const estaAtrasada = isBefore(dataDaTask, hoje);

  return (
    <Box
      key={task.uuid}
      display={"flex"}
      justifyContent={"space-between"}
      alignItems={"center"}
      bg={estaAtrasada ? "red.100" : "transparent"}
      p={2}
      borderRadius="md"
    >
      <Text fontSize={"12px"} >
        {task.responsavel.name}
      </Text>
      <Text fontSize={"12px"} color={"gray.600"}>
        {new Date(task.due_date).toLocaleDateString("pt-BR", {
          day: "2-digit",
          month: "2-digit",
          timeZone: "UTC",
        })}
      </Text>
    </Box>
  );
};
