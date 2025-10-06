import { Box, Text } from "@chakra-ui/react";
import { ProximasTasksItem } from "./ProximasTasksItem";
import type { TaskProject } from "@/types/task";

interface ProximasTasksProps {
  tasks: TaskProject[];
}

export const ProximasTasks = ({ tasks }: ProximasTasksProps) => {
  if (!tasks || tasks.length === 0) {
    return (
      <Box
        w={"100%"}
        h={"100%"}
        border={"1px solid"}
        borderColor={"gray.200"}
        borderRadius={"8px"}
        p={"16px"}
      >
        <Text fontWeight="bold" mb={8}>
          Próximas Tasks
        </Text>
        <Text>Sem tarefas para mostrar</Text>
      </Box>
    );
  }

  const proximas10Tasks = [...tasks]
    .filter(
      (task) => task.status === "not-started" || task.status === "in-progress"
    )
    .sort((a, b) => {
      if (!a.due_date && !b.due_date) return 0;
      if (!a.due_date) return 1;
      if (!b.due_date) return -1;
      return +new Date(a.due_date) - +new Date(b.due_date);
    })
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
