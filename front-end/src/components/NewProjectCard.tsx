import { Card, Flex, Icon, Dialog } from "@chakra-ui/react";
import { ModalNewProject } from "./ModalNewProject";
import { FaPlus } from "react-icons/fa";
import { useState } from "react";

export const NewProjectCard = () => {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <Card.Root 
        bg="surface.base" 
        borderWidth="2px" 
        borderStyle="dashed" 
        borderColor="border.emphasized"
        _hover={{ 
          borderColor: "blue.500",
          bg: "blue.50",
          transform: "translateY(-2px)",
          shadow: "md"
        }}
        transition="all 0.2s"
        cursor="pointer"
        minH="200px"
        onClick={() => setIsOpen(true)}
      >
        <Card.Body>
          <Flex 
            flexDir="column" 
            alignItems="center" 
            justifyContent="center" 
            h="100%" 
            gap={3}
          >
            <Icon 
              as={FaPlus} 
              boxSize={8} 
              color="blue.500"
            />
            <Card.Title 
              fontSize="lg" 
              fontWeight="600" 
              color="fg.muted"
            >
              Novo Projeto
            </Card.Title>
          </Flex>
        </Card.Body>
      </Card.Root>
      
      <Dialog.Root
        open={isOpen}
        onOpenChange={(e) => setIsOpen(e.open)}
        placement="center"
        motionPreset="slide-in-bottom"
      >
        <ModalNewProject onClose={() => setIsOpen(false)} />
      </Dialog.Root>
    </>
  );
};
