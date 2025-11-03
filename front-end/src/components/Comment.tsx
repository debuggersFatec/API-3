import type { TaskComment } from "@/types/task";
import { Box, Button, Flex, Text } from "@chakra-ui/react";
import { AvatarUser } from "./AvatarUser";
import { formatDateShort } from "@/utils/formatters";
import { useAuth } from "@/context/auth/useAuth";
import { GoTrash } from "react-icons/go";
import { taskService } from "@/services";
import { toast } from "@/utils/toast";
interface CommentProps {
  comment: TaskComment;
  taskUuid: string;
  onCommentDelete?: () => Promise<void>;
}

export const Comment = ({
  comment,
  taskUuid,
  onCommentDelete,
}: CommentProps) => {
  const { user, token } = useAuth();

  const handleDelete = async () => {
    try {
      await taskService.deleteComment(taskUuid, comment.uuid, token);
      toast("success", "Comentário deletado com sucesso!");
      if (onCommentDelete) await onCommentDelete();
    } catch (error) {
      console.error("Erro ao deletar comentário:", error);
      toast("error", "Erro ao deletar comentário.");
    }
  };
  return (
    <Box border={"black"} py={2}>
      <Flex justifyContent={"space-between"}>
        <AvatarUser user={comment.author} size="2xs" />
        <Flex>
          <Text fontSize={"0.65rem"}>{formatDateShort(comment.createdAt)}</Text>
          {comment.author.uuid === user?.uuid && (
            <Button
              onClick={handleDelete}
              ml={2}
              size="2xs"
              colorScheme="red"
              variant="ghost"
            >
              <GoTrash color="red" />
            </Button>
          )}
        </Flex>
      </Flex>
      <Text fontSize={"0.75rem"} ml={4} mt={2}>
        {comment.comment}
      </Text>
    </Box>
  );
};
