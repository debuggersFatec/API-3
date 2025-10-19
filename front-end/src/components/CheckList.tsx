import { VStack } from "@chakra-ui/react";
import { CheckListItem } from "./CheckListItem";
import type { TaskProject, TaskUser } from "@/types/task";


interface CheckListProps {
  tasks: TaskProject[] | TaskUser[] | undefined;
}

export const CheckList = ({ tasks }: CheckListProps) => {
  if (!tasks || tasks.length === 0) {
    return "Sem tarefas para mostrar";
  }
  return (
    <VStack gap={4} align="stretch" w="100%" px={4} py={2}>
      {tasks.map((task) => (
        <CheckListItem key={task.uuid} task={task} />
      ))}
    </VStack>
  );
};
