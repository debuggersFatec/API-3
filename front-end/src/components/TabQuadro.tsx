import { Grid } from "@chakra-ui/react";
import { QuadroDisplay } from "./QuadroDisplay";

const tasks = [
  {
    uuid: "task-123e4567-e89b-12d3-a456-426614174000",
    title: "Criar protótipo de interface",
    due_date: "2025-09-15",
    status: "in-progress",
    prioridade: "alta",
    equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
    responsavel: {
      uuid: "usuario-matheus-k-9999-8888-777777777777",
      name: "Matheus Karnas",
      img: "https://i.pravatar.cc/150?u=matheus_karnas",
    },
  },
  {
    uuid: "task-abcdef12-3456-7890-abcd-ef1234567890",
    title: "Desenvolver endpoint de autenticação",
    due_date: "2025-09-25",
    status: "not-started",
    prioridade: "alta",
    equipe_uuid: "equipe-123e4567-e89b-12d3-a456-426614174000",
    responsavel: {
      uuid: "usuario-987e6543-e21b-12d3-a456-426614174999",
      name: "Maria Silva",
      img: "https://example.com/avatar2.jpg",
    },
  },
];

export const TabQuadro = () => {
  return (
    <Grid  templateColumns="repeat(3, 1fr)" gap="6" w={"100%"} >
      <QuadroDisplay title={"Não atribuida"} tasks={tasks} />
      <QuadroDisplay title={"Atribuído"} tasks={tasks} />
      <QuadroDisplay title={"Concluído"} tasks={tasks} />
    </Grid>
  );
};
