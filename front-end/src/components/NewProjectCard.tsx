import { Card } from "@chakra-ui/react";
import { ModalNewProject } from "./ModalNewProject";

export const NewProjectCard = () => {
  return (
    <Card.Root bg={"purple.500"}>
      <Card.Header>
        <Card.Title>Novo Projeto</Card.Title>
      </Card.Header>
      <Card.Body></Card.Body>
      <Card.Footer>
        <ModalNewProject />
      </Card.Footer>
    </Card.Root>
  );
};
