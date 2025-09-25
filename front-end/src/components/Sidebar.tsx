import { Flex, Tabs, Separator, Dialog, Box } from "@chakra-ui/react";
import { MyTasks } from "./MyTasks";
import { EquipeDashboard } from "./EquipeDashboard";
import { VencidasTab } from "./VencidasTab";
import { CompletasTab } from "./CompletasTab";
import { LixoTab } from "./LixoTab";
import { ModalNewTeam } from "./ModalNewTeam";
import { FaPlus } from "react-icons/fa";
import { useAuth } from "@/context/AuthContext";
import { useState } from "react";

export const Sidebar = () => {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState<string>("minhasTasks");
  return (
    <>
      <Flex>
        <Tabs.Root
          value={activeTab}
          onValueChange={(details) => setActiveTab(details.value)}
          defaultValue="minhasTasks"
          variant={"plain"}
          flexDir={"row"}
          display={"flex"}
          w={"100vw"}
        >
          <Tabs.List
            pr={"16px"}
            w={"20vw"}
            flexDir={"column"}
            display={"flex"}
            pl={"24px"}
          >
            <h1>Tarefas</h1>
            <Tabs.Trigger value="minhasTasks">Minhas Tasks</Tabs.Trigger>
            <Tabs.Trigger value="vencidas">Vencidas</Tabs.Trigger>
            <Flex alignItems={"center"} justify={"space-between"}>
              <h1>Equipes</h1>
              <Dialog.Root placement={"center"}>
                <Dialog.Trigger asChild>
                  <Box bg="blue.500" borderRadius="md" p={0.5} border="none">
                    <FaPlus color="white" />
                  </Box>
                </Dialog.Trigger>
                <ModalNewTeam />
              </Dialog.Root>
            </Flex>
            {user?.equipes &&
              user.equipes.map((equipe) => (
                <Tabs.Trigger key={equipe.uuid} value={equipe.uuid}>
                  {equipe.name}
                </Tabs.Trigger>
              ))}
            <Separator maxW={"200px"} />
            <Tabs.Trigger value="completas">Completas</Tabs.Trigger>
            <Tabs.Trigger value="lixo">Lixo</Tabs.Trigger>
          </Tabs.List>
          <Tabs.Content value="minhasTasks">
            <MyTasks />
          </Tabs.Content>
          <Tabs.Content value="vencidas">
            <VencidasTab />
          </Tabs.Content>
          <Tabs.Content value="completas">
            <CompletasTab />
          </Tabs.Content>
          <Tabs.Content value="lixo">
            <LixoTab />
          </Tabs.Content>

          {user?.equipes &&
            user.equipes.map((equipe) => (
              <Tabs.Content key={equipe.uuid} value={equipe.uuid}>
                <EquipeDashboard equipe={equipe} isActive={activeTab === equipe.uuid} />
              </Tabs.Content>
            ))}
        </Tabs.Root>
      </Flex>
    </>
  );
};
