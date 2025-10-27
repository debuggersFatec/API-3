import { Box, Text } from "@chakra-ui/react";
import { useDraggable } from "@dnd-kit/core";
import { AvatarUser } from "../AvatarUser";
import type { TaskProject } from "@/types/task";

interface TaskCardProps {
  task: TaskProject;
}

export const TaskCard = ({ task }: TaskCardProps) => {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: task.uuid,
  });

  const style = transform
    ? {
        transform: `translate3d(${transform.x}px, ${transform.y}px, 0)`,
        boxShadow: "xl",
      }
    : undefined;

  return (
    <Box
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      bg="white"
      p={3}
      mb={3}
      borderRadius="md"
      boxShadow="sm"
      borderWidth="1px"
      cursor="grab"
      display="flex"
      alignItems="center"
      justifyContent="space-between"
    >
      <Text>{task.title}</Text>

      {task.responsible && (
        <AvatarUser
          user={task.responsible}
          size="2xs"
        />
      )}
    </Box>
  );
};
