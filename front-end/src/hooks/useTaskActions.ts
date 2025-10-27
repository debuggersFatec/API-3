import { useCallback } from "react";
import { useAuth } from "@/context/auth/useAuth";
import { useTeam } from "@/context/team/useTeam";
import { useProject } from "@/context/project/useProject";
import { taskService } from "@/services";
import { toast } from "@/utils/toast";

export const useTaskActions = () => {
  const { token, refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { refreshProject } = useProject();

  const deleteTask = useCallback(
    async (taskUuid: string, opts?: { onSuccess?: () => void }) => {
      if (!taskUuid) return;
      try {
        await taskService.deleteTask(taskUuid, token);
        toast("success", "Tarefa excluída com sucesso!");
        await refreshUser();
        await refreshProject();
        await refreshTeam();
        if (opts?.onSuccess) opts.onSuccess();
      } catch (err) {
        console.error("Erro ao excluir tarefa:", err);
        toast("error", "Erro ao excluir tarefa.");
        throw err;
      }
    },
    [token, refreshUser, refreshProject, refreshTeam]
  );

  return { deleteTask };
};

export default useTaskActions;
