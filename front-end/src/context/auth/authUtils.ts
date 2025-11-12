import type { User, UserRef } from "@/types/user";
import type { TeamRef } from "@/types/team";
import type { TaskUser } from "@/types/task";
import type { ProjectRef } from "@/types/project";
import type { TypeNotification } from "@/types/notification";

export type AuthContextType = {
  token: string | null;
  setToken: (token: string | null) => void;
  user: User | null;
  // Accept unknown so callers can pass API envelopes; provider will normalize
  setUser: (user: unknown) => void;
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
      notificationsRecent: [],
    };
  }

  // o payload da API às vezes vem com wrapper { user: { ... } } ou no nível raiz.
  const base = isObj((raw as UnknownRecord).user) ? (raw as UnknownRecord).user as UnknownRecord : (raw as UnknownRecord);

  const teamsRaw = getArr(base.teams ?? base.equipes);
  const teams: TeamRef[] = teamsRaw.map((t) => {
    const o = isObj(t) ? t : {};
    return {
      uuid: getStr(o.uuid) ?? "",
      name: getStr(o.name) ?? "",
      projects: (getArr(o.projects) as ProjectRef[]) || undefined,
    };
  });

  const tasksRaw = getArr(base.tasks);
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

  // Notifications: pode vir em base.notificationsRecent ou no nível raiz raw.notificationsRecent
  const notificationsRaw = getArr(base.notificationsRecent ?? (raw as UnknownRecord).notificationsRecent);
  const allowedTypes: TypeNotification[] = [
    "TASK_CREATED",
    "TASK_UPDATED",
    "TASK_DELETED",
    "TASK_DUE_SOON",
    "TASK_ASSIGNED",
    "TASK_UNASSIGNED",
    "TASK_COMMENT",
    "TEAM_MEMBER_JOINED",
    "TEAM_MEMBER_LEFT",
    "PROJECT_MEMBER_JOINED",
    "PROJECT_MEMBER_LEFT",
  ];

  const notifications = notificationsRaw.map((n) => {
    const o = isObj(n) ? n : {};
    const rawType = getStr(o.type) ?? "";
    const type: TypeNotification = allowedTypes.includes(rawType as TypeNotification) ? (rawType as TypeNotification) : "TASK_UPDATED";
    return {
      uuid: getStr(o.id) ?? getStr(o.uuid) ?? "",
      userUuid: getStr(o.userUuid) ?? undefined,
      type,
      projectUuid: getStr(o.projectUuid) ?? getStr(o.project_uuid) ?? undefined,
      teamUuid: getStr(o.teamUuid) ?? getStr(o.team_uuid) ?? undefined,
      taskUuid: getStr(o.taskUuid) ?? getStr(o.task_uuid) ?? undefined,
      taskTitle: getStr(o.taskTitle) ?? getStr(o.task_title) ?? undefined,
      actorUuid: getStr(o.actorUuid) ?? undefined,
      message: getStr(o.message) ?? "",
      createdAt: getStr(o.createdAt) ?? getStr(o.created_at) ?? "",
      read: !!o.read,
    };
  });

  return {
    uuid: getStr(base.uuid) ?? "",
    name: getStr(base.name) ?? "",
    email: getStr(base.email) ?? "",
    img: getStr(base.img),
    teams,
    tasks,
    notificationsRecent: notifications,
  };
};
