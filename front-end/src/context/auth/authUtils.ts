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
    // Status/priority are expected as UPPERCASE enum strings from backend.
    const rawStatus = getStr(o.status);
    const statusValues = ["NOT_STARTED", "IN_PROGRESS", "COMPLETED", "DELETED"];
    const status: TaskUser["status"] =
      rawStatus && statusValues.includes(rawStatus) ? (rawStatus as TaskUser["status"]) : "NOT_STARTED";

    const rawPriority = getStr(o.priority);
    const priorityValues = ["LOW", "MEDIUM", "HIGH"];
    const priority: TaskUser["priority"] =
      rawPriority && priorityValues.includes(rawPriority) ? (rawPriority as TaskUser["priority"]) : "LOW";

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
