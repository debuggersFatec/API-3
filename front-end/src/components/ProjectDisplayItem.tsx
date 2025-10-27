import { useAuth } from "@/context/auth/useAuth";
import { useProject } from "@/context/project/useProject";
import { useTeam } from "@/context/team/useTeam";
import { projectServices } from "@/services/ProjectServices";
import type { ProjectRef } from "@/types/project";
import { Heading, Card, Button } from "@chakra-ui/react";
import { toast } from "@/utils/toast";

interface ProjectDisplayItemProps {
  project: ProjectRef;
}
export const ProjectCard = ({ project }: ProjectDisplayItemProps) => {
  const { token, refreshUser } = useAuth();
  const { refreshTeam } = useTeam();
  const { fetchProject } = useProject();

  return (
    <Card.Root bgColor={project.active ? "#10B981" : "#E53E3E"}>
      <Card.Header>
        <Heading size="md">{project.name}</Heading>
      </Card.Header>
      <Card.Body />
      <Card.Footer>
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
            variant="outline"
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
            variant="outline"
          >
            Ativar
          </Button>
        )}
      </Card.Footer>
    </Card.Root>
  );
};
