import { Flex, Text } from "@chakra-ui/react";

interface TarefasAtribuidasItemProps {
  usuario: {
    name: string;
    countDeTarefas: number;
    uuiddousuario: string;
  };
}

export const TarefasAtribuidasItem = ({
  usuario: { countDeTarefas, name },
}: TarefasAtribuidasItemProps) => {
  return (
    <Flex
      justifyContent="space-between"
      alignItems="center"
      borderRadius="md"
      _hover={{ bg: "gray.50" }}
    >
      <Text fontSize={'14px'}>{name}</Text>
      <Text color="gray.500" fontWeight="bold">
        {countDeTarefas}
      </Text>
    </Flex>
  );
};
