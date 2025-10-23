import { Dialog, Button, CloseButton, Field, Input } from "@chakra-ui/react";
import { AxiosError } from "axios";
import { useAuth } from "@/context/auth/useAuth";
import { useState } from "react";
import { teamServices } from "@/services/teamServices";
import type { UserRef } from "@/types/user";

interface ModalNewTeamProps {
  onClose?: () => void;
}

export const ModalNewTeam = ({ onClose }: ModalNewTeamProps) => {
  const [teamName, setTeamName] = useState("");
  const { user, token, refreshUser } = useAuth();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    if (!user) {
      alert("Usuário não autenticado. Faça login novamente.");
      return;
    }
    const member: UserRef = { uuid: user.uuid, name: user.name, img: user.img };
    try {
      await teamServices.createTeam(teamName, member, token);
      await refreshUser();
      if (onClose) onClose();
      setTeamName("");
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
        alert("Erro ao criar equipe.");
      }
    }
  };

  return (
    <>
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
    </>
  );
};
