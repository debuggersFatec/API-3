import React, { useState, useRef } from "react";
import { Button, Icon, Box, Flex } from "@chakra-ui/react";
import {
  DialogRoot,
  DialogBackdrop,
  DialogPositioner,
  DialogContent,
  DialogHeader,
  DialogBody,
} from "@chakra-ui/react/dialog";
import type { ButtonProps } from "@chakra-ui/react";

interface DestructiveButtonProps extends Omit<ButtonProps, "onClick"> {
  children?: React.ReactNode;
  icon?: React.ElementType;
  confirmTitle?: string;
  confirmDescription?: string;
  onConfirm: () => Promise<void> | void;
}

export const DestructiveButton = ({
  children = "Excluir",
  icon,
  confirmTitle = "Confirmação",
  confirmDescription = "Tem certeza que deseja excluir essa tarefa?",
  onConfirm,
  ...btnProps
}: DestructiveButtonProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const cancelRef = useRef<HTMLButtonElement | null>(null);

  const open = () => setIsOpen(true);
  const close = () => setIsOpen(false);

  const handleConfirm = async () => {
    try {
      setIsLoading(true);
      await onConfirm();
    } finally {
      setIsLoading(false);
      close();
    }
  };

  return (
    <>
      <Button
        bg="red.500"
        color="white"
        _hover={{ bg: "red.600" }}
        onClick={open}
        aria-label={typeof children === "string" ? children : "Excluir"}
        {...btnProps}
      >
        {icon && <Icon as={icon} mr={2} />}
        {children}
      </Button>

      <DialogRoot open={isOpen} onOpenChange={(v) => (v ? open() : close())}>
        <DialogBackdrop />
        <DialogPositioner>
          <DialogContent aria-label={confirmTitle}>
            <DialogHeader>
              <Box fontSize="lg" fontWeight="bold">{confirmTitle}</Box>
            </DialogHeader>
            <DialogBody>
              <Box mb={4}>{confirmDescription}</Box>
              <Flex justifyContent="flex-end" gap={3}>
                <Button ref={cancelRef} onClick={close} variant="ghost">
                  Cancelar
                </Button>
                <Button colorPalette="red" onClick={handleConfirm} disabled={isLoading}>
                  {isLoading ? "Confirmando..." : "Excluir"}
                </Button>
              </Flex>
            </DialogBody>
          </DialogContent>
        </DialogPositioner>
      </DialogRoot>
    </>
  );
};

export default DestructiveButton;
