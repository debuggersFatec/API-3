// Removida a importação de createListCollection, pois não está sendo utilizada
import {
  Button,
  Flex,
  Box,
  Icon,
  Text,
  Input,
  Textarea,
} from "@chakra-ui/react";
import {
  Dialog,
  DialogBackdrop,
  DialogPositioner,
  DialogContent,
  DialogHeader,
  DialogBody,
  DialogCloseTrigger,
  DialogTitle,
} from "@chakra-ui/react/dialog";
import { Field, FieldLabel } from "@chakra-ui/react/field";
import { useRef, useState } from "react";
import { MdOutlineMail } from "react-icons/md";
import { useDisclosure } from "@chakra-ui/react/hooks";
// import axios from "axios";
import { AvatarUser } from "./AvatarUser";
import ChakraDatePicker from "./ChakraDatePicker"; // Certifique-se de que o caminho está correto

interface Member {
  uuid: string;
  img_url: string;
  name: string;
}

const membersData: Member[] = [
  {
    uuid: "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    img_url: "https://randomuser.me/api/portraits/women/1.jpg",
    name: "Ana Silva",
  },
  {
    uuid: "b2c3d4e5-f6a7-8901-2345-67890abcdef1",
    img_url: "https://randomuser.me/api/portraits/men/2.jpg",
    name: "Bruno Costa",
  },
  {
    uuid: "c3d4e5f6-a7b8-9012-3456-7890abcdef12",
    img_url: "https://randomuser.me/api/portraits/women/3.jpg",
    name: "Clara Santos",
  },
  {
    uuid: "d4e5f6a7-b8c9-0123-4567-890abcdef123",
    img_url: "https://randomuser.me/api/portraits/men/4.jpg",
    name: "Daniel Oliveira",
  },
  {
    uuid: "e5f6a7b8-c9d0-1234-5678-90abcdef1234",
    img_url: "https://randomuser.me/api/portraits/women/5.jpg",
    name: "Erika Lima",
  },
];

export const ModalNewTask = () => {
  const { open, onOpen, onClose } = useDisclosure();
  const [formData, setFormData] = useState({
    title: "",
    description: "",
    due_date: null as Date | null,
    status: "",
    priority: "",
    equip_uuid: "",
    arquivo: null as File | null,
    file_required: "",
    file_finish: "",
    responsible: {
      uuid: "",
      name: "",
      img_url: "",
    },
  });

  const [isDropdownOpen, setIsDropdownOpen] = useState(false);
  const [isDropdownOpenPriority, setIsDropdownOpenPriority] = useState(false);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const fileInputRef = useRef<HTMLInputElement>(null);

  const handleSelectMember = (member: Member) => {
    setFormData((prev) => ({
      ...prev,
      responsible: member,
    }));
    setIsDropdownOpen(false);
  };

  const handleSelectPriority = (priority: string) => {
    setFormData((prev) => ({
      ...prev,
      priority: priority,
    }));
    setIsDropdownOpenPriority(false);
  };

  const handleDateChange = (date: Date | null) => {
    setFormData((prev) => ({
      ...prev,
      due_date: date,
    }));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const dataToSend = new FormData();
    dataToSend.append("title", formData.title);
    dataToSend.append("description", formData.description);

    if (formData.due_date) {
      dataToSend.append("due_date", formData.due_date.toISOString());
    }

    dataToSend.append("priority", formData.priority);
    dataToSend.append("responsible_uuid", formData.responsible.uuid);

    if (formData.arquivo) {
      dataToSend.append("arquivo", formData.arquivo);
    }

    // axios
    //   .post("http://localhost:8080/api/tasks", dataToSend)
    //   .then((response) => {
    //     console.log("Dados enviados com sucesso!", response.data);
    //   })
    //   .catch((error) => {
    //     console.error("Ocorreu um erro:", error);
    //   });
    // onClose();
    console.log("task", formData);
  };

  const handleInputChange = (
    e: React.ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0] || null;
    setFormData((prev) => ({
      ...prev,
      arquivo: file,
    }));
  };

  const prioritys = [
    { label: "Baixa", value: "baixa" },
    { label: "Média", value: "media" },
    { label: "Alta", value: "alta" },
  ];

  return (
    <>
      <Button onClick={onOpen} variant={"outline"}>
        Criar nova tarefa
      </Button>

      <Dialog.Root open={open} onOpenChange={onClose}>
        <DialogBackdrop />
        <DialogPositioner>
          <DialogContent maxW={"800px"} w={"90%"}>
            <form onSubmit={handleSubmit}>
              <DialogHeader>
                <DialogTitle w={"100%"}>
                  <Field.Root mb={"16px"}>
                    <Input
                      name="title"
                      value={formData.title}
                      onChange={handleInputChange}
                      placeholder={"Dê um título para sua tarefa"}
                      variant={"flushed"}
                    />
                  </Field.Root>
                </DialogTitle>
                <DialogCloseTrigger asChild>
                  <Button variant="ghost" onClick={onClose}>
                    X
                  </Button>
                </DialogCloseTrigger>
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
                        value={formData.description}
                      />
                    </Field.Root>
                  </Box>

                  <Box
                    w={"100%"}
                    gap={"8px"}
                    display={"flex"}
                    flexDir={"column"}
                  >
                    <Field.Root>
                      <FieldLabel>Responsável</FieldLabel>
                      <Box position="relative" w={"100%"}>
                        <Button
                          onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                          variant="outline"
                          w="full"
                          justifyContent="space-between"
                        >
                          {formData.responsible.name ? (
                            <Flex
                              p={2}
                              align="center"
                              cursor="pointer"
                              _hover={{ bg: "gray.100" }}
                            >
                              <AvatarUser
                                name={formData.responsible.name}
                                imageUrl={formData.responsible.img_url}
                                size="2xs"
                              />
                              <Text ml={2}>{formData.responsible.name}</Text>
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
                            {membersData.map((member) => (
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
                                  imageUrl={member.img_url}
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
                          {formData.priority ? (
                            <Flex
                              w={"100%"}
                              p={2}
                              align="center"
                              cursor="pointer"
                              _hover={{ bg: "gray.100" }}
                            >
                              <Text ml={2}>{formData.priority}</Text>
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
                      <Box w={"100%"} position="relative">
                        <ChakraDatePicker
                          selected={formData.due_date}
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
                        {formData.arquivo && (
                          <Text mt={2} fontWeight="bold">
                            Arquivo selecionado: {formData.arquivo.name}
                          </Text>
                        )}
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
                      Concluído
                    </Button>
                  </Box>
                </Flex>
              </DialogBody>
            </form>
          </DialogContent>
        </DialogPositioner>
      </Dialog.Root>
    </>
  );
};
