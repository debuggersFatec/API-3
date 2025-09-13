import { Box, Text } from "@chakra-ui/react";
import { ProximasTasksItem } from "./ProximasTasksItem";
import { tasks } from "@/data/tasks";

export const ProximasTasks = () => {
  
  const proximas10Tasks = [...tasks]
  
    .sort((a, b) => +new Date(a.due_date) - +new Date(b.due_date))
    .slice(0, 10);

  return (
    <Box
      w={"100%"}
      h={"100%"}
      border={"1px solid"}
      borderColor={"gray.200"}
      borderRadius={"8px"}
      p={"16px"}
    >
      <Text fontWeight="bold" mb={4}>
        Próximas Tasks
      </Text>

      <Box display="flex" flexDirection="column" gap={2}>
        {proximas10Tasks.map((task) => (
          <ProximasTasksItem key={task.uuid} task={task} />
        ))}
      </Box>
    </Box>
  );
};
