import { Flex, Tabs, Separator, Dialog, Box, Button } from "@chakra-ui/react";
import { MyTasks } from "./MyTasks";
import { EquipeDashboard } from "./EquipeDashboard";
import { VencidasTab } from "./VencidasTab";
import { CompletasTab } from "./CompletasTab";
import { ModalNewTeam } from "./ModalNewTeam";
import { FaPlus } from "react-icons/fa";
import { useState } from "react";
import { useAuth } from "@/context/useAuth";
import { RiLogoutCircleRLine } from "react-icons/ri";

export const Sidebar = () => {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<string>("minhasTasks");
  const filteredTasks =
    user?.tasks?.filter((task) => task.status !== "excluida") || [];
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
          w={"100%"}
        >
          <Tabs.List
            pr={"16px"}
            w={"200px"}
            flexDir={"column"}
            display={"flex"}
            pl={"24px"}
          >
            <h1>Tarefas</h1>
            <Tabs.Trigger value="minhasTasks" justifyContent={"space-between"}>
              Minhas Tasks
              <span style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}>
                {filteredTasks.length}
              </span>
            </Tabs.Trigger>
            <Tabs.Trigger value="vencidas" justifyContent={"space-between"}>
              Vencidas
              <span style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}>
                {
                  filteredTasks.filter((t) => {
                    if (!t.due_date) return false;
                    const hoje = new Date();
                    const data = new Date(t.due_date);
                    if (isNaN(data.getTime())) return false;
                    const dataTask = new Date(
                      data.getFullYear(),
                      data.getMonth(),
                      data.getDate()
                    );
                    const dataHoje = new Date(
                      hoje.getFullYear(),
                      hoje.getMonth(),
                      hoje.getDate()
                    );
                    return dataTask < dataHoje;
                  }).length
                }
              </span>
            </Tabs.Trigger>
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
              user.equipes.map((equipe) => {
                const equipeCount = filteredTasks.filter(
                  (t) => t.equipe_uuid === equipe.uuid
                ).length;
                return (
                  <Tabs.Trigger
                    key={equipe.uuid}
                    value={equipe.uuid}
                    justifyContent={"space-between"}
                  >
                    {equipe.name}
                    <span
                      style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}
                    >
                      {equipeCount}
                    </span>
                  </Tabs.Trigger>
                );
              })}
            <Separator maxW={"200px"} />
            <Tabs.Trigger value="completas" justifyContent={"space-between"}>
              Completas
              <span style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}>
                {filteredTasks.filter((t) => t.status === "completed").length}
              </span>
            </Tabs.Trigger>
            <Button
              onClick={() => logout()}
              variant="plain"
              color={"red.500"}
              alignContent={"flex-start"}
            >
              Logout <RiLogoutCircleRLine color="red" size={22} />
            </Button>
          </Tabs.List>
          <Tabs.Content value="minhasTasks">
            <MyTasks />
          </Tabs.Content>
          <Tabs.Content value="vencidas">
            <VencidasTab tasks={filteredTasks} />
          </Tabs.Content>
          <Tabs.Content value="completas">
            <CompletasTab tasks={filteredTasks} />
          </Tabs.Content>
          {user?.equipes &&
            user.equipes.map((equipe) => (
              <Tabs.Content key={equipe.uuid} value={equipe.uuid}>
                <EquipeDashboard
                  equipe={equipe}
                  isActive={activeTab === equipe.uuid}
                />
              </Tabs.Content>
            ))}
        </Tabs.Root>
      </Flex>
    </>
  );
};
