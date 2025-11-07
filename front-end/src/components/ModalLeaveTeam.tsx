import { useAuth } from "@/context/auth/useAuth";
import { useTeam } from "@/context/team/useTeam";
import { teamServices } from "@/services";
import { toast } from "@/utils/toast";
import { Dialog, Button, Flex } from "@chakra-ui/react";
import { useState } from "react";
import { IoMdExit } from "react-icons/io";
interface ModalLeaveTeamProps {
  setActiveTab: (tab: string) => void;
}

export const ModalLeaveTeam = ({ setActiveTab }: ModalLeaveTeamProps) => {
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const { refreshUser } = useAuth();
  const { refreshTeam, teamData } = useTeam();

  const handleLeaveTeam = async () => {
    if (!teamData) return toast("error", "Dados da equipe não disponíveis.");
    try {
      await teamServices.leaveTeam(teamData.uuid);
      toast("success", `Você saiu da equipe ${teamData.name}.`);
      refreshUser();
      refreshTeam();
      setIsDialogOpen(false);
      setActiveTab("minhasTasks");
    } catch (error) {
      toast(
        "error",
        "Ocorreu um problema. Tente novamente mais tarde." + teamData.uuid
      );
      console.error("Erro ao sair do time:", error);
    }
  };
  console.log(teamData?.uuid);
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
          title="Sair da equipe"
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
            Tem certeza de que deseja sair da equipe{" "}
            <b>{teamData?.name || "atual"}</b>?
          </Dialog.Body>
          <Dialog.Footer>
            <Flex justify="flex-end" gap={3}>
              <Button variant="outline" onClick={() => setIsDialogOpen(false)}>
                Cancelar
              </Button>
              <Button colorPalette="red" onClick={handleLeaveTeam}>
                Sair da equipe
              </Button>
            </Flex>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
};
