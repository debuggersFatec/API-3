import type { TaskProject} from "@/types/task";
import type { BoardData } from "@/components/Board/QuadroDisplay";

export function organizarTarefas(listaDeTarefas: TaskProject[]): BoardData {
  const quadro: BoardData = {
    tasks: {},
    columns: {
      "nao-iniciada": {
        id: "nao-iniciada",
        title: "Não Iniciada",
        taskIds: [],
      },
      "em-progresso": { id: "em-progresso", title: "Em Progresso", taskIds: [] },
      "concluida": { id: "concluida", title: "Concluída", taskIds: [] },
    },
    columnOrder: ["nao-iniciada", "em-progresso", "concluida"],
  };

  for (const tarefa of listaDeTarefas) {
    quadro.tasks[tarefa.uuid] = tarefa;
    if (tarefa.status === "NOT_STARTED") {
      quadro.columns["nao-iniciada"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "IN_PROGRESS") {
      quadro.columns["em-progresso"].taskIds.push(tarefa.uuid);
    } else if (tarefa.status === "COMPLETED") {
      quadro.columns["concluida"].taskIds.push(tarefa.uuid);
    }
  }
  return quadro;
}
