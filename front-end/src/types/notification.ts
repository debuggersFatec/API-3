export type TypeNotification =
  | "TASK_CREATED"
  | "TASK_UPDATED"
  | "TASK_DELETED"
  | "TASK_DUE_SOON"
  | "TASK_ASSIGNED"
  | "TASK_UNASSIGNED"
  | "TASK_COMMENT"
  | "TEAM_MEMBER_JOINED"
  | "TEAM_MEMBER_LEFT"
  | "PROJECT_MEMBER_JOINED"
  | "PROJECT_MEMBER_LEFT";

export interface Notification {
  uuid: string;
  userUuid?: string;
  type: TypeNotification;
  projectUuid?: string;
  teamUuid?: string;
  taskUuid?: string;
  taskTitle?: string;
  actorUuid?: string;
  message: string;
  createdAt: string;
  read: boolean;
}
