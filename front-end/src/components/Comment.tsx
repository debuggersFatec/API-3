import type { TaskComment } from "@/types/task";
import { Box, Flex, Text } from "@chakra-ui/react";
import { AvatarUser } from "./AvatarUser";
import { formatDateShort } from "@/utils/formatDateShort";

interface CommentProps {
  comment: TaskComment;
}

export const Comment = ({ comment }: CommentProps) => {
  return (
    <Box border={"black"} py={2}>
      <Flex justifyContent={"space-between"}>
        <AvatarUser user={comment.author} size="2xs" />
        <Text fontSize={"0.65rem"}>{formatDateShort(comment.createdAt)}</Text>
      </Flex>
      <Text fontSize={"0.75rem"} ml={4} mt={2}>
        {comment.comment}
      </Text>
    </Box>
  );
};
