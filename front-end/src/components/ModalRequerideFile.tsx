import React, { useEffect, useRef } from "react";
import {
  Button,
  CloseButton,
  Dialog,
  FileUpload,
  Portal,
  useFileUploadContext,
} from "@chakra-ui/react";
import { HiUpload } from "react-icons/hi";
import { fileService } from "@/services/fileServices";
import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { taskService } from "@/services/taskServices";
import type { Task } from "@/types/task";
import { toast } from "@/utils/toast";

interface ModalRequerideFileProps {
  taskUuid: string;
  isOpen: boolean;
  onClose: () => void;
}

export const ModalRequerideFile = ({
  taskUuid,
  isOpen,
  onClose,
}: ModalRequerideFileProps) => {
  // Usaremos um ref que será atualizado por um componente filho
  // que chama `useFileUploadContext()` *dentro* de <FileUpload.Root />.
  const { refreshProject } = useProject();
  const { refreshUser, token } = useAuth();
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

  const handleSubimit = async () => {
    const files = filesRef.current;
    if (!files || files.length === 0) {
      toast("error", "Nenhum arquivo selecionado para envio.");
      return;
    }
    const formData = new FormData();
    files.forEach((f) => formData.append("files", f, f.name));
    formData.append("taskUuid", taskUuid);
    console.log(
      "Arquivos a enviar:",
      files.map((f) => f.name)
    );
    try {
      await fileService.uploadFiles(taskUuid, formData);
      const task = await taskService.getTaskById(taskUuid, token);
      // Monta o payload no formato que o backend espera (camelo/camelCase)
      const updatePayload: Record<string, unknown> = {
        title: task.title,
        description: task.description,
        due_date: task.due_date,
        status: "COMPLETED",
        priority: task.priority,
        responsible: task.responsible,
        requiredFile: task.requiredFile,
        isRequiredFile: task.is_required_file,
      };
      await taskService.updateTask(taskUuid, updatePayload as unknown as Task, token);
      await refreshProject();
      await refreshUser();
      onClose();
    } catch (err) {
      toast("error", "Falha no upload do arquivo.");
      console.error("Erro ao enviar arquivos:", err);
    }
  };
  return (
    <Dialog.Root open={isOpen} onOpenChange={() => onClose()}>
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content>
            <Dialog.Header>
              <Dialog.Title>
                Insira um arquivo para concluir a tarefa
              </Dialog.Title>
            </Dialog.Header>
            <Dialog.Body>
              <FileUpload.Root maxFiles={5}>
                <FileUpload.HiddenInput />
                <FileUpload.Trigger asChild>
                  <Button variant="outline" size="sm">
                    <HiUpload /> Subir arquivo
                  </Button>
                </FileUpload.Trigger>
                <FileUpload.List showSize clearable />
                <FileUploadConsumer targetRef={filesRef} />
              </FileUpload.Root>
            </Dialog.Body>
            <Dialog.Footer>
              <Dialog.ActionTrigger asChild>
                <Button variant="outline">Cancel</Button>
              </Dialog.ActionTrigger>
              <Button onClick={handleSubimit}>Enviar</Button>
            </Dialog.Footer>
            <Dialog.CloseTrigger asChild>
              <CloseButton size="sm" />
            </Dialog.CloseTrigger>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
};
