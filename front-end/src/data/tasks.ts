export type Task = {
  uuid: string;
  title: string;
  due_date: Date | null;
  status: "not-started" | "in-progress" | "completed";
  priority: "baixa" | "media" | "alta" | "critica";
  equip_uuid: string;
  responsavel?: {
    name: string;
    img: string;
    uuid: string;
  };
};

export const tasks: Task[] = [
  {
    uuid: "task-123e4567-e89b-12d3-a456-426614174000",
    title: "Criar protótipo de interface",
    due_date: new Date("2025-09-15"),
    status: "in-progress",
    priority: "alta",
    equip_uuid: "equipe-001",
    responsavel: {
      uuid: "user-001",
      name: "Matheus Karnas",
      img: "https://i.pravatar.cc/150?u=Matheus_Karnas",
    },
  },
  {
    uuid: "task-atrasada-xyz",
    title: "Revisar documentação da API",
    due_date: new Date("2025-09-10"),
    status: "not-started",
    priority: "baixa",
    equip_uuid: "equipe-001",
  },
  {
    uuid: "task-abcdef12-3456-7890-abcd-ef1234567890",
    title: "Desenvolver endpoint de autenticação",
    due_date: new Date("2025-09-25"),
    status: "not-started",
    priority: "alta",
    equip_uuid: "equipe-001",
  },
  {
    uuid: "task-9c8b7a6d-5e4f-3c2b-1a98-76543210fedc",
    title: "Planejar a próxima Sprint",
    due_date: new Date("2025-09-18"),
    status: "completed",
    priority: "media",
    equip_uuid: "equipe-002",
    responsavel: {
      uuid: "user-002",
      name: "Ana Oliveira",
      img: "https://i.pravatar.cc/150?u=Ana_Oliveira",
    },
  },
  {
    uuid: "task-fedcba09-8765-4321-fedc-ba9876543210",
    title: "Corrigir bug na página de login",
    due_date: new Date("2025-09-05"),
    status: "in-progress",
    priority: "alta",
    equip_uuid: "equipe-002",
    responsavel: {
      uuid: "user-003",
      name: "João Pereira",
      img: "https://i.pravatar.cc/150?u=Joao_Pereira",
    },
  },
  {
    uuid: "task-11223344-5566-7788-99aa-bbccddeeff00",
    title: "Atualizar dependências do projeto",
    due_date: new Date("2025-10-01"),
    status: "not-started",
    priority: "baixa",
    equip_uuid: "equipe-003",
  },
  {
    uuid: "task-aabbccdd-eeff-0011-2233-445566778899",
    title: "Testar fluxo de pagamento",
    due_date: new Date("2025-09-28"),
    status: "not-started",
    priority: "alta",
    equip_uuid: "equipe-003",
  },
  {
    uuid: "task-ffeeddcc-bbaa-9988-7766-554433221100",
    title: "Refatorar componente de Tabela",
    due_date: new Date("2025-10-10"),
    status: "in-progress",
    priority: "media",
    equip_uuid: "equipe-001",
    responsavel: {
      uuid: "user-002",
      name: "Ana Oliveira",
      img: "https://i.pravatar.cc/150?u=Ana_Oliveira",
    },
  },
  {
    uuid: "task-cafebabe-dead-beef-face-d00d12345678",
    title: "Configurar pipeline de CI/CD",
    due_date: new Date("2025-09-20"),
    status: "in-progress",
    priority: "alta",
    equip_uuid: "equipe-002",
    responsavel: {
      uuid: "user-004",
      name: "Beatriz Costa",
      img: "https://i.pravatar.cc/150?u=Beatriz_Costa",
    },
  },
  {
    uuid: "task-d00d1234-face-beef-dead-cafebabeface",
    title: "Implementar cache com Redis",
    due_date: new Date("2025-10-05"),
    status: "not-started",
    priority: "media",
    equip_uuid: "equipe-003",
  },
  {
    uuid: "task-b0b0b0b0-b0b0-b0b0-b0b0-b0b0b0b0b0b0",
    title: "Escrever testes unitários para serviço de email",
    due_date: new Date("2025-08-30"),
    status: "in-progress",
    priority: "media",
    equip_uuid: "equipe-001",
    responsavel: {
      uuid: "user-005",
      name: "Maria Silva",
      img: "https://i.pravatar.cc/150?u=Maria_Silva",
    },
  },
  {
    uuid: "task-c1c1c1c1-c1c1-c1c1-c1c1-c1c1c1c1c1c1",
    title: "Realizar deploy em ambiente de homologação",
    due_date: new Date("2025-09-19"),
    status: "completed",
    priority: "alta",
    equip_uuid: "equipe-002",
    responsavel: {
      uuid: "user-003",
      name: "João Pereira",
      img: "https://i.pravatar.cc/150?u=Joao_Pereira",
    },
  },
  {
    uuid: "task-d2d2d2d2-d2d2-d2d2-d2d2-d2d2d2d2d2d2",
    title: "Otimizar performance de query de produtos",
    due_date: new Date("2025-11-01"),
    status: "not-started",
    priority: "media",
    equip_uuid: "equipe-003",
  },
  {
    uuid: "task-e3e3e3e3-e3e3-e3e3-e3e3-e3e3e3e3e3e3",
    title: "Reunião de alinhamento com stakeholders",
    due_date: new Date("2025-09-23"),
    status: "completed",
    priority: "baixa",
    equip_uuid: "equipe-001",
    responsavel: {
      uuid: "user-002",
      name: "Ana Oliveira",
      img: "https://i.pravatar.cc/150?u=Ana_Oliveira",
    },
  },
  {
    uuid: "task-f4f4f4f4-f4f4-f4f4-f4f4-f4f4f4f4f4f4",
    title: "Criar documentação para novo microserviço",
    due_date: new Date("2025-10-20"),
    status: "not-started",
    priority: "media",
    equip_uuid: "equipe-002",
  },
  {
    uuid: "task-a5a5a5a5-a5a5-a5a5-a5a5-a5a5a5a5a5a5",
    title: "Analisar logs de erro da última semana",
    due_date: new Date("2025-09-11"),
    status: "in-progress",
    priority: "alta",
    equip_uuid: "equipe-003",
    responsavel: {
      uuid: "user-006",
      name: "Lucas Martins",
      img: "https://i.pravatar.cc/150?u=Lucas_Martins",
    },
  },
  {
    uuid: "task-b6b6b6b6-b6b6-b6b6-b6b6-b6b6b6b6b6b6",
    title: "Desenvolver tela de perfil do usuário",
    due_date: new Date("2025-10-08"),
    status: "not-started",
    priority: "alta",
    equip_uuid: "equipe-003",
  },
  {
    uuid: "task-c7c7c7c7-c7c7-c7c7-c7c7-c7c7c7c7c7c7",
    title: "Ajustar layout responsivo do dashboard",
    due_date: new Date("2025-09-29"),
    status: "in-progress",
    priority: "media",
    equip_uuid: "equipe-003",
    responsavel: {
      uuid: "user-005",
      name: "Maria Silva",
      img: "https://i.pravatar.cc/150?u=Maria_Silva",
    },
  },
  {
    uuid: "task-d8d8d8d8-d8d8-d8d8-d8d8-d8d8d8d8d8d8",
    title: "Corrigir vulnerabilidade de segurança XSS",
    due_date: new Date("2025-08-20"),
    status: "not-started",
    priority: "critica",
    equip_uuid: "equipe-002",
  },
  {
    uuid: "task-e9e9e9e9-e9e9-e9e9-e9e9-e9e9e9e9e9e9",
    title: "Pesquisar nova biblioteca de gráficos",
    due_date: new Date("2025-11-15"),
    status: "not-started",
    priority: "baixa",
    equip_uuid: "equipe-001",
  },
];
