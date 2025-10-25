"use client";

import { Text, Box, Badge, HStack, VStack, Spacer } from "@chakra-ui/react";
import { useState } from "react";
import { ModalEditTask } from "./ModalEditTask";
import type { Task, TaskProject, TaskUser } from "@/types/task";
import { useAuth } from "@/context/auth/useAuth";
import { taskService } from "@/services";
import {
  formatDateShort,
  formatStatus,
  formatPriority,
} from "@/utils/formatters";
import { AvatarUser } from "./AvatarUser";
import { toast } from "@/utils/toast";

interface CheckItemProps {
  task: TaskProject | TaskUser;
  isUserArea?: boolean;
}

export const CheckListItem = ({ task, isUserArea }: CheckItemProps) => {
  const { token, user } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [taskData, setTaskData] = useState<Task>();

  const handleOpenModal = async () => {
    setModalOpen(true);
    try {
      setTaskData(await taskService.getTaskById(task.uuid, token));
    } catch (err) {
      toast("error", "Erro ao buscar detalhes da tarefa.");
      console.error("Erro ao buscar task:", err);
    }
  };

  const colorPalet = {
    status: {
      IN_PROGRESS: { bg: "yellow.100", color: "yellow.800" },
      COMPLETED: { bg: "green.100", color: "green.800" },
      DEFAULT: { bg: "gray.100", color: "gray.800" },
    },
    priority: {
      HIGH: { bg: "red.100", color: "red.800" },
      MEDIUM: { bg: "orange.100", color: "orange.800" },
      LOW: { bg: "green.100", color: "green.800" },
      DEFAULT: { bg: "blue.100", color: "blue.800" },
    },
  } as const;

  return (
    <>
      <Box
        borderWidth={1}
        borderRadius="md"
        p={3}
        mb={2}
        _hover={{ bg: "gray.50" }}
        cursor="pointer"
        onClick={handleOpenModal}
      >
        <HStack align="start">
          <VStack align="stretch" gap={1} flex={1}>
            <Text fontWeight="bold" fontSize="md">
              {task.title}
            </Text>

            <HStack gap={2}>
              {(() => {
                const s =
                  (task.status &&
                    (colorPalet.status[
                      task.status as keyof typeof colorPalet.status
                    ] as { bg: string; color: string } | undefined)) ||
                  colorPalet.status.DEFAULT;
                return (
                  <Badge
                    bg={s.bg}
                    color={s.color}
                    borderRadius="md"
                    px={2}
                    py={0.5}
                    fontSize="xs"
                  >
                    {formatStatus(task.status)}
                  </Badge>
                );
              })()}

              {(() => {
                const p =
                  (task.priority &&
                    (colorPalet.priority[
                      task.priority as keyof typeof colorPalet.priority
                    ] as { bg: string; color: string } | undefined)) ||
                  colorPalet.priority.DEFAULT;
                return (
                  <Badge
                    bg={p.bg}
                    color={p.color}
                    borderRadius="md"
                    px={2}
                    py={0.5}
                    fontSize="xs"
                  >
                    {formatPriority(task.priority)}
                  </Badge>
                );
              })()}
              <Text color="gray.500" fontSize="sm">
                {task.due_date ? formatDateShort(task.due_date) : "Sem prazo"}
              </Text>
            </HStack>
          </VStack>

          <Spacer />

          <VStack align="end" gap={0}>
            {isUserArea && user ? (
              <>
                <AvatarUser user={user} size="xs" />
                <Text fontSize="sm">{user.name}</Text>
              </>
            ) : task.responsible ? (
              <>
                <AvatarUser user={task.responsible} size="xs" />
                <Text fontSize="sm">{task.responsible.name}</Text>
              </>
            ) : (
              <Text color="gray.500" fontSize="sm">
                Sem responsável
              </Text>
            )}
          </VStack>
        </HStack>
      </Box>

      {taskData && (
        <ModalEditTask
          open={modalOpen}
          task={taskData}
          onClose={() => {
            setModalOpen(false);
          }}
        />
      )}
    </>
  );
};
