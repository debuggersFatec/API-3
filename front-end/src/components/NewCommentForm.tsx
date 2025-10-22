import { useAuth } from "@/context/auth/useAuth";
import { taskService } from "@/services";
import { Button, Field, Flex, Textarea } from "@chakra-ui/react";
import { useState } from "react";

interface NewCommentFormProps {
  taskUuid: string;
  onCommentCreated?: () => Promise<void>;
}

export const NewCommentForm = ({ taskUuid, onCommentCreated }: NewCommentFormProps) => {
  const { token } = useAuth();
  const [content, setContent] = useState("");

  const handleSubmit = async () => {
    if (!content.trim()) return;
    try {
      await taskService.createComment(taskUuid, content, token);
      setContent("");
      if (onCommentCreated) await onCommentCreated();
    } catch (err) {
      console.error("Erro ao criar comentário:", err);
    }
  };

  return (
    <Flex flexDir={"column"} alignItems="flex-start"gap={2} >
      <Field.Root h={"50%"} flex={1}>
        <Textarea
          name="comment"
          placeholder="Novo comentário ..."
          h={"100%"}
          onChange={(e) => setContent(e.target.value)}
          value={content}
        />
      </Field.Root>
      <Button type="button" onClick={handleSubmit}  w={'100%'}>
        Comentar
      </Button>
    </Flex>
  );
};
