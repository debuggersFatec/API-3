import { CheckList } from "./CheckList";
import { useProject } from "@/context/project/useProject";



export const TabTarefasEquipes = () => {
  const { project } = useProject();
  const tasks = project?.tasks || [];
  return <CheckList tasks={tasks} />;
};
