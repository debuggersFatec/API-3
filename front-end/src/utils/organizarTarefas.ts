import type { Task, BoardData } from "@/types/task";

export function organizarTarefas(listaDeTarefas: Task[]): BoardData {
  const quadro: BoardData = {
    tasks: {},
    columns: {
      "nao-atribuidas": {
        id: "nao-atribuidas",
        title: "Não atribuídas",
        taskIds: [],
      },
      atribuido: { id: "atribuido", title: "Atribuído", taskIds: [] },
      concluido: { id: "concluido", title: "Concluído", taskIds: [] },
    },
    columnOrder: ["nao-atribuidas", "atribuido", "concluido"],
  };

  for (const tarefa of listaDeTarefas) {
    quadro.tasks[tarefa.uuid] = tarefa;
    if (tarefa.status === "not-started") {
      quadro.columns["nao-atribuidas"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "in-progress") {
      quadro.columns["atribuido"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "completed") {
      quadro.columns["concluido"].taskIds.push(tarefa.uuid);
    }
  }
  return quadro;
}
