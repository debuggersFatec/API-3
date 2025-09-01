import { Button, HStack } from "@chakra-ui/react";

export const App = () => {
  return (
    <>
      <p>Hello World</p>
      <HStack>
        <Button colorScheme={"dark"} color={"black"}>
          Click me
        </Button>
        <Button>Click me</Button>
      </HStack>
    </>
  );
};
