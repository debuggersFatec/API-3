import { Tabs } from "@chakra-ui/react";
import { TabDashboard } from "./TabDashboard";
import { TabQuadro } from "./TabQuadro";
import { TabTarefasEquipes } from "./TabTarefasEquipes";
import { useEquipe } from "@/context/EquipeContext";
import { TabLixeiraEquipe } from "./TabLixeiraEquipe";

export const EquipeTabs = () => {
  const { equipeData } = useEquipe();

  return (
    <Tabs.Root
      w={"100%"}
      pr={"32px"}
      defaultValue="dashboard"
      colorScheme={"blue"}
    >
      <Tabs.List>
        <Tabs.Trigger value="dashboard">Dashboard</Tabs.Trigger>
        <Tabs.Trigger value="tarefas">Tarefas</Tabs.Trigger>
        <Tabs.Trigger value="Quadro">Quadro</Tabs.Trigger>
        <Tabs.Trigger value="lixo">Lixeira</Tabs.Trigger>
      </Tabs.List>
      <Tabs.Content value="dashboard">
        <TabDashboard tasks={equipeData?.tasks || []} />
      </Tabs.Content>
      <Tabs.Content value="tarefas">
        <TabTarefasEquipes tasks={equipeData?.tasks || []} />
      </Tabs.Content>
      <Tabs.Content value="Quadro">
        <TabQuadro />
      </Tabs.Content>
      <Tabs.Content value="lixo">
        <TabLixeiraEquipe lixeira={equipeData?.trashcan || []} />
      </Tabs.Content>
    </Tabs.Root>
  );
};
