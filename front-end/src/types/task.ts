export interface Responsavel {
  name: string;
  img: string;
}

export type TaskStatus = "not-started" | "in-progress" | "completed";
export type TaskPrioridade = "baixa" | "media" | "alta" | "critica";

export interface Task {
  uuid: string;
  title: string;
  description?: string;
  due_date: string;
  status: TaskStatus;
  prioridade: TaskPrioridade;
  responsavel?: Responsavel;
}

export interface Column {
  id: string;
  title: string;
  taskIds: string[];
}

export interface BoardData {
  tasks: Record<string, Task>;
  columns: Record<string, Column>;
  columnOrder: string[];
}
