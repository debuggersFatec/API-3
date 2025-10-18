import { Tabs, Text } from "@chakra-ui/react";
import { TabDashboard } from "./TabDashboard";
import { TabQuadro } from "./TabQuadro";
import { TabTarefasEquipes } from "./TabTarefasEquipes";

import { TabLixeiraEquipe } from "./TabLixeiraEquipe";
import { useProject } from "@/context/project/useProject";

export const EquipeTabs = () => {
  const { project } = useProject();

  return (
    <Tabs.Root
      w={"100%"}
      pr={"32px"}
      defaultValue="dashboard"
      colorScheme={"blue"}
    >
      <Text>{project?.name}</Text>
      <Tabs.List>
        <Tabs.Trigger value="dashboard">Dashboard</Tabs.Trigger>
        <Tabs.Trigger value="tarefas">Tarefas</Tabs.Trigger>
        <Tabs.Trigger value="Quadro">Quadro</Tabs.Trigger>
        <Tabs.Trigger value="lixo">Lixeira</Tabs.Trigger>
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
      <Tabs.Content value="lixo">
        <TabLixeiraEquipe />
      </Tabs.Content>
    </Tabs.Root>
  );
};
