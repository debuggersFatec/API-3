import { useProject } from "@/context/project/useProject";
import { CheckList } from "./CheckList";

export const TabLixeiraEquipe = () => {
  const { project } = useProject();
  const lixeira = project?.trashcan || [];

  if (!lixeira || lixeira.length === 0) {
    return <div>Sem tarefas na lixeira</div>;
  }
  return (
    <CheckList tasks={lixeira} />
  );
};
