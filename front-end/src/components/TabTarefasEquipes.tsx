import type { TaskTeam } from "@/types/task";
import { CheckList } from "./CheckList";

interface TabTarefasEquipesProps {
  tasks: TaskTeam[];
}

export const TabTarefasEquipes = ({ tasks }: TabTarefasEquipesProps) => {
  return <CheckList tasks={tasks} />;
};
