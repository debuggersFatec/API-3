import { Dialog, Flex, Tabs, Text } from "@chakra-ui/react";
import { TabDashboard } from "./TabDashboard";
import { TabQuadro } from "./TabQuadro";
import { TabTarefasEquipes } from "./TabTarefasEquipes";

import { TabLixeiraEquipe } from "./TabLixeiraEquipe";
import { useProject } from "@/context/project/useProject";
import { ModalNewTask } from "./ModalNewTask";
import { SelectAddMemberProject } from "./SelectAddMemberProject";
import { ModalLeaveProject } from "./ModalLeaveProject";

export const EquipeTabs = () => {
  const { project } = useProject();

  return (
    <Tabs.Root
      w={"100%"}
      pr={"32px"}
      defaultValue="dashboard"
      colorScheme={"blue"}
    >
      <Flex mb={"10px"} alignItems="center">
        <Text textStyle={"xl"} fontWeight="bold" mb={"20px"}>
          {project?.name}
        </Text>
       <ModalLeaveProject />
      </Flex>

      {project && (
        // Mantém o ModalNewTask
        <Flex gap={4}>
          <Dialog.Root placement={"center"}>
            <Dialog.Trigger asChild></Dialog.Trigger>
            <ModalNewTask />
          </Dialog.Root>
          <SelectAddMemberProject />
        </Flex>
      )}
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
