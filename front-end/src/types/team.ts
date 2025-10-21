import type { ProjectRef } from "./project";
import type { UserRef } from "./user";

export interface TeamRef {
  uuid: string;
  name: string;
  projects?: ProjectRef[];
}

export interface Team {
  uuid: string;
  name: string;
  members: UserRef[];
  projects?: ProjectRef[];
}
