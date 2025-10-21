import type { TaskUser } from "./task";
import type { TeamRef } from "./team";

export interface User {
  uuid: string;
  name: string;
  email: string;
  img?: string;
  teams: TeamRef[];
  tasks: TaskUser[];
};

export interface UserRef {
  uuid: string;
  name: string;
  img?: string;
};
