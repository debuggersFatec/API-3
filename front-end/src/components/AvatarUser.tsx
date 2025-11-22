import type { UserRef } from "@/types/user";
import { Avatar } from "@chakra-ui/react";

interface AvatarUserProps {
  user: UserRef;
  size?: "sm" | "md" | "lg" | "xl" | "2xl" | "full" | "2xs" | "xs";
}

export const AvatarUser = ({ user, size }: AvatarUserProps) => {
  return (
    <Avatar.Root size={size || "md"} colorPalette="blue">
      <Avatar.Fallback name={user.name} />
      <Avatar.Image src={`http://localhost:8080/${user.img}`} />
    </Avatar.Root>
  );
};
