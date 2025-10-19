import { Flex, Tabs, Separator, Dialog, Box, Button } from "@chakra-ui/react";
import { MyTasks } from "./MyTasks";
import { EquipeDashboard } from "./EquipeDashboard";
import { VencidasTab } from "./VencidasTab";
import { CompletasTab } from "./CompletasTab";
import { ModalNewTeam } from "./ModalNewTeam";
import { FaPlus } from "react-icons/fa";
import { useState, useEffect } from "react";
import { useAuth } from "@/context/auth/useAuth";
import { RiLogoutCircleRLine } from "react-icons/ri";
import { useTeam } from "@/context/team/useTeam";
import { useProject } from "@/context/project/useProject";

export const Sidebar = () => {
  const { user, logout } = useAuth();
  const { fetchTeam, teamData } = useTeam();
  const { project, setProject } = useProject();

  const [modalOpen, setModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<string>("minhasTasks");

  const filteredTasks =
    user?.tasks?.filter((task) => task.status !== "deleted") || [];

  useEffect(() => {
    if (activeTab && user?.teams) {
      const team = user.teams.find((e) => e.uuid === activeTab);
      if (team) fetchTeam(team.uuid);
    }
  }, [activeTab, user?.teams, fetchTeam]);

    console.log("Team Data in EquipeDashboard:", teamData);
  console.log("Team Project in EquipeDashboard:", project);

  return (
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
            <h1>Teams</h1>
            <Dialog.Root
              placement={"center"}
              open={modalOpen}
              onOpenChange={() => setModalOpen(!modalOpen)}
            >
              <Dialog.Trigger asChild>
                <Box bg="blue.500" borderRadius="md" p={0.5} border="none">
                  <FaPlus color="white" />
                </Box>
              </Dialog.Trigger>
              {modalOpen && (
                <ModalNewTeam onClose={() => setModalOpen(false)} />
              )}
            </Dialog.Root>
          </Flex>
          {user?.teams &&
            user?.teams.map((team) => {
              const teamCount = filteredTasks.filter(
                (t) => t.team_uuid === team.uuid
              ).length;
              return (
                <Tabs.Trigger
                  key={team.uuid}
                  value={team.uuid}
                  justifyContent={"space-between"}
                  onClick={() => setProject(undefined)}
                >
                  {team.name}
                  <span
                    style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}
                  >
                    {teamCount}
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
        {user?.teams &&
          user?.teams.map((team) => (
            <Tabs.Content key={team.uuid} value={team.uuid}>
              <EquipeDashboard team={team} isActive={activeTab === team.uuid} />
            </Tabs.Content>
          ))}
      </Tabs.Root>
    </Flex>
  );
};
