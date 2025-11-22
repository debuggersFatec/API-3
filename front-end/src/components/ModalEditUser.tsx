import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { userService } from "@/services/userServices";
import {
  Button,
  CloseButton,
  Dialog,
  Field,
  FileUpload,
  Input,
  Portal,
  useDisclosure,
  VStack,
  useFileUploadContext,
} from "@chakra-ui/react";
import React, { useRef, useEffect, useState } from "react";
import { AvatarUser } from "./AvatarUser";
import { Navigate } from "react-router-dom";
import { toast } from "@/utils/toast";
import { HiUpload } from "react-icons/hi";

export const ModalEditUser = () => {
  const { user, refreshUser } = useAuth();
  const { refreshProject } = useProject();
  const { refreshTeam } = useTeam();
  const { open, onOpen, onClose } = useDisclosure();
  const [formData, setFormData] = useState({
    name: user?.name || "",
  });
  const filesRef = useRef<File[]>([]);

  type FileUploadContextLike = {
    getFiles?: () => File[];
    files?: File[];
    acceptedFiles?: File[];
  };

  function FileUploadConsumer({
    targetRef,
  }: {
    targetRef: React.MutableRefObject<File[]>;
  }) {
    const ctx = useFileUploadContext() as FileUploadContextLike | undefined;

    useEffect(() => {
      if (!ctx) return;
      const currentFiles = ctx.getFiles
        ? ctx.getFiles()
        : ctx.files ?? ctx.acceptedFiles ?? [];
      targetRef.current = Array.from(currentFiles);
    }, [ctx, ctx?.files, ctx?.acceptedFiles, targetRef]);

    return null;
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await userService.updateUser(formData.name, user?.img || "");
      const files = filesRef.current;
      if (files && files.length > 0) {
        const form = new FormData();
        form.append("file", files[0], files[0].name);
        await userService.updateImageUser(form);
      }

      toast("success", "Usuário atualizado com sucesso!");
      await refreshUser();
      await refreshProject();
      await refreshTeam();
      onClose();
    } catch (err) {
      console.error("Erro ao atualizar usuário:", err);
      toast("error", "Falha ao atualizar usuário. Veja o console.");
    }
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

      <Dialog.Root open={open} onOpenChange={onClose}>
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
                    <FileUpload.Root
                      maxFiles={1}
                      accept={["image/jpeg", "image/png", "image/jpg"]}
                    >
                      <FileUpload.HiddenInput />
                      <FileUpload.Trigger asChild>
                        <Button variant="outline" size="sm">
                          <HiUpload /> Atualizar imagem
                        </Button>
                      </FileUpload.Trigger>
                      <FileUpload.List />
                      <FileUploadConsumer targetRef={filesRef} />
                    </FileUpload.Root>
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
