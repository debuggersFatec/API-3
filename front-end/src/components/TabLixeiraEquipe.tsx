import type { TaskTeam } from "@/types/task";

interface TabLixeiraEquipeProps {
  lixeira: TaskTeam[] | undefined;
}
export const TabLixeiraEquipe = ({ lixeira }: TabLixeiraEquipeProps) => {
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
