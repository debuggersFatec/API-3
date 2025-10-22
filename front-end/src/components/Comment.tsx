import type { TaskComment } from "@/types/task";
import { Box, Button, Flex, Text } from "@chakra-ui/react";
import { AvatarUser } from "./AvatarUser";
import { formatDateShort } from "@/utils/formatDateShort";
import { useAuth } from "@/context/auth/useAuth";
import { GoTrash } from "react-icons/go";
import { taskService } from "@/services";
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
  return (
    <Box border={"black"} py={2}>
      <Flex justifyContent={"space-between"}>
        <AvatarUser user={comment.author} size="2xs" />
        <Flex>
          <Text fontSize={"0.65rem"}>{formatDateShort(comment.createdAt)}</Text>
          {comment.author.uuid === user?.uuid && (
            <Button
              onClick={async () => {
                await taskService.deleteComment(taskUuid, comment.uuid, token);
                if (onCommentDelete) await onCommentDelete();
              }}
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
