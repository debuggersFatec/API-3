import {
  Dialog,
  Button,
  CloseButton,
  Field,
  Input,
  Portal,
} from "@chakra-ui/react";
import { useAuth } from "@/context/auth/useAuth";
import { useState } from "react";
import { teamServices } from "@/services";
import type { UserRef } from "@/types/user";
import { toast } from "@/utils/toast";

interface ModalNewTeamProps {
  onClose?: () => void;
}

export const ModalNewTeam = ({ onClose }: ModalNewTeamProps) => {
  const [teamName, setTeamName] = useState("");
  const { user, refreshUser } = useAuth();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!user) {
      alert("Usuário não autenticado. Faça login novamente.");
      return;
    }
    const member: UserRef = { uuid: user.uuid, name: user.name, img: user.img };
    try {
      await teamServices.createTeam(teamName, member);
      toast("success", "Equipe criada com sucesso!");
      await refreshUser();
      if (onClose) onClose();
      setTeamName("");
    } catch (error) {
      console.error("Erro ao criar equipe:", error);
      toast("error", "Erro ao criar equipe.");
    }
  };

  return (
    <>
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <Dialog.Header>
              <Dialog.Title>Criar uma nova equipe</Dialog.Title>
            </Dialog.Header>
            <form onSubmit={handleSubmit}>
              <Dialog.Body>
                <Field.Root mb={"16px"}>
                  <Input
                    name="title"
                    value={teamName}
                    onChange={(e) => setTeamName(e.target.value)}
                    placeholder={"Nome da equipe"}
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
      </Portal>
    </>
  );
};
