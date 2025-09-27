import type { TaskTeam } from "./task";

export type Members = {
  name: string;
  img: string;
  uuid: string;
};

export type EquipeData = {
  uuid: string;
  name: string;
  membros: Members[];
  tasks?: TaskTeam[];
};
