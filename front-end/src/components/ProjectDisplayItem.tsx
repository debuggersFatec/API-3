import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { projectServices } from "@/services/ProjectServices";
import type { ProjectRef } from "@/types/project";
import { Heading, Card, Button } from "@chakra-ui/react";

interface ProjectDisplayItemProps {
  project: ProjectRef;
}
export const ProjectCard = ({ project }: ProjectDisplayItemProps) => {
  const { token, refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { fetchProject } = useProject();

  return (
    <Card.Root bgColor={project.active ? "green" : "red"}>
      <Card.Header>
        <Heading size="md">{project.name}</Heading>
      </Card.Header>
      <Card.Body />
      <Card.Footer>
        <Button
          onClick={async () => {
            await fetchProject(project.uuid);
            await refreshUser();
          }}
          variant="outline"
        >
          Ver detalhes
        </Button>
        {project.active ? (
          <Button
            onClick={async () => {
              await projectServices.desactiveProject(project.uuid, token);
              await refreshUser();
              await refreshTeam();
            }}
            variant="outline"
          >
            Desativar
          </Button>
        ) : (
          <Button
            onClick={async () => {
              await projectServices.activeProject(project.uuid, token);
              await refreshUser();
              await refreshTeam();
            }}
            variant="outline"
          >
            Ativar
          </Button>
        )}
      </Card.Footer>
    </Card.Root>
  );
};
