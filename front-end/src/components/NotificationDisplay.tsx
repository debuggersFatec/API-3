import {
  Badge,
  Box,
  IconButton,
  Menu,
  Portal,
  Stack,
  Text,
  Flex,
} from "@chakra-ui/react";
import { FaBell } from "react-icons/fa";
import { NotificationItem } from "./NotificationItem";
import { useAuth } from "@/context/auth/useAuth";

export const NotificationDisplay = () => {
  const { user } = useAuth();
  const unreadCount =
    user?.notificationsRecent?.filter((n) => !n.read).length ?? 0;
  return (
    <Menu.Root>
      <Menu.Trigger asChild>
        <IconButton
          variant="ghost"
          aria-label={`${unreadCount} notificações não lidas.`}
        >
          <Box position="relative" p="1">
            <FaBell width={6} height={6} />
            <Badge
              position="absolute"
              top="-4px"
              right="-4px"
              colorPalette="red"
              borderRadius="full"
              px="1.5"
              fontSize="0.7em"
            >
              {unreadCount >= 0
                ? unreadCount < 10
                  ? unreadCount
                  : "9+"
                : null}
            </Badge>
          </Box>
        </IconButton>
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content bgColor={"blue.800"}>
            <Box p={3} mb={2} borderBottomWidth={1} borderColor="gray.200">
              <Flex justify="space-between" align="center" gap={3}>
                <Text fontWeight={600} color={"white"}>
                  Notificações
                </Text>
                <Flex align="center" gap={2}>
                  <Text fontSize="sm" color="White">
                    {unreadCount} não lidas
                  </Text>
                </Flex>
              </Flex>
            </Box>
            <Stack
              gap={2}
              minW={"350px"}
              maxH="56vh"
              overflowY="auto"
              p={3}
              pt={0}
            >
              {(user?.notificationsRecent ?? []).length === 0 ? (
                <Text color="gray.500" textAlign="center" py={4}>
                  Sem notificações
                </Text>
              ) : (
                user!.notificationsRecent
                  .slice()
                  .sort((a, b) => (a.createdAt < b.createdAt ? 1 : -1))
                  .map((notification) => (
                    <NotificationItem
                      key={notification.uuid}
                      notification={notification}
                    />
                  ))
              )}
            </Stack>
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
};
