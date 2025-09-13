import { Box, Text } from "@chakra-ui/react";
import { ProximasTasksItem } from "./ProximasTasksItem";

const tasks = [
  {
    uuid: "task-123e4567-e89b-12d3-a456-426614174000",
    title: "Criar protótipo de interface",
    due_date: "2025-09-15", // Futuro
    status: "in-progress",
    prioridade: "alta",
    responsavel: { name: "Matheus Karnas" },
  },
  {
    // --- TAREFA VENCIDA 1 ---
    uuid: "task-atrasada-xyz",
    title: "Revisar documentação da API",
    due_date: "2025-09-10", // Vencida
    status: "not-started",
    prioridade: "baixa",
    responsavel: { name: "Carlos Souza" },
  },
  {
    uuid: "task-abcdef12-3456-7890-abcd-ef1234567890",
    title: "Desenvolver endpoint de autenticação",
    due_date: "2025-09-25", // Futuro
    status: "not-started",
    prioridade: "alta",
    responsavel: { name: "Maria Silva" },
  },
  {
    uuid: "task-9c8b7a6d-5e4f-3c2b-1a98-76543210fedc",
    title: "Planejar a próxima Sprint",
    due_date: "2025-09-18", // Futuro
    status: "completed",
    prioridade: "media",
    responsavel: { name: "Ana Oliveira" },
  },
  {
    // --- TAREFA VENCIDA 2 ---
    uuid: "task-fedcba09-8765-4321-fedc-ba9876543210",
    title: "Corrigir bug na página de login",
    due_date: "2025-09-05", // Vencida (data alterada)
    status: "in-progress",
    prioridade: "alta",
    responsavel: { name: "João Pereira" },
  },
  {
    uuid: "task-11223344-5566-7788-99aa-bbccddeeff00",
    title: "Atualizar dependências do projeto",
    due_date: "2025-10-01", // Futuro
    status: "not-started",
    prioridade: "baixa",
    responsavel: { name: "Matheus Karnas" },
  },
  {
    uuid: "task-aabbccdd-eeff-0011-2233-445566778899",
    title: "Testar fluxo de pagamento",
    due_date: "2025-09-28", // Futuro
    status: "not-started",
    prioridade: "alta",
    responsavel: { name: "Carlos Souza" },
  },
  {
    uuid: "task-ffeeddcc-bbaa-9988-7766-554433221100",
    title: "Refatorar componente de Tabela",
    due_date: "2025-10-10", // Futuro
    status: "in-progress",
    prioridade: "media",
    responsavel: { name: "Ana Oliveira" },
  },
  {
    uuid: "task-cafebabe-dead-beef-face-d00d12345678",
    title: "Configurar pipeline de CI/CD",
    due_date: "2025-09-20", // Futuro
    status: "in-progress",
    prioridade: "alta",
    responsavel: { name: "Beatriz Costa" },
  },
  {
    uuid: "task-d00d1234-face-beef-dead-cafebabeface",
    title: "Implementar cache com Redis",
    due_date: "2025-10-05", // Futuro
    status: "not-started",
    prioridade: "media",
    responsavel: { name: "Lucas Martins" },
  },
  {
    // --- TAREFA VENCIDA 3 ---
    uuid: "task-b0b0b0b0-b0b0-b0b0-b0b0-b0b0b0b0b0b0",
    title: "Escrever testes unitários para serviço de email",
    due_date: "2025-08-30", // Vencida (data alterada)
    status: "in-progress",
    prioridade: "media",
    responsavel: { name: "Maria Silva" },
  },
  {
    uuid: "task-c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1",
    title: "Realizar deploy em ambiente de homologação",
    due_date: "2025-09-19", // Futuro
    status: "completed",
    prioridade: "alta",
    responsavel: { name: "João Pereira" },
  },
  {
    uuid: "task-d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2",
    title: "Otimizar performance de query de produtos",
    due_date: "2025-11-01", // Futuro
    status: "not-started",
    prioridade: "media",
    responsavel: { name: "Carlos Souza" },
  },
  {
    uuid: "task-e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3",
    title: "Reunião de alinhamento com stakeholders",
    due_date: "2025-09-23", // Futuro
    status: "completed",
    prioridade: "baixa",
    responsavel: { name: "Ana Oliveira" },
  },
  {
    uuid: "task-f4f4f4f4-f4f4-f4f4-f4f4-f4f4f4f4f4f4",
    title: "Criar documentação para novo microserviço",
    due_date: "2025-10-20", // Futuro
    status: "not-started",
    prioridade: "media",
    responsavel: { name: "Beatriz Costa" },
  },
  {
    // --- TAREFA VENCIDA 4 ---
    uuid: "task-a5a5a5a5-a5a5-a5a5-a5a5-a5a5a5a5a5a5",
    title: "Analisar logs de erro da última semana",
    due_date: "2025-09-11", // Vencida (data alterada)
    status: "in-progress",
    prioridade: "alta",
    responsavel: { name: "Lucas Martins" },
  },
  {
    uuid: "task-b6b6b6b6-b6b6-b6b6-b6b6-b6b6b6b6b6b6",
    title: "Desenvolver tela de perfil do usuário",
    due_date: "2025-10-08", // Futuro
    status: "not-started",
    prioridade: "alta",
    responsavel: { name: "Matheus Karnas" },
  },
  {
    uuid: "task-c7c7c7c7-c7c7-c7c7-c7c7-c7c7c7c7c7c7",
    title: "Ajustar layout responsivo do dashboard",
    due_date: "2025-09-29", // Futuro
    status: "in-progress",
    prioridade: "media",
    responsavel: { name: "Maria Silva" },
  },
  {
    // --- TAREFA VENCIDA 5 ---
    uuid: "task-d8d8d8d8-d8d8-d8d8-d8d8-d8d8d8d8d8d8",
    title: "Corrigir vulnerabilidade de segurança XSS",
    due_date: "2025-08-20", // Vencida (data alterada)
    status: "not-started",
    prioridade: "critica",
    responsavel: { name: "Carlos Souza" },
  },
  {
    uuid: "task-e9e9e9e9-e9e9-e9e9-e9e9-e9e9e9e9e9e9",
    title: "Pesquisar nova biblioteca de gráficos",
    due_date: "2025-11-15", // Futuro
    status: "not-started",
    prioridade: "baixa",
    responsavel: { name: "Ana Oliveira" },
  },
];

export const ProximasTasks = () => {
  // A LÓGICA DE FILTRO E ORDENAÇÃO ACONTECE AQUI
  const proximas10Tasks = [...tasks]
    // Ordena pela data, da mais antiga/próxima para a mais distante
    .sort((a, b) => +new Date(a.due_date) - +new Date(b.due_date))
    // Pega apenas os 10 primeiros itens do array ordenado
    .slice(0, 10);

  return (
    <Box
      w={"100%"}
      h={"100%"}
      border={"1px solid"}
      borderColor={"gray.200"}
      borderRadius={"8px"}
      p={"16px"}
    >
      <Text fontWeight="bold" mb={4}>
        Próximas Tasks
      </Text>

      <Box display="flex" flexDirection="column" gap={2}>
        {/* Usamos o novo array com no máximo 10 tarefas para o map */}
        {proximas10Tasks.map((task) => (
          <ProximasTasksItem key={task.uuid} task={task} />
        ))}
      </Box>
    </Box>
  );
};