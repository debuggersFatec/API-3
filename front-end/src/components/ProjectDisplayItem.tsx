import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { projectServices } from "@/services/ProjectServices";
import type { ProjectRef } from "@/types/project";
import { Heading, Card, Button, Badge, Flex } from "@chakra-ui/react";
import { toast } from "@/utils/toast";

interface ProjectDisplayItemProps {
  project: ProjectRef;
}
export const ProjectCard = ({ project }: ProjectDisplayItemProps) => {
  const { token, refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { fetchProject } = useProject();

  return (
    <Card.Root 
      bg="surface.base"
      borderWidth="1px"
      borderColor="border.muted"
      _hover={{ 
        shadow: "lg",
        transform: "translateY(-2px)",
        borderColor: "border.emphasized"
      }}
      transition="all 0.2s"
      minH="200px"
    >
      <Card.Header pb={2}>
        <Flex alignItems="center" justifyContent="space-between">
          <Heading size="md" fontWeight="600">{project.name}</Heading>
          <Badge 
            colorPalette={project.active ? "green" : "red"}
            variant="subtle"
            fontSize="xs"
          >
            {project.active ? "Ativo" : "Inativo"}
          </Badge>
        </Flex>
      </Card.Header>
      <Card.Body py={3} />
      <Card.Footer pt={3} gap={2} flexWrap="wrap">
        {project.active && (
          <Button
            onClick={async () => {
              const fetched = await fetchProject(project.uuid);
              if (!fetched) {
                toast("error", "Você não tem permissão para visualizar este projeto.");
                return;
              }
              await refreshUser();
            }}
            variant="solid"
            colorPalette="blue"
            size="sm"
            flex="1"
          >
            Ver detalhes
          </Button>
        )}

        {project.active ? (
          <Button
            onClick={async () => {
              const fetched = await fetchProject(project.uuid);
              if (!fetched) {
                toast("error", "Você não tem permissão para alterar o status deste projeto.");
                return;
              }
              await projectServices.desactiveProject(project.uuid, token);
              await refreshUser();
              await refreshTeam();
            }}
            variant="outline"
            colorPalette="red"
            size="sm"
            flex="1"
          >
            Desativar
          </Button>
        ) : (
          <Button
            onClick={async () => {
              const fetched = await fetchProject(project.uuid);
              if (!fetched) {
                toast("error", "Você não tem permissão para alterar o status deste projeto.");
                return;
              }
              await projectServices.activeProject(project.uuid, token);
              await refreshUser();
              await refreshTeam();
            }}
            variant="solid"
            colorPalette="green"
            size="sm"
            flex="1"
          >
            Ativar
          </Button>
        )}
      </Card.Footer>
    </Card.Root>
  );
};
