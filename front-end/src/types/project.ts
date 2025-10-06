import type { TaskProject } from "./task";
import type { UserRef } from "./user";

export interface Project {
  uuid: string;
  name: string;
  isActive: boolean;
  members: UserRef[];
  team_uuid: string;
  tasks?: TaskProject[];
  trashcan?: TaskProject[];
}

export interface ProjectRef {
  uuid: string;
  name: string;
  isActive: boolean;
}
