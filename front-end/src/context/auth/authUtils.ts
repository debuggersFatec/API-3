import type { User, UserRef } from "@/types/user";
import type { TeamRef } from "@/types/team";
import type { TaskUser } from "@/types/task";
import type { ProjectRef } from "@/types/project";

export type AuthContextType = {
  token: string | null;
  setToken: (token: string | null) => void;
  user: User | null;
  setUser: (user: User | null) => void;
  logout: () => void;
  refreshUser: () => Promise<void>;
};

// Helpers de tipo seguros
type UnknownRecord = Record<string, unknown>;
const isObj = (v: unknown): v is UnknownRecord =>
  typeof v === "object" && v !== null;
const getStr = (v: unknown): string | undefined =>
  typeof v === "string" ? v : undefined;
const getArr = (v: unknown): unknown[] => (Array.isArray(v) ? v : []);

export const normalizeUser = (raw: unknown): User => {
  if (!isObj(raw)) {
    return {
      uuid: "",
      name: "",
      email: "",
      img: undefined,
      teams: [],
      tasks: [],
    };
  }

  const teamsRaw = getArr(raw.teams ?? (raw as UnknownRecord).equipes);
  const teams: TeamRef[] = teamsRaw.map((t) => {
    const o = isObj(t) ? t : {};
    return {
      uuid: getStr(o.uuid) ?? "",
      name: getStr(o.name) ?? "",
      projects: (getArr(o.projects) as ProjectRef[]) || undefined,
    };
  });

  const tasksRaw = getArr(raw.tasks);
  const tasks: TaskUser[] = tasksRaw.map((t) => {
    const o = isObj(t) ? t : {};
    // status/priority normalizados de forma conservadora
    const rawStatus = getStr(o.status);
    const statusMap: Record<string, TaskUser["status"]> = {
      not_started: "not-started",
      "not-started": "not-started",
      in_progress: "in-progress",
      "in-progress": "in-progress",
      completed: "completed",
      deleted: "deleted",
      NOT_STARTED: "not-started",
      IN_PROGRESS: "in-progress",
      COMPLETED: "completed",
      DELETED: "deleted",
    } as const;
    const status =
      rawStatus && statusMap[rawStatus] ? statusMap[rawStatus] : "not-started";

    const rawPriority = getStr(o.priority);
    const prioMap: Record<string, TaskUser["priority"]> = {
      low: "low",
      medium: "medium",
      high: "high",
      LOW: "low",
      MEDIUM: "medium",
      HIGH: "high",
    } as const;
    const priority =
      rawPriority && prioMap[rawPriority] ? prioMap[rawPriority] : "low";

    const responsible = isObj(o.responsible)
      ? ({
          uuid: getStr(o.responsible.uuid) ?? "",
          name: getStr(o.responsible.name) ?? "",
          img: getStr(o.responsible.img),
        } as UserRef)
      : undefined;

    return {
      uuid: getStr(o.uuid) ?? "",
      title: getStr(o.title) ?? "",
      due_date: getStr(o.due_date ?? (o as UnknownRecord).dueDate),
      status,
      priority,
      team_uuid:
        getStr(o.team_uuid) ||
        getStr((o as UnknownRecord).equip_uuid) ||
        getStr((o as UnknownRecord).teamUuid) ||
        getStr((o as UnknownRecord).equipe_uuid) ||
        "",
      project_uuid:
        getStr(o.project_uuid) ??
        getStr((o as UnknownRecord).projectUuid) ??
        "",
      responsible,
    };
  });

  return {
    uuid: getStr(raw.uuid) ?? "",
    name: getStr(raw.name) ?? "",
    email: getStr(raw.email) ?? "",
    img: getStr(raw.img),
    teams,
    tasks,
  };
};
