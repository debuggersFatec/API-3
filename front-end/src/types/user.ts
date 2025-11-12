import type { TaskUser } from "./task";
import type { TeamRef } from "./team";
import type { Notification as AppNotification } from "./notification";

export interface User {
  uuid: string;
  name: string;
  email: string;
  img?: string;
  teams: TeamRef[];
  tasks: TaskUser[];
  notificationsRecent: AppNotification[];
}

export interface UserRef {
  uuid: string;
  name: string;
  img?: string;
}
