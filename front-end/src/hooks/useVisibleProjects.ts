import { useEffect, useState } from "react";
import type { ProjectRef, Project } from "@/types/project";
import { axiosInstance } from "@/services";
import { useAuth } from "@/context/auth/useAuth";

type UseVisibleProjectsResult = {
  visibleProjects: ProjectRef[];
  loading: boolean;
  error?: string;
};

export const useVisibleProjects = (projects: ProjectRef[] = []): UseVisibleProjectsResult => {
  const { user, token } = useAuth();
  const [visibleProjects, setVisibleProjects] = useState<ProjectRef[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | undefined>();

  useEffect(() => {
    let mounted = true;
    if (!projects || projects.length === 0) {
      setVisibleProjects([]);
      return;
    }

    setLoading(true);
    setError(undefined);

    const CONCURRENCY = 5;
    const queue = [...projects];
    const results: (ProjectRef | null)[] = [];

    const worker = async () => {
      while (queue.length > 0) {
        const p = queue.shift();
        if (!p) break;
        try {
          const { data } = await axiosInstance.get<Project>(`/projects/${p.uuid}`, {
            headers: token ? { Authorization: `Bearer ${token}` } : undefined,
          });
          const proj = data;
          const isMember =
            (proj.members && proj.members.some((m) => m.uuid === user?.uuid)) ||
            (!!proj.team_uuid && !!user?.teams && user.teams.some((t) => t.uuid === proj.team_uuid));
          results.push(isMember ? p : null);
        } catch (err) {
          console.warn("Erro ao checar projeto:", p.uuid, err);
          results.push(null);
        }
      }
    };

    const workers = Array.from({ length: CONCURRENCY }).map(() => worker());

    Promise.all(workers)
      .then(() => {
        if (!mounted) return;
        setVisibleProjects(results.filter(Boolean) as ProjectRef[]);
      })
      .catch((err) => {
        console.error("Erro ao filtrar projetos:", err);
        if (mounted) setError("Erro ao filtrar projetos");
      })
      .finally(() => {
        if (mounted) setLoading(false);
      });

    return () => {
      mounted = false;
    };
  }, [projects, token, user]);

  return { visibleProjects, loading, error };
};
