import {
  Flex,
  Box,
  Text,
  Input,
  Textarea,
  Button,
  Icon,
  ScrollArea,
  Portal,
} from "@chakra-ui/react";
import { Field } from "@chakra-ui/react/field";
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
import { AvatarUser } from "./AvatarUser";
import type { Task } from "../types/task";
import { formatPriority } from "@/utils/formatters";

interface ModalDisplayTaskProps {
  task: Task;
  open: boolean;
  onClose?: () => void;
}

export const ModalDisplayTask = ({
  task,
  open,
  onClose,
}: ModalDisplayTaskProps) => {
  const bgTextarea = "white";
  const commentBg = "#F7FAFC"; // gray.50
  if (!task) {
    return (
      <Portal>
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
      </Portal>
    );
  }

  const formatDate = (d?: Date | string | undefined) => {
    if (!d) return "-";
    const date = typeof d === "string" ? new Date(d) : d;
    if (Number.isNaN(date.getTime())) return "-";
    return date.toLocaleDateString();
  };

  return (
    <Portal>
      <DialogRoot open={open} onOpenChange={onClose}>
        <DialogBackdrop />
        <DialogPositioner>
          <DialogContent maxW={"800px"} w={"90%"}>
            <DialogHeader pb={0}>
              <DialogTitle w="100%">
                <Flex align="center" justify="space-between" gap={2}>
                  <Field.Root w="100%">
                    <Input
                      name="title"
                      value={task.title}
                      readOnly
                      variant="flushed"
                      fontWeight={600}
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
              <Box borderBottomWidth={1} borderColor="gray.200" mt={3} />
            </DialogHeader>

            <DialogBody px={6} pt={4} pb={6}>
              <Flex
                justifyContent={"space-between"}
                gap={"24px"}
                flexDirection={{ base: "column", md: "row" }}
              >
                <Box flex="1.5">
                  <Field.Root mb={4}>
                    <Text fontWeight={600} mb={2}>
                      Descrição
                    </Text>
                    <Textarea
                      readOnly
                      value={task.description || ""}
                      h="150px"
                      bg={bgTextarea}
                    />
                  </Field.Root>

                  <Box mb={4}>
                    <Text fontWeight={600} mb={2}>
                      Comentários
                    </Text>
                    {task.comments && task.comments.length > 0 ? (
                      <Box>
                        <ScrollArea.Root height="8rem" variant={"always"}>
                          <ScrollArea.Viewport>
                            <ScrollArea.Content paddingEnd="3" textStyle="sm">
                              {task.comments.map((comment) => (
                                <Box
                                  key={comment.uuid}
                                  p={3}
                                  borderRadius="md"
                                  bg={commentBg}
                                  mb={3}
                                >
                                  <Flex gap={3} align="start">
                                    {comment.author?.name ? (
                                      <AvatarUser
                                        user={{
                                          uuid: comment.author.uuid,
                                          name: comment.author.name,
                                        }}
                                        size="xs"
                                      />
                                    ) : null}
                                    <Box>
                                      <Text fontSize="sm" color="gray.700">
                                        {comment.comment}
                                      </Text>
                                      {comment.createdAt && (
                                        <Text
                                          fontSize="xs"
                                          color="gray.500"
                                          mt={1}
                                        >
                                          {formatDate(comment.createdAt)}
                                        </Text>
                                      )}
                                    </Box>
                                  </Flex>
                                </Box>
                              ))}
                            </ScrollArea.Content>
                          </ScrollArea.Viewport>
                        </ScrollArea.Root>
                      </Box>
                    ) : (
                      <Text color="gray.500">Sem comentários</Text>
                    )}
                  </Box>
                </Box>

                <Box flex="1" minW={{ base: "100%", md: "220px" }}>
                  <Box display="flex" flexDirection="column" gap={4}>
                    <Box>
                      <Text fontWeight={600} mb={2}>
                        Responsável
                      </Text>
                      {task.responsible ? (
                        <Flex align="center" gap={3}>
                          <AvatarUser user={task.responsible} size="sm" />
                          <Text fontSize="md" fontWeight={500}>
                            {task.responsible.name}
                          </Text>
                        </Flex>
                      ) : (
                        <Text color="gray.500" fontSize="sm">
                          Sem responsável
                        </Text>
                      )}
                    </Box>

                    <Box>
                      <Text fontWeight={600} mb={2}>
                        Prioridade
                      </Text>
                      <Field.Root>
                        <Input
                          readOnly
                          value={formatPriority(task.priority) || ""}
                          variant="outline"
                        />
                      </Field.Root>
                    </Box>

                    <Box>
                      <Text fontWeight={600} mb={2}>
                        Data de entrega
                      </Text>
                      <Field.Root>
                        <Input
                          readOnly
                          value={formatDate(task.due_date)}
                          variant="outline"
                        />
                      </Field.Root>
                    </Box>

                    <Box>
                      <Text fontWeight={600} mb={2}>
                        Arquivo necessário
                      </Text>
                      <Text>{task.isRequiredFile ? "Sim" : "Não"}</Text>
                    </Box>

                    <Box>
                      <Text fontWeight={600} mb={2}>
                        Arquivo anexado
                      </Text>
                      <Box
                        borderStyle="dashed"
                        borderWidth="2px"
                        borderColor="gray.300"
                        borderRadius="md"
                        p={4}
                        textAlign="center"
                      >
                        <Flex justify="center" gap={3} align="center">
                          <Icon viewBox="0 0 24 24" boxSize={6}>
                            <path
                              d="M20 14v4a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-4"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="1.5"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                            />
                            <path
                              d="M16 6l-4-4-4 4"
                              fill="none"
                              stroke="currentColor"
                              strokeWidth="1.5"
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              transform="translate(0 4)"
                            />
                          </Icon>
                        </Flex>
                      </Box>
                    </Box>
                  </Box>
                </Box>
              </Flex>
            </DialogBody>
          </DialogContent>
        </DialogPositioner>
      </DialogRoot>
    </Portal>
  );
};
