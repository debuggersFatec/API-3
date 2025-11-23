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
  const { fetchTeam } = useTeam();
  const { setProject } = useProject();

  const [modalOpen, setModalOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<string>("minhasTasks");

  const filteredTasks =
    user?.tasks?.filter((task) => task.status !== "DELETED") || [];

  useEffect(() => {
    if (activeTab && user?.teams) {
      const team = user.teams.find((e) => e.uuid === activeTab);
      if (team) fetchTeam(team.uuid);
    }
  }, [activeTab, user?.teams, fetchTeam]);

  return (
    <Flex h="100vh">
      <Tabs.Root
        value={activeTab}
        onValueChange={(details) => setActiveTab(details.value)}
        defaultValue="minhasTasks"
        variant={"plain"}
        flexDir={"row"}
        display={"flex"}
        w={"100%"}
        h="100%"
        
      >
        <Tabs.List
          pr={"16px"}
          w={"300px"}
          flexDir={"column"}
          display={"flex"}
          pl={"24px"}
          justifyContent="space-between"
          h="100%"
          position="relative"
        >
          <Flex 
            flexDir="column" 
            gap={2} 
            overflowY="auto" 
            flex="1"
            css={{
              '&::-webkit-scrollbar': {
                width: '6px',
              },
              '&::-webkit-scrollbar-track': {
                background: 'transparent',
              },
              '&::-webkit-scrollbar-thumb': {
                background: '#CBD5E0',
                borderRadius: '3px',
              },
              '&::-webkit-scrollbar-thumb:hover': {
                background: '#A0AEC0',
              },
            }}
          >
            <h1 style={{ fontSize: '14px', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase', marginTop: '16px', marginBottom: '8px' }}>Tarefas</h1>
          <Tabs.Trigger
            value="minhasTasks"
            justifyContent={"space-between"}
            onClick={() => setProject(undefined)}
            px={3}
            py={2}
            borderRadius="md"
            _hover={{ bg: "blue.50" }}
            cursor="pointer"
            transition="all 0.2s"
            fontWeight={500}
          >
            Minhas tarefas
            <span style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}>
              {filteredTasks.length}
            </span>
          </Tabs.Trigger>
          <Tabs.Trigger
            value="vencidas"
            justifyContent={"space-between"}
            onClick={() => setProject(undefined)}
            px={3}
            py={2}
            borderRadius="md"
            _hover={{ bg: "blue.50" }}
            cursor="pointer"
            transition="all 0.2s"
            fontWeight={500}
          >
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
          <Flex alignItems={"center"} justify={"space-between"} mb={2} mt={4}>
            <h1 style={{ fontSize: '14px', fontWeight: 600, color: '#6B7280', textTransform: 'uppercase' }}>Teams</h1>
            <Button
              size="xs"
              variant="solid"
              bg="#3B82F6"
              color="white"
              px={2}
              py={1}
              _hover={{ bg: "#2563EB" }}
              borderRadius="md"
              onClick={() => setModalOpen(true)}
            >
              <FaPlus size={12} />
            </Button>
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
                  px={3}
                  py={2}
                  borderRadius="md"
                  _hover={{ bg: "blue.50" }}
                  cursor="pointer"
                  transition="all 0.2s"
                  fontWeight={500}
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
          <Separator maxW={"200px"} my={2} />
          <Tabs.Trigger
            value="completas"
            justifyContent={"space-between"}
            onClick={() => setProject(undefined)}
            px={3}
            py={2}
            borderRadius="md"
            _hover={{ bg: "blue.50" }}
            cursor="pointer"
            transition="all 0.2s"
            fontWeight={500}
          >
            Completas
            <span style={{ marginLeft: 6, color: "#888", fontWeight: 500 }}>
              {filteredTasks.filter((t) => t.status === "COMPLETED").length}
            </span>
          </Tabs.Trigger>
          </Flex>
          
          <Box 
            pb={4} 
            pt={3} 
            borderTop="1px solid" 
            borderColor="border.muted"
            bg="surface.base"
            position="sticky"
            bottom={0}
          >
            <Button
              onClick={() => logout()}
              variant="ghost"
              colorPalette="red"
              justifyContent={"flex-start"}
              w="100%"
              gap={2}
            >
              <RiLogoutCircleRLine size={20} />
              Sair
            </Button>
          </Box>
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
      
      <Dialog.Root
        open={modalOpen}
        onOpenChange={(e) => setModalOpen(e.open)}
        placement="center"
        motionPreset="slide-in-bottom"
      >
        <ModalNewTeam onClose={() => setModalOpen(false)} />
      </Dialog.Root>
    </Flex>
  );
};
