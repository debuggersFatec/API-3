import type { Notification } from "@/types/notification";
import { formatRelativeForNotification } from "@/utils/formatters";
import {
  Flex,
  HStack,
  VStack,
  Text,
  Badge,
  IconButton,
  Stack,
} from "@chakra-ui/react";
import { useColorModeValue } from "@/components/ui/color-mode";
import { useState, type ReactNode } from "react";
import { GoDotFill } from "react-icons/go";
import { IoMdClose } from "react-icons/io";
import { notificationServices } from "@/services/notificationServices";
import { useAuth } from "@/context/auth/useAuth";
import { taskService } from "@/services";
import { projectServices } from "@/services/ProjectServices";
import type { Task } from "@/types/task";
import type { UserRef } from "@/types/user";
import { toast } from "@/utils/toast";
import { ModalEditTask } from "./ModalEditTask";
import { ModalDisplayTask } from "./ModalDisplayTask";

type NotificationItemProps = {
  notification: Notification;
};

const TYPE_LABELS: Record<string, string> = {
  TASK_CREATED: "Tarefa criada",
  TASK_UPDATED: "Tarefa atualizada",
  TASK_DELETED: "Tarefa excluída",
  TASK_DUE_SOON: "Prazo próximo",
  TASK_ASSIGNED: "Você foi atribuído",
  TASK_UNASSIGNED: "Atribuição removida",
  TASK_COMMENT: "Comentário",
  TEAM_MEMBER_JOINED: "Membro entrou",
  TEAM_MEMBER_LEFT: "Membro saiu",
  PROJECT_MEMBER_JOINED: "Entrou no projeto",
  PROJECT_MEMBER_LEFT: "Saiu do projeto",
};

export const NotificationItem = ({ notification }: NotificationItemProps) => {
  const { refreshUser } = useAuth();
  const { token } = useAuth();
  const [modalOpen, setModalOpen] = useState(false);
  const [taskData, setTaskData] = useState<Task>();
  const [members, setMembers] = useState<UserRef[]>([]);

  const bg = useColorModeValue("white", "gray.700");
  const muted = useColorModeValue("gray.600", "gray.300");
  const border = useColorModeValue("gray.200", "gray.600");
  const unreadBg = useColorModeValue("blue.50", "gray.500");

  const handleMarkRead = () => {
    notificationServices.markAsRead(notification.uuid);
    refreshUser();
  };

  const handleDelete = () => {
    notificationServices.delete(notification.uuid);
    refreshUser();
  };

  const title = TYPE_LABELS[notification.type] ?? notification.type;

  const handleOpenModal = async () => {
    try {
      if (!notification.taskUuid) return;
      const fetched = await taskService.getTaskById(
        notification.taskUuid,
        token
      );
      setTaskData(fetched);

      if (fetched?.project_uuid) {
        const proj = await projectServices.getProjectByUuid(
          fetched.project_uuid
        );
        setMembers(proj.members ?? []);
      }

      setModalOpen(true);
    } catch (err) {
      toast("error", "Erro ao buscar detalhes da tarefa.");
      console.error("Erro ao buscar task:", err);
    }
  };

  return (
    <>
      <Flex
        p={3}
        borderRadius="md"
        bg={notification.read ? bg : unreadBg}
        borderWidth={1}
        borderColor={border}
        boxShadow="sm"
        _hover={{ transform: "translateY(-2px)", boxShadow: "md" }}
        transition="all 120ms ease"
        gap={3}
      >
        <VStack
          align="stretch"
          flex={1}
          onClick={handleOpenModal}
          cursor={notification.taskUuid && "pointer"}
        >
          <HStack gap={3} align="start">
            <Text fontWeight={600}>{title}</Text>
            {(() => {
              const parts = (notification.type || "").split("_");
              const entity = parts.find((p) =>
                ["TASK", "PROJECT", "TEAM"].includes(p)
              );
              const action = parts.find((p) =>
                [
                  "CREATED",
                  "UPDATED",
                  "DELETED",
                  "DUE",
                  "SOON",
                  "ASSIGNED",
                  "UNASSIGNED",
                  "COMMENT",
                  "JOINED",
                  "LEFT",
                ].includes(p)
              );

              const entityMap: Record<
                string,
                { label: string; color: string }
              > = {
                TASK: { label: "Tarefa", color: "green" },
                PROJECT: { label: "Projeto", color: "purple" },
                TEAM: { label: "Equipe", color: "blue" },
              };

              const actionMap: Record<
                string,
                { label: string; color: string }
              > = {
                CREATED: { label: "Criado", color: "green" },
                UPDATED: { label: "Atualizado", color: "yellow" },
                DELETED: { label: "Excluído", color: "red" },
                DUE: { label: "Prazo", color: "orange" },
                SOON: { label: "Próximo", color: "orange" },
                ASSIGNED: { label: "Atribuído", color: "teal" },
                UNASSIGNED: { label: "Removido", color: "gray" },
                COMMENT: { label: "Comentário", color: "cyan" },
                JOINED: { label: "Entrou", color: "green" },
                LEFT: { label: "Saiu", color: "red" },
              };

              const badges: ReactNode[] = [];
              if (entity && entityMap[entity]) {
                badges.push(
                  <Badge
                    key={"entity"}
                    colorPalette={entityMap[entity].color}
                    variant="subtle"
                    fontSize="2xs"
                  >
                    {entityMap[entity].label}
                  </Badge>
                );
              }
              if (action && actionMap[action]) {
                badges.push(
                  <Badge
                    key={"action"}
                    colorPalette={actionMap[action].color}
                    variant="subtle"
                    fontSize="2xs"
                  >
                    {actionMap[action].label}
                  </Badge>
                );
              }

              if (badges.length === 0) {
                if (notification.projectUuid) {
                  badges.push(
                    <Badge
                      key="proj-fallback"
                      colorPalette="purple"
                      variant="subtle"
                      fontSize="xs"
                    >
                      Projeto
                    </Badge>
                  );
                }
                if (notification.uuid) {
                  badges.push(
                    <Badge
                      key="task-fallback"
                      colorPalette="green"
                      variant="subtle"
                      fontSize="xs"
                    >
                      Tarefa
                    </Badge>
                  );
                }
              }

              return badges;
            })()}
          </HStack>

          <Text fontSize="sm" color={muted}>
            {notification.message}
          </Text>
        </VStack>
        <Stack
          align="center"
          gap={2}
          w="72px"
          minW="72px"
          maxW="72px"
          justify="flex-end"
          alignItems={"flex-end"}
        >
          <Text fontSize="xs" color={muted} whiteSpace="nowrap">
            {formatRelativeForNotification(notification.createdAt)}
          </Text>
          <IconButton
            aria-label={notification.read ? "Já lida" : "Marcar como lida"}
            size="sm"
            variant="ghost"
            onClick={handleMarkRead}
            visibility={!notification.read ? "visible" : "hidden"}
          >
            <GoDotFill color="#2F80ED" />
          </IconButton>

          <IconButton
            aria-label="Remover"
            size="sm"
            variant="ghost"
            colorPalette="red"
            onClick={handleDelete}
          >
            <IoMdClose />
          </IconButton>
        </Stack>
      </Flex>
      {taskData &&
        (taskData.status === "DELETED" ? (
          <ModalDisplayTask
            task={taskData}
            open={modalOpen}
            onClose={() => setModalOpen(false)}
          />
        ) : (
          <ModalEditTask
            open={modalOpen}
            task={taskData}
            members={members}
            onClose={() => {
              setModalOpen(false);
            }}
          />
        ))}
    </>
  );
};
