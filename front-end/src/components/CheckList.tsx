import { VStack } from "@chakra-ui/react";
import { CheckListItem } from "./CheckListItem";
import type { TaskTeam } from "@/types/task";
import type { tasksUser } from "@/context/authUtils";

interface CheckListProps {
  tasks: TaskTeam[] | tasksUser[] | undefined;
}

export const CheckList = ({ tasks }: CheckListProps) => {
  if (!tasks || tasks.length === 0) {
    return "Sem tarefas para mostrar";
  }
  return (
    <VStack gap={4} align="stretch" w="100%" px={4} py={2}>
      {tasks.map((task) => (
        <CheckListItem
          key={task.uuid}
          title={task.title}
          uuid={task.uuid}
          status={task.status}
        />
      ))}
    </VStack>
  );
};
