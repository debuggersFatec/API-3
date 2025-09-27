import * as Chakra from "@chakra-ui/react";
import type { Task } from "../types/task";

export const ProximasTasksItem = ({ task }: { task: Task }) => {
  // Aceita due_date como Date | string | null
  let dataTask: Date | null = null;
  if (task.due_date) {
    dataTask = task.due_date instanceof Date ? task.due_date : new Date(task.due_date);
    if (isNaN(dataTask.getTime())) dataTask = null;
  }
  let estaAtrasada = false;
  if (dataTask) {
    const hoje = new Date();
    const dataHoje = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());
    const dataTaskSoData = new Date(dataTask.getFullYear(), dataTask.getMonth(), dataTask.getDate());
    estaAtrasada = dataTaskSoData < dataHoje;
  }
  return (
    <Chakra.Box
      display="flex"
      justifyContent="space-between"
      alignItems="center"
      bg={estaAtrasada ? "red.300" : "transparent"}
      p={2}
      borderRadius="md"
    >
      <Chakra.Text fontSize="12px">{task.responsible?.name || task.title}</Chakra.Text>
      <Chakra.Text fontSize="12px" color="gray.600">
        {dataTask
          ? dataTask.toLocaleDateString("pt-BR", {
              day: "2-digit",
              month: "2-digit",
              timeZone: "UTC",
            })
          : "Sem data"}
      </Chakra.Text>
    </Chakra.Box>
  );
};
