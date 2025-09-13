import { Box, Text } from "@chakra-ui/react";
import { TarefasAtribuidasItem } from "./TarefasAtribuidasItem";

// 1. GARANTIR QUE O RESPONSÁVEL TENHA UM UUID
const tasks = [
  { uuid: "task-1", responsavel: { uuid: "user-111", name: "Matheus Karnas" } },
  { uuid: "task-2", responsavel: { uuid: "user-222", name: "Carlos Souza" } },
  { uuid: "task-3", responsavel: { uuid: "user-333", name: "Maria Silva" } },
  { uuid: "task-4", responsavel: { uuid: "user-444", name: "Ana Oliveira" } },
  { uuid: "task-5", responsavel: { uuid: "user-555", name: "João Pereira" } },
  { uuid: "task-6", responsavel: { uuid: "user-111", name: "Matheus Karnas" } },
  { uuid: "task-7", responsavel: { uuid: "user-222", name: "Carlos Souza" } },
  { uuid: "task-8", responsavel: { uuid: "user-444", name: "Ana Oliveira" } },
  { uuid: "task-9", responsavel: { uuid: "user-222", name: "Carlos Souza" } },
];

interface UserTaskCount {
  uuiddousuario: string;
  name: string;
  countDeTarefas: number;
}

type Accumulator = {
  [key: string]: UserTaskCount;
};

export const TarefasAtribuidas = () => {
  const contagemPorUsuario = tasks.reduce<Accumulator>((acc, task) => {
    const { uuid, name } = task.responsavel;

 
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
          <TarefasAtribuidasItem key={usuario.uuiddousuario} usuario={usuario}/>
        ))}
      </Box>
    </Box>
  );
};
