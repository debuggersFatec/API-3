import type { TaskProject} from "@/types/task";
import type { BoardData } from "@/components/Board/QuadroDisplay";

export function organizarTarefas(listaDeTarefas: TaskProject[]): BoardData {
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
    if (tarefa.status === "NOT_STARTED") {
      quadro.columns["nao-atribuidas"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "IN_PROGRESS") {
      quadro.columns["atribuido"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "COMPLETED") {
      quadro.columns["concluido"].taskIds.push(tarefa.uuid);
    }
  }
  return quadro;
}
