import { useProject } from "@/context/project/useProject";

export const TabLixeiraEquipe = () => {
  const { project } = useProject();
  const lixeira = project?.trashcan || [];

  if (!lixeira || lixeira.length === 0) {
    return <div>Sem tarefas na lixeira</div>;
  }
  return (
    <>
      {lixeira.map((task) => (
        <h1 key={task.uuid}>{task.title}</h1>
      ))}
    </>
  );
};
