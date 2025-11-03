import { Dialog, Button, CloseButton, Field, Input } from "@chakra-ui/react";
import { useAuth } from "@/context/auth/useAuth";
import { useState } from "react";
import { projectServices } from "@/services/ProjectServices";
import { useTeam } from "@/context/team/useTeam";
import { toast } from "@/utils/toast";

interface ModalNewProjectProps {
  onClose?: () => void;
}

export const ModalNewProject = ({ onClose }: ModalNewProjectProps) => {
  const [projectName, setProjectName] = useState("");
  const { user, token, refreshUser } = useAuth();
  const { teamData } = useTeam();
  const [open, setOpen] = useState(false);

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!user || !token) {
      toast("error", "Usuário não autenticado. Faça login novamente.");
      return;
    }
    if (!teamData) {
      toast("error", "Dados da equipe não encontrados.");
      return;
    }
    try {
      await projectServices.createProject(projectName, teamData.uuid, token);
      toast("success", "Projeto criado com sucesso!");
      setOpen(false);
      if (onClose) onClose();
      setProjectName("");
      refreshUser();
    } catch (error) {
      toast("error", "Erro ao criar projeto.");
      console.error("Erro ao criar projeto:", error);
    }
  };

  return (
    <Dialog.Root
      placement="center"
      motionPreset="slide-in-bottom"
      open={open}
      onOpenChange={() => setOpen(!open)}
    >
      <Dialog.Trigger asChild>
        <Button variant="outline" size="sm">
          Criar
        </Button>
      </Dialog.Trigger>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content>
          <Dialog.Header>
            <Dialog.Title>Criar um novo projeto</Dialog.Title>
          </Dialog.Header>
          <form onSubmit={handleSubmit}>
            <Dialog.Body>
              <Field.Root mb={"16px"}>
                <Input
                  name="title"
                  value={projectName}
                  onChange={(e) => setProjectName(e.target.value)}
                  placeholder={"Nome do projeto"}
                  variant={"flushed"}
                  required
                />
              </Field.Root>
            </Dialog.Body>
            <Dialog.Footer>
              <Dialog.ActionTrigger asChild>
                <Button type="button" variant="outline" onClick={onClose}>
                  Cancel
                </Button>
              </Dialog.ActionTrigger>
              <Button type="submit">Criar</Button>
            </Dialog.Footer>
          </form>
          <Dialog.CloseTrigger asChild>
            <CloseButton size="sm" />
          </Dialog.CloseTrigger>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
};
