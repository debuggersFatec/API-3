import { Flex, Box, Icon, Text, Input, Textarea, Button } from "@chakra-ui/react";
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
import { Field, FieldLabel } from "@chakra-ui/react/field";
import { useRef, useState, useEffect } from "react";
import { MdOutlineMail, MdDelete } from "react-icons/md";
import axios from "axios";
import { AvatarUser } from "./AvatarUser";
import ChakraDatePicker from "./ChakraDatePicker";
import type { Task, TaskPriority } from "../types/task";
import { useAuth } from "../context/useAuth";
import { useEquipe } from "@/context/EquipeContext";


interface Member {
  uuid: string;
  img: string;
  name: string;
}

interface ModalEditTaskProps {
  equipe_uuid: string;
  membros: Member[];
  task: Task | null;
  open: boolean;
  onClose?: () => void;
}
function useEquipeSafe() {
  try {
    return useEquipe();
  } catch {
    return undefined;
  }
}
export const ModalEditTask = ({ task, equipe_uuid, membros, open, onClose }: ModalEditTaskProps) => {
  const { token } = useAuth();
  const equipe = useEquipeSafe();
  const fetchEquipe = equipe?.fetchEquipe;
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isDropdownOpenPriority, setIsDropdownOpenPriority] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const [formData, setFormData] = useState<Task | null>(
    task
      ? {
          uuid: task.uuid,
          title: task.title,
          description: task.description || "",
          due_date: task.due_date || null,
          status: task.status,
          priority: task.priority,
          equip_uuid: task.equip_uuid || equipe_uuid,
          responsible: task.responsible,
        }
      : null
  );

  useEffect(() => {
    if (task) {
      setFormData({
        uuid: task.uuid,
        title: task.title,
        description: task.description || "",
        due_date: task.due_date || null,
        status: task.status,
        priority: task.priority,
        equip_uuid: task.equip_uuid || equipe_uuid,
        responsible: task.responsible,
      });
    } else {
      setFormData(null);
    }
  }, [task, equipe_uuid]);

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

  const handleSelectMember = (member: Member) => {
    setFormData((prev: Task | null) =>
      prev
        ? {
            ...prev,
            responsible: member,
          }
        : prev
    );
    setIsDropdownOpen(false);
  };

  const handleSelectPriority = (priority: TaskPriority) => {
    setFormData((prev: Task | null) =>
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
    setFormData((prev: Task | null) =>
      prev
        ? {
            ...prev,
            due_date: date,
          }
        : prev
    );
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const payload = {
      ...formData!,
      status: formData!.responsible && formData!.responsible.uuid ? 'in-progress' : 'not-started',
    };

    axios.put(`http://localhost:8080/api/tasks/${formData!.uuid}`, payload, {
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
    })
      .then((response: import('axios').AxiosResponse) => {
        console.log("Task editada com sucesso!", response.data);
        if (fetchEquipe) fetchEquipe();
        if (onClose) onClose();
      })
      .catch((error: unknown) => {
        console.error("Ocorreu um erro ao editar:", error);
      });
  };

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev: Task | null) =>
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
    setFormData((prev: Task | null) =>
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
      await axios.delete(`http://localhost:8080/api/tasks/${formData.uuid}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      if (fetchEquipe) fetchEquipe();
      if (onClose) onClose();
    } catch (err) {
      console.error("Erro ao excluir task:", err);
    }
  };

  const prioritys: { label: string; value: TaskPriority }[] = [
    { label: "Baixa", value: "baixa" },
    { label: "Média", value: "media" },
    { label: "Alta", value: "alta" },
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
                  <Field.Root mb={"16px"} w="100%">
                    <Input
                      name="title"
                      value={formData!.title}
                      onChange={handleInputChange}
                      placeholder={"Dê um título para sua tarefa"}
                      variant={"flushed"}
                    />
                  </Field.Root>
                  <Button
                    variant="ghost"
                    colorScheme="red"
                    onClick={handleDelete}
                    aria-label="Excluir tarefa"
                    minW={"auto"}
                    px={2}
                  >
                    <MdDelete size={22} color="#E53E3E" />
                  </Button>
                  <DialogCloseTrigger asChild>
                    <Button variant="ghost" onClick={onClose} aria-label="Fechar modal">
                      X
                    </Button>
                  </DialogCloseTrigger>
                </Flex>
              </DialogTitle>
            </DialogHeader>

            <DialogBody>
              <Flex justifyContent={"space-between"} gap={"12px"}>
                <Box w={"100%"}>
                  <Field.Root h={"50%"}>
                    <FieldLabel>Descrição</FieldLabel>
                    <Textarea
                      name="description"
                      placeholder="Descrição"
                      h={"100%"}
                      onChange={handleInputChange}
                      value={formData!.description}
                    />
                  </Field.Root>
                </Box>

                <Box w={"100%"}>
                  <Field.Root>
                    <FieldLabel>Responsável</FieldLabel>
                    <Box position="relative" w="100%">
                      <Button
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
                              name={formData!.responsible!.name}
                              imageUrl={formData!.responsible!.img ? formData!.responsible!.img : ''}
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
                              <AvatarUser
                                name={member.name}
                                imageUrl={member.img || ''}
                                size="2xs"
                              />
                              <Text ml={2}>{member.name}</Text>
                            </Flex>
                          ))}
                        </Box>
                      )}
                    </Box>
                  </Field.Root>

                  <Field.Root>
                    <FieldLabel>Prioridade</FieldLabel>
                    <Box position="relative" w="100%">
                      <Button
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
                              onClick={() => handleSelectPriority(priority.value)}
                            >
                              <Text ml={2}>{priority.label}</Text>
                            </Flex>
                          ))}
                        </Box>
                      )}
                    </Box>
                  </Field.Root>

                  <Field.Root w={"100%"}>
                    <Box w={"100%"} position="relative">
                      <ChakraDatePicker
                        selected={formData!.due_date}
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
                      textAlign="center"
                      cursor="pointer"
                      onClick={() => fileInputRef.current?.click()}
                      _hover={{ borderColor: "blue.500" }}
                    >
                      <Icon
                        as={MdOutlineMail}
                        w={12}
                        h={12}
                        color="gray.400"
                      />
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

                  <Button w={"100%"} type="submit" colorScheme={"blue"}>
                    Salvar alterações
                  </Button>
                </Box>
              </Flex>
            </DialogBody>
          </form>
        </DialogContent>
      </DialogPositioner>
    </DialogRoot>
  );
}

