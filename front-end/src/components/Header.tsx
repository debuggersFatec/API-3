import { Box } from "@chakra-ui/react";
import { AvatarUser } from "./AvatarUser";
import { ColorModeButton } from "./ui/color-mode";

export const Header = () => {
  return (
    <Box>
      <ColorModeButton />
      <AvatarUser name="Matheus Karnas" imageUrl="" />
    </Box>
  );
};
