import { useState, useMemo, useCallback } from "react";
import type { TaskProject, TaskUser } from "@/types/task";
import type { Status, Priority } from "@/types/task";

export interface TaskFilters {
  search: string;
  status: Status | "all";
  priority: Priority | "all";
  dueDateFrom?: string;
  dueDateTo?: string;
  responsibleUuid?: string | "unassigned";
}

type TaskType = TaskProject | TaskUser;

export const useTaskFilters = (tasks: TaskType[] | undefined) => {
  const [filters, setFilters] = useState<TaskFilters>({
    search: "",
    status: "all",
    priority: "all",
    dueDateFrom: undefined,
    dueDateTo: undefined,
    responsibleUuid: undefined,
  });

  const filteredTasks = useMemo(() => {
    if (!tasks || tasks.length === 0) return [];

    return tasks.filter((task) => {
      // Filtro por busca (título)
      if (
        filters.search &&
        !task.title.toLowerCase().includes(filters.search.toLowerCase())
      ) {
        return false;
      }

      // Status now comes from backend as UPPERCASE enum strings. Accept those
      // directly and avoid legacy normalizations.
      let taskStatus: Status | undefined;
      const rawStatus = (task as unknown as Record<string, unknown>)["status"];
      if (typeof rawStatus === "string") {
        const upper = rawStatus.toUpperCase();
        if (upper === "NOT_STARTED" || upper === "IN_PROGRESS" || upper === "COMPLETED" || upper === "DELETED") {
          taskStatus = upper as Status;
        }
      }

      // Filtro por status
      if (filters.status !== "all") {
        if (!taskStatus || taskStatus !== filters.status) return false;
      }

      // Filtro por prioridade
      if (filters.priority !== "all") {
        // Priority expected as UPPERCASE enum strings from backend.
        let taskPriority: Priority | undefined;
        const rawPriority =
          (task as unknown as Record<string, unknown>)["priority"] ??
          (task as unknown as Record<string, unknown>)["prioridade"];
        if (typeof rawPriority === "string") {
          const upper = rawPriority.toUpperCase();
          if (upper === "LOW" || upper === "MEDIUM" || upper === "HIGH") {
            taskPriority = upper as Priority;
          }
        }

        if (!taskPriority || taskPriority !== filters.priority) return false;
      }

      // Filtro por data de vencimento
      if (filters.dueDateFrom || filters.dueDateTo) {
        if (!task.due_date) return false;

        const taskDate = new Date(task.due_date);
        if (filters.dueDateFrom) {
          const fromDate = new Date(filters.dueDateFrom);
          if (taskDate < fromDate) return false;
        }
        if (filters.dueDateTo) {
          const toDate = new Date(filters.dueDateTo);
          if (taskDate > toDate) return false;
        }
      }

      // Filtro por responsável (usando uuid ou "unassigned")
      if (filters.responsibleUuid) {
        if (filters.responsibleUuid === "unassigned") {
          if ("responsible" in task && task.responsible) {
            return false;
          }
        } else {
          if (
            !("responsible" in task) ||
            !task.responsible ||
            task.responsible.uuid !== filters.responsibleUuid
          ) {
            return false;
          }
        }
      }

      return true;
    });
  }, [tasks, filters]);

  const updateSearch = useCallback((search: string) => {
    setFilters((prev) => ({ ...prev, search }));
  }, []);

  const updateStatus = useCallback((status: Status | "all") => {
    setFilters((prev) => ({ ...prev, status }));
  }, []);

  const updatePriority = useCallback((priority: Priority | "all") => {
    setFilters((prev) => ({ ...prev, priority }));
  }, []);

  const updateDueDateRange = useCallback((from?: string, to?: string) => {
    setFilters((prev) => ({
      ...prev,
      dueDateFrom: from,
      dueDateTo: to,
    }));
  }, []);

  const updateResponsible = useCallback((responsibleUuid?: string) => {
    setFilters((prev) => ({ ...prev, responsibleUuid }));
  }, []);

  const clearAllFilters = useCallback(() => {
    setFilters({
      search: "",
      status: "all",
      priority: "all",
      dueDateFrom: undefined,
      dueDateTo: undefined,
      responsibleUuid: undefined,
    });
  }, []);

  const hasActiveFilters = useMemo(() => {
    return (
      filters.search !== "" ||
      filters.status !== "all" ||
      filters.priority !== "all" ||
      filters.dueDateFrom !== undefined ||
      filters.dueDateTo !== undefined ||
      filters.responsibleUuid !== undefined
    );
  }, [filters]);

  return {
    filters,
    filteredTasks,
    updateSearch,
    updateStatus,
    updatePriority,
    updateDueDateRange,
    updateResponsible,
    clearAllFilters,
    hasActiveFilters,
    totalTasks: tasks?.length || 0,
    filteredCount: filteredTasks.length,
  };
};
