import { Box, Text } from "@chakra-ui/react";
import { TarefasAtribuidasItem } from "./TarefasAtribuidasItem";
import type { TaskTeam } from "@/types/task";

interface UserTaskCount {
  uuiddousuario: string;
  name: string;
  countDeTarefas: number;
}

type Accumulator = {
  [key: string]: UserTaskCount;
};

interface TarefasAtribuidasProps {
  tasks: TaskTeam[];
}

export const TarefasAtribuidas = ({ tasks }: TarefasAtribuidasProps) => {
  console.log("TarefasAtribuidas received tasks:", tasks);

  const contagemPorUsuario = tasks.reduce<Accumulator>((acc, task) => {
    if (!task.responsavel) return acc;
    const { name } = task.responsavel;
    const uuid = task.responsavel.name;

    if (!acc[uuid]) {
      acc[uuid] = {
        uuiddousuario: uuid,
        name: name,
        countDeTarefas: 0,
      };
    }
    acc[uuid].countDeTarefas++;
    return acc;
  }, {});

  const resultadoFinal: UserTaskCount[] = Object.values(
    contagemPorUsuario
  ).sort((a, b) => b.countDeTarefas - a.countDeTarefas);

  return (
    <Box
      w={"100%"}
      borderRadius={"8px"}
      border={"1px solid #E2E8F0"}
      p={"16px"}
    >
      <Text fontSize={"16px"} fontWeight={"bold"} mb={"16px"}>
        Tarefas Atribuídas
      </Text>

      <Box display="flex" flexDirection="column" gap={4}>
        {resultadoFinal.map((usuario) => (
          <TarefasAtribuidasItem
            key={usuario.uuiddousuario}
            usuario={usuario}
          />
        ))}
      </Box>
    </Box>
  );
};
