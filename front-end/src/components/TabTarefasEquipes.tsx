import type { TaskProject } from "@/types/task";
import { CheckList } from "./CheckList";

interface TabTarefasEquipesProps {
  tasks: TaskProject[];
}

export const TabTarefasEquipes = ({ tasks }: TabTarefasEquipesProps) => {
  return <CheckList tasks={tasks} />;
};
