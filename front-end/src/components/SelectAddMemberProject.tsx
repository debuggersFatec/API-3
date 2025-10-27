import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { Button, Flex } from "@chakra-ui/react";
import { toast } from "@/utils/toast";
import { projectServices } from "@/services/ProjectServices";
import { useState } from "react";

export const SelectAddMemberProject = () => {
  const { teamData } = useTeam();
  const { project, refreshProject } = useProject();
  const { refreshTeam } = useTeam();
  const [newMemberUuid, setNewMemberUuid] = useState<string | null>(null);

  // Garante que project.members é array antes de usar some()
  const projectMemberUuids = new Set(
    (project?.members ?? []).map((m) => m.uuid)
  );

  const membersForAdd = (teamData?.members ?? []).filter(
    (member) => !projectMemberUuids.has(member.uuid)
  );

  const handleSubmit = async () => {
    const selectedUuid = newMemberUuid;
    if (!selectedUuid || !project)
      return toast("error", "Selecione um membro para adicionar.");
    try {
      await projectServices.addMemberToProject(project.uuid, selectedUuid);
      toast("success", "Membro adicionado ao projeto com sucesso!");
      await refreshProject();
      await refreshTeam();
      setNewMemberUuid(null);
    } catch (error) {
      toast("error", "Erro ao adicionar membro ao projeto.");
      console.error("Erro ao adicionar membro ao projeto:", error);
    }
  };

  return (
    <Flex alignItems={"center"}>
      <select
        onChange={(e) => setNewMemberUuid(e.target.value)}
        disabled={membersForAdd.length === 0}
        aria-label="Adicionar membro ao projeto"
        style={{ padding: '8px 12px', borderRadius: 6, border: "1px solid #E2E8F0", height: 38 }}
        defaultValue=""
      >
        <option value="" disabled>
          {membersForAdd.length === 0
            ? "Nenhum membro disponível"
            : "Adicione um membro ao projeto"}
        </option>
        {membersForAdd.map((m) => (
          <option key={m.uuid} value={m.uuid}>
            {m.name}
          </option>
        ))}
      </select>
      <Button size={'sm'} onClick={handleSubmit} ml={3}>
        Adicionar
      </Button>
    </Flex>
  );
};
