import { Tabs } from "@chakra-ui/react";
import { TabDashboard } from "./TabDashboard";
import { TabQuadro } from "./TabQuadro";
import { TabTarefasEquipes } from "./TabTarefasEquipes";

export const EquipeTabs = () => {
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
      </Tabs.List>
      <Tabs.Content value="dashboard">
        <TabDashboard />
      </Tabs.Content>
      <Tabs.Content value="tarefas">
        <TabTarefasEquipes />
      </Tabs.Content>
      <Tabs.Content value="Quadro">
        <TabQuadro />
      </Tabs.Content>
    </Tabs.Root>
  );
};
