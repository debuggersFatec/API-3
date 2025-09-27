export interface Responsavel {
  name: string;
  img: string;
  uuid: string;
}

export type TaskStatus = "not-started" | "in-progress" | "completed";
export type TaskPriority = "baixa" | "media" | "alta" | "critica";

export interface Task {
  uuid: string;
  title: string;
  description?: string;
  due_date: Date | null;
  status: TaskStatus;
  priority: TaskPriority;
  equip_uuid: string;
  responsible?: Responsavel;
  responsavel?: Responsavel;
}

export interface TaskTeam {
  uuid: string;
  title: string;
  due_date: Date | null;
  status: TaskStatus;
  priority: TaskPriority;
  equip_uuid: string;
  responsible?: Responsavel;
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
