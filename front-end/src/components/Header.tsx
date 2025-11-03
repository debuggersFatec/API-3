import { Box, Dialog, Icon, Image } from "@chakra-ui/react";
import { ColorModeButton } from "./ui/color-mode";
import { FaBell, FaLink } from "react-icons/fa";
import logo from "@/assets/logo.svg";
import { ModalEditUser } from "./ModalEditUser";

export const Header = () => {
  return (
    <Box
      h={"80px"}
      flexDirection="row"
      display="flex"
      alignItems="center"
      justifyContent="space-between"
      px="24px"
    >
      <Image src={logo} alt="" />
      <Box
        flexDirection="row"
        display="flex"
        alignItems="center"
        justifyContent="flex-end"
        gap={4}
        padding={4}
      >
        <Icon as={FaLink} boxSize={5} cursor="pointer" />
        <Icon as={FaBell} boxSize={5} cursor="pointer" />
        <ColorModeButton w="24px" h="24px" fontSize="12px" minW={0} />
        <Dialog.Root placement={"center"}>
          <Dialog.Trigger asChild></Dialog.Trigger>
          <ModalEditUser/>
        </Dialog.Root>
      </Box>
    </Box>
  );
};
