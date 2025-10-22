import type { TaskComment } from "@/types/task";
import { NewCommentForm } from "./NewCommentForm";
import { ScrollArea, Stack } from "@chakra-ui/react";
import { Comment } from "./Comment";

interface CommentsAreaProps {
  comments?: TaskComment[];
  taskUuid: string;
  onCommentChange?: () => Promise<void>;
}

export const CommentsArea = ({
  comments,
  taskUuid,
  onCommentChange,
}: CommentsAreaProps) => {
  return (
    <Stack mt={2}>
      <Stack border={"black"} gap="2">
        <ScrollArea.Root height="8rem" variant={"always"}>
          <ScrollArea.Viewport>
            <ScrollArea.Content paddingEnd="3" textStyle="sm">
              {comments?.map((comment) => (
                <Comment
                  key={comment.uuid}
                  comment={comment}
                  taskUuid={taskUuid}
                  onCommentDelete={onCommentChange}
                />
              ))}
            </ScrollArea.Content>
          </ScrollArea.Viewport>
          <ScrollArea.Scrollbar />
        </ScrollArea.Root>
      </Stack>

      <NewCommentForm taskUuid={taskUuid} onCommentCreated={onCommentChange} />
    </Stack>
  );
};
