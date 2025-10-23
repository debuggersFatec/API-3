import {
  Flex,
  Box,
  Icon,
  Text,
  Input,
  Textarea,
  Button,
} from "@chakra-ui/react";
import {
  DialogRoot,
  DialogBackdrop,
  DialogPositioner,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogCloseTrigger,
  DialogTitle,
} from "@chakra-ui/react/dialog";
import { Field } from "@chakra-ui/react/field";
import { useRef, useState, useEffect } from "react";
import { MdOutlineMail, MdDelete } from "react-icons/md";
import { AvatarUser } from "./AvatarUser";
import ChakraDatePicker from "./chakraDatePicker/ChakraDatePicker";
import type { Priority, Task } from "../types/task";
import { useAuth } from "../context/auth/useAuth";

import type { UserRef } from "@/types/user";
import { taskService } from "@/services";
import { useTeam } from "@/context/team/useTeam";
import { useProject } from "@/context/project/useProject";
import { CommentsArea } from "./CommentsArea";
import { toast } from "@/utils/toast";

interface ModalEditTaskProps {
  membros: UserRef[];
  task: Task;
  open: boolean;
  onClose?: () => void;
}

export const ModalEditTask = ({
  task,
  membros,
  open,
  onClose,
}: ModalEditTaskProps) => {
  const { token, refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { refreshProject } = useProject();

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isDropdownOpenPriority, setIsDropdownOpenPriority] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [formData, setFormData] = useState(task);

  useEffect(() => {
    setFormData(task);
  }, [task, task.project_uuid]);

  const reloadTask = async () => {
    try {
      const fresh = await taskService.getTaskById(task.uuid, token);
      if (fresh) setFormData(fresh);
    } catch (err) {
      console.error("Erro ao recarregar task:", err);
    }
  };

  if (!task || !formData) {
    return (
      <DialogRoot open={open} onOpenChange={onClose}>
        <DialogBackdrop />
        <DialogPositioner>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Carregando tarefa...</DialogTitle>
            </DialogHeader>
            <DialogBody>
              <Text>Buscando dados da tarefa...</Text>
            </DialogBody>
          </DialogContent>
        </DialogPositioner>
      </DialogRoot>
    );
  }

  const handleSelectMember = (member: UserRef) => {
    setFormData((prev) =>
      prev
        ? {
            ...prev,
            responsible: member,
          }
        : prev
    );
    setIsDropdownOpen(false);
  };

  const handleSelectPriority = (priority: Priority) => {
    setFormData((prev) =>
      prev
        ? {
            ...prev,
            priority: priority,
          }
        : prev
    );
    setIsDropdownOpenPriority(false);
  };

  const handleDateChange = (date: Date | null) => {
    setFormData((prev) =>
      prev
        ? {
            ...prev,
            due_date: date || undefined,
          }
        : prev
    );
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await taskService.updateTask(formData!.uuid, formData!, token);
      toast("success", "Tarefa atualizada com sucesso!");
      await refreshUser();
      await refreshProject();
      await refreshTeam();
      if (onClose) onClose();
    } catch (error) {
      console.error("Ocorreu um erro ao editar:", error);
    }
  };

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) =>
      prev
        ? {
            ...prev,
            [name]: value,
          }
        : prev
    );
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) =>
      prev
        ? {
            ...prev,
            arquivo: file,
          }
        : prev
    );
  };

  const handleDelete = async () => {
    if (!formData) return;

    try {
      await taskService.deleteTask(formData.uuid, token);
      toast("success", "Tarefa excluída com sucesso!");
      await refreshUser();
      await refreshProject();
      await refreshTeam();
      if (onClose) onClose();
    } catch (err) {
      console.error("Erro ao excluir tarefa:", err);
      toast("error", "Erro ao excluir tarefa.");
    }
  };

  const prioritys: { label: string; value: Priority }[] = [
    { label: "Baixa", value: "low" },
    { label: "Média", value: "medium" },
    { label: "Alta", value: "high" },
  ];

  return (
    <DialogRoot open={open} onOpenChange={onClose}>
      <DialogBackdrop />
      <DialogPositioner>
        <DialogContent maxW={"800px"} w={"90%"}>
          <form onSubmit={handleSubmit}>
            <DialogHeader>
              <DialogTitle w={"100%"}>
                <Flex align="center" justify="space-between" gap={2}>
                  <Field.Root w="100%" required>
                    <Input
                      name="title"
                      value={formData!.title}
                      onChange={handleInputChange}
                      placeholder={"Dê um título para sua tarefa"}
                      variant={"flushed"}
                    />
                  </Field.Root>

                  <DialogCloseTrigger asChild>
                    <Button
                      type="button"
                      variant="ghost"
                      onClick={onClose}
                      aria-label="Fechar modal"
                    >
                      X
                    </Button>
                  </DialogCloseTrigger>
                </Flex>
              </DialogTitle>
            </DialogHeader>

            <DialogBody>
              <Flex justifyContent={"space-between"} gap={"12px"}>
                <Box w={"100%"}>
                  <Field.Root h={"25%"}>
                    <Textarea
                      name="description"
                      placeholder="Descrição"
                      h={"100%"}
                      onChange={handleInputChange}
                      value={formData!.description}
                    />
                  </Field.Root>
                  <CommentsArea
                    taskUuid={task.uuid}
                    comments={formData.comments}
                    onCommentChange={reloadTask}
                  />
                </Box>

                <Box w={"100%"}>
                  <Field.Root>
                    <Box position="relative" w="100%" mb={"24px"}>
                      <Button
                        type="button"
                        onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                        variant="outline"
                        w="full"
                        justifyContent="space-between"
                      >
                        {formData!.responsible?.name ? (
                          <Flex
                            p={2}
                            align="center"
                            cursor="pointer"
                            _hover={{ bg: "gray.100" }}
                          >
                            <AvatarUser
                              user={formData!.responsible}
                              size="2xs"
                            />
                            <Text ml={2}>{formData!.responsible!.name}</Text>
                          </Flex>
                        ) : (
                          "Selecione um responsável"
                        )}
                      </Button>
                      {isDropdownOpen && (
                        <Box
                          ref={dropdownRef}
                          position="absolute"
                          w="full"
                          mt={2}
                          bg="white"
                          border="1px"
                          borderColor="gray.200"
                          borderRadius="md"
                          zIndex="10"
                          boxShadow="md"
                        >
                          {membros.map((member) => (
                            <Flex
                              key={member.uuid}
                              p={2}
                              align="center"
                              cursor="pointer"
                              _hover={{ bg: "gray.100" }}
                              onClick={() => handleSelectMember(member)}
                            >
                              <AvatarUser user={member} size="2xs" />
                              <Text ml={2}>{member.name}</Text>
                            </Flex>
                          ))}
                        </Box>
                      )}
                    </Box>
                  </Field.Root>

                  <Field.Root>
                    <Box position="relative" w="100%" mb={"8px"}>
                      <Button
                        type="button"
                        onClick={() =>
                          setIsDropdownOpenPriority(!isDropdownOpenPriority)
                        }
                        variant="outline"
                        w="full"
                        justifyContent="space-between"
                      >
                        {formData!.priority ? (
                          <Flex
                            w={"100%"}
                            p={2}
                            align="center"
                            cursor="pointer"
                            _hover={{ bg: "gray.100" }}
                          >
                            <Text ml={2}>{formData!.priority}</Text>
                          </Flex>
                        ) : (
                          "Defina uma prioridade"
                        )}
                      </Button>
                      {isDropdownOpenPriority && (
                        <Box
                          ref={dropdownRef}
                          position="absolute"
                          w="full"
                          mt={2}
                          bg="white"
                          border="1px"
                          borderColor="gray.200"
                          borderRadius="md"
                          zIndex="10"
                          boxShadow="md"
                        >
                          {prioritys.map((priority) => (
                            <Flex
                              key={priority.label}
                              p={2}
                              align="center"
                              cursor="pointer"
                              _hover={{ bg: "gray.100" }}
                              onClick={() =>
                                handleSelectPriority(priority.value)
                              }
                            >
                              <Text ml={2}>{priority.label}</Text>
                            </Flex>
                          ))}
                        </Box>
                      )}
                    </Box>
                  </Field.Root>

                  <Field.Root w={"100%"}>
                    <Box w={"100%"} position="relative" mb={"8px"}>
                      <ChakraDatePicker
                        selected={formData!.due_date || null}
                        onChange={handleDateChange}
                      />
                    </Box>
                  </Field.Root>

                  <Field.Root>
                    <Box
                      border="2px dashed"
                      borderColor="gray.300"
                      borderRadius="md"
                      p={6}
                      w={"100%"}
                      mb={"8px"}
                      textAlign="center"
                      cursor="pointer"
                      onClick={() => fileInputRef.current?.click()}
                      _hover={{ borderColor: "blue.500" }}
                    >
                      <Icon as={MdOutlineMail} w={12} h={12} color="gray.400" />
                      <Text mt={2} color="gray.500">
                        Anexar arquivo
                      </Text>
                      {/* {formData.arquivo && (
                        <Text mt={2} fontWeight="bold">
                          Arquivo selecionado: {formData.arquivo.name}
                        </Text>
                      )} */}
                    </Box>
                    <Input
                      type="file"
                      name="arquivo"
                      ref={fileInputRef}
                      onChange={handleFileChange}
                      position="absolute"
                      zIndex={-1}
                      opacity={0}
                      h="0"
                      w="0"
                      pointerEvents="none"
                    />
                  </Field.Root>

                  <Flex gap={2} maxW={"100%"}>
                    <Button
                      bg="red.500"
                      color="white"
                      _hover={{ bg: "red.600" }}
                      onClick={handleDelete}
                      aria-label="Excluir tarefa"
                      flex={1}
                      px={2}
                    >
                      Excluir
                      <MdDelete size={22} color="#fff" />
                    </Button>
                    <Button flex={1} type="submit" colorScheme={"blue"}>
                      Salvar alterações
                    </Button>
                  </Flex>
                </Box>
              </Flex>
            </DialogBody>
          </form>
        </DialogContent>
      </DialogPositioner>
    </DialogRoot>
  );
};
