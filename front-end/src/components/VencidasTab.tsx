import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { CheckList } from "./CheckList";
import { useState, useEffect } from "react";
import type { TaskUser } from "@/types/task";

interface VencidasTabProps {
  tasks?: TaskUser[];
}

export const VencidasTab = ({ tasks }: VencidasTabProps) => {
  const [vencidas, setVencidas] = useState<TaskUser[]>([]);

  useEffect(() => {
    if (tasks) {
      const hoje = new Date();
      const vencidas = tasks.filter((t) => {
        if (!t.due_date) return false;
        let data: Date;
        if (typeof t.due_date === "object" && t.due_date !== null && "getTime" in t.due_date) {
          data = t.due_date as Date;
        } else {
          data = new Date(t.due_date);
        }
        if (isNaN(data.getTime())) return false;
        // Ignora horas
        const dataTask = new Date(data.getFullYear(), data.getMonth(), data.getDate());
        const dataHoje = new Date(hoje.getFullYear(), hoje.getMonth(), hoje.getDate());
        return dataTask < dataHoje;
      });
      setVencidas(vencidas);
    }
  }, [tasks]);

  return (
    <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
      <SectionHeader title="Vencidas" />
      <CheckList tasks={vencidas} />
    </Box>
  );
};
