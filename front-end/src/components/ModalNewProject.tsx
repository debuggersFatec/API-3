import { Dialog, Button, CloseButton, Field, Input } from "@chakra-ui/react";
import { AxiosError } from "axios";
import { useAuth } from "@/context/auth/useAuth";
import { useState } from "react";
import { projectServices } from "@/services/ProjectServices";
import { useTeam } from "@/context/team/useTeam";

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
      alert("Usuário não autenticado. Faça login novamente.");
      return;
    }
    if (!teamData) {
      alert("Dados da equipe não encontrados.");
      return;
    }
    try {
      await projectServices.createProject(projectName, teamData.uuid, token);
      setOpen(false);
      if (onClose) onClose();
      setProjectName("");
      refreshUser();
    } catch (error) {
      const err = error as AxiosError;
      console.error("Erro ao criar equipe:", err);
      if (err.response?.status === 401) {
        alert("Token inválido ou expirado. Faça login novamente.");
      } else if (err.response?.status === 403) {
        alert("Acesso negado. Você não tem permissão.");
      } else if (err.response?.status === 404) {
        alert("Endpoint não encontrado no backend.");
      } else if (err.message?.includes("Network Error")) {
        alert(
          "Não foi possível conectar ao backend. Verifique se o servidor está rodando e se o CORS está liberado."
        );
      } else {
        alert( error);
      }
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
                <Button variant="outline" onClick={onClose}>
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
