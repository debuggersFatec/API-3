import { Flex, Heading, VStack } from "@chakra-ui/react";
import { useDroppable } from "@dnd-kit/core";

import { TaskCard } from "./TaskCard";
import type { TaskProject } from "@/types/task";
import type { Column as ColumnType } from "./QuadroDisplay";

interface ColumnProps {
  column: ColumnType;
  tasks: TaskProject[];
}

export const Column = ({ column, tasks }: ColumnProps) => {
  const { setNodeRef } = useDroppable({ id: column.id });

  return (
    <Flex
      flex="1"
      direction="column"
      border="1px #ccc solid"
      p={4}
      borderRadius="md"
    >
      <Heading size="md" mb={4}>
        {column.title}
      </Heading>
      <VStack
        ref={setNodeRef}
        align="stretch"
        minHeight="300px"
        borderRadius="md"
        p={1}
      >
        {tasks.map((task) => (
          <TaskCard key={task.uuid} task={task} />
        ))}
      </VStack>
    </Flex>
  );
};
