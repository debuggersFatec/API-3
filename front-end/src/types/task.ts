import type { UserRef } from "./user";

export type Status = "not-started" | "in-progress" | "completed" | "deleted";
export type Priority = "low" | "medium" | "high";



export interface TaskComment {
  uuid: string;
  comment: string;
  created_at: string;
  user: UserRef;
}

export interface Task {
  uuid: string;
  title: string;
  description?: string;
  due_date?: Date;
  status: Status;
  priority: Priority;
  file_url?: string;
  isRequiredFile: boolean;
  required_file?: string;
  team_uuid: string;
  project_uuid: string;
  responsible?: UserRef;
  comments?: TaskComment[];
}

export interface TaskUser {
  uuid: string;
  title: string;
  due_date?: string;
  status: Status;
  priority: Priority;
  team_uuid: string;
  project_uuid: string;
  responsible?: UserRef;
}

export interface TaskProject {
  uuid: string;
  title: string;
  due_date?: string;
  status: Status;
  priority: Priority;
  team_uuid: string;
  project_uuid: string;
  responsible?: UserRef;
}
