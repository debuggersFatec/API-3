import type { UserRef } from "./user";

export type Status = "NOT_STARTED" | "IN_PROGRESS" | "COMPLETED" | "DELETED";
export type Priority = "LOW" | "MEDIUM" | "HIGH";



export interface TaskComment {
  uuid: string;
  comment: string;
  createdAt: string;
  author: UserRef;
}

export interface RequiredFile {
  originalName: string;
  storedName: string;
  uploaderUUID: string;
  uploadeDate: string;
}

export interface Task {
  uuid: string;
  title: string;
  description?: string;
  due_date?: Date;
  status: Status;
  priority: Priority;
  file_url?: string;
  is_required_file: boolean;
  requiredFile?: RequiredFile[];
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
  is_required_file: boolean;
}
