import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { projectServices } from "@/services/ProjectServices";
import { toast } from "@/utils/toast";
import { Dialog, Button, Flex } from "@chakra-ui/react";
import { useState } from "react";
import { IoMdExit } from "react-icons/io";

interface ModalLeaveProjectProps {
  setActiveTab: (tab: string) => void;
}

export const ModalLeaveProject = ({ setActiveTab }: ModalLeaveProjectProps) => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const { refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { project } = useProject();

  const handleLeaveTeam = async () => {
    if (!project) return toast("error", "Dados da projeto não disponíveis.");
    try {
      await projectServices.leaveProject(project.uuid);
      toast("success", `Você saiu da projeto ${project.name}.`);
      refreshUser();
      refreshTeam();
      setActiveTab("minhasTasks");
      setIsDialogOpen(false);
    } catch (error) {
      toast(
        "error",
        "Ocorreu um problema. Tente novamente mais tarde." + project.uuid
      );
      console.error("Erro ao sair do time:", error);
    }
  };
  console.log(project?.uuid);
  return (
    <Dialog.Root
      placement="center"
      motionPreset="slide-in-bottom"
      open={isDialogOpen}
      onOpenChange={(details) => setIsDialogOpen(details.open)}
    >
      <Dialog.Trigger asChild>
        <Button
          bg="white"
          _hover={{ bg: "red.50" }}
          title="Sair da projeto"
          color={"red"}
        >
          Sair
          <IoMdExit color="red" />
        </Button>
      </Dialog.Trigger>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content>
          <Dialog.Header>
            <Dialog.Title>Confirmar saída</Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            Tem certeza de que deseja sair da projeto{" "}
            <b>{project?.name || "atual"}</b>?
          </Dialog.Body>
          <Dialog.Footer>
            <Flex justify="flex-end" gap={3}>
              <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                Cancelar
              </Button>
              <Button colorPalette="red" onClick={handleLeaveTeam}>
                Sair da projeto
              </Button>
            </Flex>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
};
