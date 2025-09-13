import { useEffect, useState } from "react";
import { Flex } from "@chakra-ui/react";
import { DndContext } from "@dnd-kit/core";
import type { DragEndEvent } from "@dnd-kit/core";

import type { BoardData } from "../../types/task";
import { organizarTarefas } from "../../utils/organizarTarefas";
import { tasks } from "../../data/tasks";
import { Column } from "./Column";

export const QuadroDisplay = () => {
  const [boardData, setBoardData] = useState<BoardData>({
    tasks: {},
    columns: {},
    columnOrder: [],
  });

  useEffect(() => {
    setBoardData(organizarTarefas(tasks));
  }, []);

  const handleDragEnd = (event: DragEndEvent) => {
    const { active, over } = event;
    if (!over) return;

    const taskId = active.id as string;
    const destColumnId = over.id as string;

    const startColumnId = boardData.columnOrder.find((colId) =>
      boardData.columns[colId].taskIds.includes(taskId)
    );

    if (!startColumnId || startColumnId === destColumnId) return;

    const newStatus =
      destColumnId === "nao-atribuidas"
        ? "not-started"
        : destColumnId === "atribuido"
        ? "in-progress"
        : "completed";

    setBoardData((prevData) => {
      const newStartTaskIds = prevData.columns[startColumnId].taskIds.filter(
        (id) => id !== taskId
      );
      const newDestTaskIds = [...prevData.columns[destColumnId].taskIds, taskId];

      return {
        ...prevData,
        tasks: {
          ...prevData.tasks,
          [taskId]: { ...prevData.tasks[taskId], status: newStatus },
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
    });
  };

  return (
    <DndContext onDragEnd={handleDragEnd}>
      <Flex gap={5} p={5} w="100%">
        {boardData.columnOrder.map((columnId) => {
          const column = boardData.columns[columnId];
          if (!column) return null;
          const columnTasks = column.taskIds.map(
            (taskId) => boardData.tasks[taskId]
          );
          return <Column key={column.id} column={column} tasks={columnTasks} />;
        })}
      </Flex>
    </DndContext>
  );
};
