import { Flex, Tabs, Separator } from "@chakra-ui/react";
import { MyTasks } from "./MyTasks";

const usuario = {
  uuid: "usuario-123e4567-e89b-12d3-a456-426614174000",
  name: "Juan perez",
  email: "Juanperez@gmail.com",
  password: "securepassword123",
  img: "https://example.com/avatar.jpg",
  equipes: [
    {
      uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
      name: "Debuggers",
    },
    {
      uuid: "equipe-987e6543-e21b-12d3-a456-426614174999",
      name: "Faculdade",
    },
    {
      uuid: "equipe-456e7890-e12b-12d3-a456-426614174555",
      name: "Freelas",
    },
  ],
  tasks: [
    {
      uuid: "task-123e4567-e89b-12d3-a456-426614174000",
      title: "Complete project report",
      description:
        "Finish the final report for the project by end of the week.",
      due_date: "2024-07-05",
      status: "in-progress",
      prioridade: "alta",
      equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
    },
    {
      uuid: "task-987e6543-e21b-12d3-a456-426614174999",
      title: "Criar esquema de banco de dados",
      due_date: "2024-07-10",
      status: "not-started",
      prioridade: "média",
      equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
    },
  ],
  lixeira: [
    {
      uuid: "task-123e4567-e89b-12d3-a456-426614174000",
      title: "Criar protótipo de interface",
      description:
        "Desenvolver o protótipo inicial da interface do usuário para o novo aplicativo.",
      due_date: "2024-07-15",
      status: "not-started",
      prioridade: "alta",
      equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
      file_url: null,
      responsavel: {
        uuid: "usuario-123e4567-e89b-12d3-a456-426614174000",
        name: "Juan perez",
        img: "https://example.com/avatar.jpg",
      },
    },
  ],
  notificacoes: [
    {
      uuid: "notif-123e4567-e89b-12d3-a456-426614174000",
      message: "Nova tarefa atribuída: Criar esquema de banco de dados",
      date: "2024-06-25T10:00:00Z",
      read: false,
    },
    {
      uuid: "notif-987e6543-e21b-12d3-a456-426614174999",
      message: "Tarefa concluída: Complete project report",
      date: "2024-06-20T15:30:00Z",
      read: true,
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
          <Tabs.List w={"20vw"} flexDir={"column"} display={"flex"} pl={"24px"}>
            <h1>Tarefas</h1>
            <Tabs.Trigger value="minhasTasks">Minhas Tasks</Tabs.Trigger>
            <Tabs.Trigger value="vencidas">Vencidas</Tabs.Trigger>
            <h1>Equipes</h1>
            {usuario.equipes.map((equipe) => (
              <Tabs.Trigger key={equipe.uuid} value={equipe.uuid}>
                {equipe.name}
              </Tabs.Trigger>
            ))}
            <Separator />
            <Tabs.Trigger value="completas">Completas</Tabs.Trigger>
            <Tabs.Trigger value="lixo">Lixo</Tabs.Trigger>
          </Tabs.List>
          <Tabs.Content value="minhasTasks">
            <MyTasks />
          </Tabs.Content>
          <Tabs.Content value="vencidas">Manage your projects</Tabs.Content>
          <Tabs.Content value="tasks">
            Manage your tasks for freelancers
          </Tabs.Content>
        </Tabs.Root>
      </Flex>
    </>
  )
}
