import { Avatar } from "@chakra-ui/react"

interface AvatarUserProps {
  name: string;
  imageUrl: string;
}

export const AvatarUser = ({ name, imageUrl }: AvatarUserProps) => {
  return (
    <Avatar.Root colorPalette="blue">
      <Avatar.Fallback name={name} />
      <Avatar.Image src={imageUrl} />
    </Avatar.Root>
  )
}
