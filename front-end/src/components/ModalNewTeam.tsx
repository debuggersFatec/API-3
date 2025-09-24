import { useState } from "react";
import {
  Portal,
  Dialog,
  Button,
  CloseButton,
  Field,
  Input,
} from "@chakra-ui/react";
import { useAuth } from "@/context/AuthContext";
import axios, { AxiosError } from "axios";

export const ModalNewTeam = () => {
  const [teamName, setTeamName] = useState("");
  const { user, token } = useAuth();

  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();
    try {
      await axios.post(
        "http://localhost:8080/api/equipes",
        {
          name: teamName,
          membros: [
            {
              uuid: user?.uuid,
              name: user?.name,
              email: user?.email,
              img: user?.img || null,
            },
          ],
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert("Equipe criada com sucesso!");
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
                <Button variant="outline">Cancel</Button>
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
  );
};
