export type tasksUser = {
  uuid: string;
  title: string;
  description?: string;
  due_date: string;
  status: "not-started" | "in-progress" | "completed" | "excluida";
  prioridade: "baixa" | "média" | "alta";
  equipe_uuid: string;
}

export type equipeUser = {
  uuid: string;
  name: string;
}

export type UserData = {
  uuid: string;
  name: string;
  email: string;
  img: string | null;
  tasks: tasksUser[];
  equipes: equipeUser[];  
};

export type AuthContextType = {
  token: string | null;
  setToken: (token: string | null) => void;
  user: UserData | null;
  setUser: (user: UserData | null) => void;
  logout: () => void;
  refreshUser: () => Promise<void>;
};