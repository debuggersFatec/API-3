import { Avatar } from "@chakra-ui/react";

interface AvatarUserProps {
  name: string;
  imageUrl: string;
  size?: "sm" | "md" | "lg" | "xl" | "2xl" | "full" | "2xs" | "xs";
}

export const AvatarUser = ({ name, imageUrl, size }: AvatarUserProps) => {
  return (
    <Avatar.Root size={size || "md"} colorPalette="blue">
      <Avatar.Fallback name={name} />
      <Avatar.Image src={imageUrl} />
    </Avatar.Root>
  );
};
