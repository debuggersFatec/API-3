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

import type { Task } from "@/types/task";
import { useAuth } from "@/context/useAuth";

type Member = {
  uuid: string;
  img: string;
  name: string;
};

interface CheckItemProps {
  title: string;
  uuid: string;
  status: "not-started" | "in-progress" | "completed";
  due_date?: string;
  task?: Task;
  equipe_uuid?: string;
  membros?: Member[];
}

export const CheckListItem = ({
  title,
  status,
  uuid,
  equipe_uuid,
  membros,
}: CheckItemProps) => {
  const { token } = useAuth();
  const [checked, setChecked] = useState(status === "completed");
  const [modalOpen, setModalOpen] = useState(false);
  const [taskData, setTaskData] = useState<Task | null>(null);

  const handleOpenModal = async () => {
    setTaskData(null); // sempre limpa antes de abrir
    setModalOpen(true); // abre imediatamente (mostra loading)
    try {
      const response = await axios.get(
        `http://localhost:8080/api/tasks/${uuid}`,
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
          {title}
        </Text>
      </Flex>
      <ModalEditTask
        open={modalOpen}
        task={taskData}
        equipe_uuid={equipe_uuid || ""}
        membros={membros || []}
        onClose={() => {
          setModalOpen(false);
          setTaskData(null);
        }}
      />
    </>
  );
};
