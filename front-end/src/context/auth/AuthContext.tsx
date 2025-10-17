import React, { useState } from "react";

import { AuthContext } from "./AuthContextInstance";
import axios from "axios";
import type { User } from "@/types/user";
import type { TaskUser, Priority, Status } from "@/types/task";
import { normalizeUser } from "./authUtils";

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [user, setUserState] = useState<User | null>(() => {
    const userJson = localStorage.getItem("user");
    if (!userJson) return null;
    try {
      return normalizeUser(JSON.parse(userJson));
    } catch (e) {
      console.warn("Falha ao ler user do localStorage:", e);
      return null;
    }
  });
  const [token, setTokenState] = useState<string | null>(() => {
    return localStorage.getItem("token");
  });

  const setToken = (newToken: string | null) => {
    setTokenState(newToken);
    if (newToken) {
      localStorage.setItem("token", newToken);
    } else {
      localStorage.removeItem("token");
    }
  };

  const setUser = (newUser: User | null) => {
    setUserState(newUser);
    if (newUser) {
      try {
        localStorage.setItem("user", JSON.stringify(newUser));
      } catch (e) {
        console.warn("Falha ao salvar user no localStorage:", e);
      }
    } else {
      try {
        localStorage.removeItem("user");
      } catch (e) {
        console.warn("Falha ao remover user do localStorage:", e);
      }
    }
  };

  const refreshUser = async () => {
    if (!token) return;
    try {
      const response = await axios.get("http://localhost:8080/api/users/me", {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      type Rec = Record<string, unknown>;
      const isRec = (v: unknown): v is Rec => typeof v === "object" && v !== null;
      const str = (v: unknown): string | undefined => (typeof v === "string" ? v : undefined);
      const arr = (v: unknown): unknown[] => (Array.isArray(v) ? v : []);

      // Modelos básicos da API (conforme OpenAPI)
      type ApiTeamInfo = { uuid: string; name: string };
      type ApiTaskInfo = {
        uuid: string;
        title: string;
        due_date?: string;
        status?: string;
        priority?: string;
        team_uuid?: string;
        equip_uuid?: string;
        teamUuid?: string;
        project_uuid?: string;
        projectUuid?: string;
        responsible?: { uuid: string; name: string; img?: string };
      };
      type ApiUserInfo = {
        uuid: string;
        name: string;
        email: string;
        img?: string;
        teams?: ApiTeamInfo[];
        tasks?: ApiTaskInfo[];
      };

      const data = response.data as unknown;
      let apiUser: ApiUserInfo | null = null;
      if (isRec(data) && isRec((data as Rec).user)) {
        const d = (data as Rec).user as Rec;
        apiUser = {
          uuid: str(d.uuid) ?? "",
          name: str(d.name) ?? "",
          email: str(d.email) ?? "",
          img: str(d.img),
          teams: arr(d.teams) as ApiTeamInfo[],
          tasks: arr(d.tasks) as ApiTaskInfo[],
        };
      } else if (isRec(data)) {
        apiUser = {
          uuid: str((data as Rec).uuid) ?? "",
          name: str((data as Rec).name) ?? "",
          email: str((data as Rec).email) ?? "",
          img: str((data as Rec).img),
          teams: arr((data as Rec).teams) as ApiTeamInfo[],
          tasks: arr((data as Rec).tasks) as ApiTaskInfo[],
        };
      }

      if (!apiUser) {
        console.warn("refreshUser: resposta inesperada de /users/me", data);
        return;
      }

      const toStatus = (s?: string): Status => {
        if (!s) return "not-started";
        const v = s.toUpperCase();
        if (v === "NOT_STARTED") return "not-started";
        if (v === "IN_PROGRESS") return "in-progress";
        if (v === "COMPLETED") return "completed";
        if (v === "DELETED") return "deleted";
        return "not-started";
      };
      const toPriority = (p?: string): Priority => {
        if (!p) return "low";
        const v = p.toUpperCase();
        if (v === "LOW") return "low";
        if (v === "MEDIUM") return "medium";
        if (v === "HIGH") return "high";
        return "low";
      };

      const tasks: TaskUser[] = (apiUser.tasks ?? []).map((t) => ({
        uuid: t.uuid,
        title: t.title,
        due_date: t.due_date,
        status: toStatus(t.status),
        priority: toPriority(t.priority),
        team_uuid: t.team_uuid ?? t.equip_uuid ?? t.teamUuid ?? "",
        project_uuid: t.project_uuid ?? t.projectUuid ?? "",
        responsible: t.responsible ? { uuid: t.responsible.uuid, name: t.responsible.name, img: t.responsible.img } : undefined,
      }));

      const userNormalized: User = {
        uuid: apiUser.uuid,
        name: apiUser.name,
        email: apiUser.email,
        img: apiUser.img,
        teams: (apiUser.teams ?? []).map((tm) => ({ uuid: tm.uuid, name: tm.name })),
        tasks,
      };

      setUser(userNormalized);
    } catch (err) {
      console.error("Erro ao atualizar dados do usuário:", err);
    }
  };

  const logout = () => {
    setUser(null);
    setToken(null);
  };

  return (
    <AuthContext.Provider
      value={{ user, setUser, logout, token, setToken, refreshUser }}
    >
      {children}
    </AuthContext.Provider>
  );
};
