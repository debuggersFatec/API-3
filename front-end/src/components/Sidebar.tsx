import { Flex, Tabs, Separator } from "@chakra-ui/react";
import { MyTasks } from "./MyTasks";
import { EquipeDashboard } from "./EquipeDashboard";
import { VencidasTab } from "./VencidasTab";
import { CompletasTab } from "./CompletasTab";
import { LixoTab } from "./LixoTab";

const usuario = {
  uuid: "usuario-matheus-k-9999-8888-777777777777",
  name: "Matheus Karnas",
  email: "matheus.karnas@email.com",
  password: "hashed_password_example",
  img: "https://i.pravatar.cc/150?u=matheus_karnas",
  equipes: [
    {
      uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
      name: "Debuggers",
    },
    {
      uuid: "equipe-facul-a1b2-c3d4-e5f6a7b8c9d0",
      name: "Faculdade",
    },
    {
      uuid: "equipe-work-b2c3-d4e5-f6a7b8c9d0e1",
      name: "Trabalho",
    },
    {
      uuid: "equipe-team2-c3d4-e5f6-a7b8c9d0e1f2",
      name: "Equipe 2",
    },
  ],
  tasks: [
    {
      uuid: "task-123e4567-e89b-12d3-a456-426614174000",
      title: "Criar protótipo de interface",
      due_date: "2025-09-15",
      status: "in-progress",
      prioridade: "alta",
      equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
    },
    {
      uuid: "task-facul-002",
      title: "Estudar para prova de Redes",
      due_date: "2025-09-25",
      status: "not-started",
      prioridade: "alta",
      equipe_uuid: "equipe-facul-a1b2-c3d4-e5f6a7b8c9d0",
    },
    {
      uuid: "task-work-003",
      title: "Reunião com Cliente X",
      due_date: "2025-09-15",
      status: "not-started",
      prioridade: "média",
      equipe_uuid: "equipe-work-b2c3-d4e5-f6a7b8c9d0e1",
    },
    {
      uuid: "task-team2-002",
      title: "Atualizar documentação do projeto",
      due_date: "2025-09-28",
      status: "in-progress",
      prioridade: "baixa",
      equipe_uuid: "equipe-team2-c3d4-e5f6-a7b8c9d0e1f2",
    },
  ],
  lixeira: [
    {
      uuid: "task-deleted-example-001",
      title: "Configurar ambiente de dev antigo",
      description:
        "Instalar versão legada do Java e configurar variáveis de ambiente.",
      due_date: "2025-08-30",
      status: "completed",
      prioridade: "baixa",
      equipe_uuid: "equipe-work-b2c3-d4e5-f6a7b8c9d0e1",
      responsavel: {
        uuid: "usuario-matheus-k-9999-8888-777777777777",
        name: "Matheus Karnas",
        img: "https://i.pravatar.cc/150?u=matheus_karnas",
      },
    },
  ],
};

export const Sidebar = () => {
  return (
    <>
      <Flex>
        <Tabs.Root
          defaultValue="minhasTasks"
          variant={"plain"}
          flexDir={"row"}
          display={"flex"}
          w={"100vw"}
        >
          <Tabs.List
            pr={"16px"}
            w={"20vw"}
            flexDir={"column"}
            display={"flex"}
            pl={"24px"}
          >
            <h1>Tarefas</h1>
            <Tabs.Trigger value="minhasTasks">Minhas Tasks</Tabs.Trigger>
            <Tabs.Trigger value="vencidas">Vencidas</Tabs.Trigger>
            <h1>Equipes</h1>
            {usuario.equipes.map((equipe) => (
              <Tabs.Trigger key={equipe.uuid} value={equipe.uuid}>
                {equipe.name}
              </Tabs.Trigger>
            ))}
            <Separator maxW={"200px"} />
            <Tabs.Trigger value="completas">Completas</Tabs.Trigger>
            <Tabs.Trigger value="lixo">Lixo</Tabs.Trigger>
          </Tabs.List>
          <Tabs.Content value="minhasTasks">
            <MyTasks />
          </Tabs.Content>
          <Tabs.Content value="vencidas">
            <VencidasTab />
          </Tabs.Content>
          <Tabs.Content value="completas">
            <CompletasTab />
          </Tabs.Content>
          <Tabs.Content value="lixo">
            <LixoTab />
          </Tabs.Content>
          {usuario.equipes.map((equipe) => (
            <Tabs.Content key={equipe.uuid} value={equipe.uuid}>
              <EquipeDashboard equipe={equipe} />
            </Tabs.Content>
          ))}
        </Tabs.Root>
      </Flex>
    </>
  );
};
