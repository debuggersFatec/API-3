import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { userService } from "@/services/userServices";
import {
  Button,
  CloseButton,
  Dialog,
  Field,
  Input,
  Portal,
  useDisclosure,
  VStack,
} from "@chakra-ui/react";
import { useState } from "react";
import { AvatarUser } from "./AvatarUser";
import { Navigate } from "react-router-dom";

export const ModalEditUser = () => {
  const { user, token, refreshUser } = useAuth();
  const { refreshProject } = useProject();
  const { refreshTeam } = useTeam();
  const { open, onOpen, onClose } = useDisclosure();
  const [formData, setFormData] = useState({
    name: user?.name || "",
    img: user?.img || "",
  });

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await userService.updateUser(formData.name, formData.img, token);
    await refreshUser();
    await refreshProject();
    await refreshTeam();
    onClose();
  };

  if (user === null) {
    return <Navigate to="/login" replace />;
  }

  return (
    <>
      <Button variant="ghost" size="sm" onClick={onOpen}>
        <AvatarUser
          user={{ name: user.name, uuid: user.uuid, img: user.img }}
        />
      </Button>

      <Dialog.Root
        open={open}
        onOpenChange={onClose}
      >
        <Portal>
          <Dialog.Backdrop />
          <Dialog.Positioner>
            <Dialog.Content>
              <Dialog.Header>
                <Dialog.Title>Editar usuário</Dialog.Title>
                <Dialog.CloseTrigger asChild>
                  <CloseButton size="sm" />
                </Dialog.CloseTrigger>
              </Dialog.Header>
              <Dialog.Body>
                <form onSubmit={handleSubmit}>
                  <VStack>
                    <Field.Root>
                      <Field.Label>Nome</Field.Label>
                      <Input
                        name="name"
                        value={formData.name}
                        placeholder={user?.name || ""}
                        onChange={(e) =>
                          setFormData({ ...formData, name: e.target.value })
                        }
                      />
                    </Field.Root>
                    <Field.Root>
                      <Field.Label>Imagem de perfil</Field.Label>
                      <Input
                        name="img"
                        value={formData.img}
                        onChange={(e) => {
                          setFormData({ ...formData, img: e.target.value });
                        }}
                      />
                    </Field.Root>
                    <Button type="submit">Salvar</Button>
                  </VStack>
                </form>
              </Dialog.Body>
            </Dialog.Content>
          </Dialog.Positioner>
        </Portal>
      </Dialog.Root>
    </>
  );
};
