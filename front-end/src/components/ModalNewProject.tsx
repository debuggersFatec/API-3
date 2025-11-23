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
      if (onClose) onClose();
      setProjectName("");
      refreshUser();
    } catch (error) {
      toast("error", "Erro ao criar projeto.");
      console.error("Erro ao criar projeto:", error);
    }
  };

  return (
    <>
      <Dialog.Backdrop 
        bg="blackAlpha.600" 
        backdropFilter="blur(4px)"
      />
      <Dialog.Positioner zIndex={1500}>
        <Dialog.Content
          maxW="500px"
          bg="surface.base"
          shadow="2xl"
          borderRadius="lg"
        >
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
            <Dialog.Footer gap={2}>
              <Dialog.ActionTrigger asChild>
                <Button 
                  type="button" 
                  variant="outline" 
                  onClick={() => {
                    if (onClose) onClose();
                  }}
                >
                  Cancelar
                </Button>
              </Dialog.ActionTrigger>
              <Button type="submit" colorPalette="blue">Criar</Button>
            </Dialog.Footer>
          </form>
          <Dialog.CloseTrigger asChild>
            <CloseButton size="sm" />
          </Dialog.CloseTrigger>
        </Dialog.Content>
      </Dialog.Positioner>
    </>
  );
};
