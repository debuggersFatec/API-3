import { Box, Text, Button, Dialog , Portal } from "@chakra-ui/react";
import { useDraggable } from "@dnd-kit/core";
import { AvatarUser } from "../AvatarUser";
import type { Task } from "@/types/task";
import { useState, useRef } from "react";

interface TaskCardProps {
  task: Task;
  onDelete: (id: string) => void;
}

export const TaskCard = ({ task, onDelete }: TaskCardProps) => {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: task.uuid,
  });

  const [isOpen, setIsOpen] = useState(false);

  const handleDelete = async () => {
    try {
      await fetch(`http://localhost:5173/tasks/${task.uuid}`, {
        method: "DELETE",
      });
      onDelete(task.uuid);
      setIsOpen(false);
    } catch (err) {
      console.error("Erro ao deletar tarefa:", err);
    }
  };

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

      {task.responsavel && (
        <AvatarUser
          name={task.responsavel.name}
          imageUrl={task.responsavel.img}
          size="2xs"
        />
      )}

      <Button size="xs" colorScheme="red" ml={2} onClick={() => setIsOpen(true)}>
        Deletar
      </Button>

      <Dialog.Root 
        open={isOpen} 
        onOpenChange={(details) => setIsOpen(details.open)}
      >
        <Portal>
          <Dialog.Backdrop />
          <Dialog.Positioner>
            <Dialog.Content>
              <Dialog.Header>Confirmar Exclusão</Dialog.Header>
              <Dialog.Body>
                Deseja realmente excluir a tarefa "{task.title}"?
              </Dialog.Body>
              <Dialog.Footer>
                <Dialog.ActionTrigger asChild>
                  <Button variant="outline">Cancelar</Button>
                </Dialog.ActionTrigger>
                <Button colorScheme="red" onClick={handleDelete} ml={3}>
                  Deletar
                </Button>
              </Dialog.Footer>
              <Dialog.CloseTrigger />
            </Dialog.Content>
          </Dialog.Positioner>
        </Portal>
      </Dialog.Root>
    </Box>
  );
};