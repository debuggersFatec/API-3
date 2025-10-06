"use client";

import { Flex, Text } from "@chakra-ui/react";
import {
  CheckboxRoot,
  CheckboxControl,
  CheckboxHiddenInput,
} from "@chakra-ui/react/checkbox";
import { useState } from "react";
import axios from "axios";

import { ModalEditTask } from "./ModalEditTask";

import type { Task, TaskProject } from "@/types/task";
import { useAuth } from "@/context/useAuth";
import type { tasksUser } from "@/context/authUtils";

type Member = {
  uuid: string;
  img: string;
  name: string;
};

interface CheckItemProps {
  task: TaskProject | tasksUser;
  membros?: Member[];
}

export const CheckListItem = ({ task, membros }: CheckItemProps) => {
  const { token } = useAuth();
  const [checked, setChecked] = useState(status === "completed");
  const [modalOpen, setModalOpen] = useState(false);
  const [taskData, setTaskData] = useState<Task>();

  const handleOpenModal = async () => {
    setModalOpen(true); // abre imediatamente (mostra loading)
    try {
      const response = await axios.get(
        `http://localhost:8080/api/tasks/${task.uuid}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setTaskData(response.data);
    } catch (err) {
      // Trate o erro conforme necessário
      console.error("Erro ao buscar task:", err);
    }
  };

  return (
    <>
      <Flex align="center" gap={2}>
        <CheckboxRoot
          checked={checked}
          onCheckedChange={(e) => setChecked(!!e.checked)}
        >
          <CheckboxHiddenInput />
          <CheckboxControl />
        </CheckboxRoot>
        <Text
          onClick={handleOpenModal}
          style={{ cursor: "pointer" }}
          fontWeight={checked ? "normal" : "semibold"}
          textDecoration={checked ? "line-through" : "none"}
        >
          {task.title}
        </Text>
      </Flex>
      {taskData && (
        <ModalEditTask
          open={modalOpen}
          task={taskData}
          membros={membros || []}
          onClose={() => {
            setModalOpen(false);
          }}
        />
      )}
    </>
  );
};
