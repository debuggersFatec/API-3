import { VStack } from "@chakra-ui/react";
import { CheckListItem } from "./CheckListItem";

interface UserTaskProps {
  uuid: string;
  title: string;
  description?: string;
  due_date: string;
  status: "not-started" | "in-progress" | "completed";
  prioridade: "baixa" | "média" | "alta";
  equipe_uuid: string;
}

const data: UserTaskProps[] = [
  {
    uuid: "task-123e4567-e89b-12d3-a456-426614174000",
    title: "Complete project report",
    description: "Finish the final report for the project by end of the week.",
    due_date: "2024-07-05",
    status: "in-progress",
    prioridade: "alta",
    equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
  },
  {
    uuid: "task-987e6543-e21b-12d3-a456-426614174999",
    title: "Criar esquema de banco de dados",
    due_date: "2024-07-10",
    status: "completed",
    prioridade: "média",
    equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
  },
];
export const CheckList = () => {
  return (
    <VStack gap={4} align="stretch" w="100%" px={4} py={2}>
      {data.map((task) => (
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
