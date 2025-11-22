import { useEffect, useState } from "react";
import { Flex } from "@chakra-ui/react";
import { DndContext } from "@dnd-kit/core";
import type { DragEndEvent } from "@dnd-kit/core";

import { organizarTarefas } from "../../utils/organizarTarefas";

import { Column } from "./Column";
import type { TaskProject, Status } from "@/types/task";
import { useProject } from "@/context/project/useProject";
import { useAuth } from "@/context/auth/useAuth";
import { taskService } from "@/services";
import { toast } from "@/utils/toast";
import type { Task } from "../../types/task";
import { ModalRequerideFile } from "../ModalRequerideFile";

export interface Column {
  id: string;
  title: string;
  taskIds: string[];
}
export interface BoardData {
  tasks: Record<string, TaskProject>;
  columns: Record<string, Column>;
  columnOrder: string[];
}

export interface QuadroDisplayProps {
  tasks: TaskProject[];
}

export const QuadroDisplay = ({ tasks }: QuadroDisplayProps) => {
  const { refreshProject } = useProject();
  const { refreshUser } = useAuth();
  const { token } = useAuth();
  const [boardData, setBoardData] = useState<BoardData>({
    tasks: {},
    columns: {},
    columnOrder: [],
  });

  const [riquiredFileTaskUuid, setRiquiredFileTaskUuid] = useState<string>();
  const [isOpen, setOpen] = useState(false);

  useEffect(() => {
    setBoardData(organizarTarefas(tasks));
  }, [tasks]);

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!over) return;

    const taskId = active.id as string;
    const destColumnId = over.id as string;

    const startColumnId = boardData.columnOrder.find((colId) =>
      boardData.columns[colId].taskIds.includes(taskId)
    );

    if (!startColumnId || startColumnId === destColumnId) return;

    // Map column IDs to backend UPPERCASE status enums
    const newStatus: Status =
      destColumnId === "nao-iniciada"
        ? "NOT_STARTED"
        : destColumnId === "em-progresso"
          ? "IN_PROGRESS"
          : "COMPLETED";

    // snapshot para rollback
    const prevData = boardData;
    if (
      destColumnId === "concluida" &&
      tasks.filter((task) => task.uuid === taskId)[0].is_required_file
    ) {
      setRiquiredFileTaskUuid(taskId);
      setOpen(true);
      return;
    }

    // montar novo estado otimista
    const newStartTaskIds = prevData.columns[startColumnId].taskIds.filter(
      (id) => id !== taskId
    );
    const newDestTaskIds = [...prevData.columns[destColumnId].taskIds, taskId];

    const updatedTask: TaskProject = {
      ...prevData.tasks[taskId],
      status: newStatus,
    };

    const optimistic: BoardData = {
      ...prevData,
      tasks: {
        ...prevData.tasks,
        [taskId]: updatedTask,
      },
      columns: {
        ...prevData.columns,
        [startColumnId]: {
          ...prevData.columns[startColumnId],
          taskIds: newStartTaskIds,
        },
        [destColumnId]: {
          ...prevData.columns[destColumnId],
          taskIds: newDestTaskIds,
        },
      },
    };

    setBoardData(optimistic);

    // enviar update para o servidor; reverter se falhar
    (async () => {
      try {
        await taskService.updateTask(taskId, updatedTask as Task, token);
        await refreshProject();
        await refreshUser();
        toast("success", "Tarefa movida com sucesso.");
      } catch (err) {
        console.error("Erro ao atualizar task no servidor:", err);
        toast("error", "Erro ao mover tarefa.");
        setBoardData(prevData); // rollback
      }
    })();
  };

  return (
    <>
      <ModalRequerideFile
        isOpen={isOpen}
        onClose={() => setOpen(false)}
        taskUuid={riquiredFileTaskUuid!}
        
      />
      <DndContext onDragEnd={handleDragEnd}>
        <Flex gap={5} p={5} w="100%">
          {boardData.columnOrder.map((columnId) => {
            const column = boardData.columns[columnId];
            if (!column) return null;
            const columnTasks = column.taskIds.map(
              (taskId) => boardData.tasks[taskId]
            );
            return (
              <Column key={column.id} column={column} tasks={columnTasks} />
            );
          })}
        </Flex>
      </DndContext>
    </>
  );
};
