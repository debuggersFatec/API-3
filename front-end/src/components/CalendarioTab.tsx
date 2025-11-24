import { useEffect, useState } from "react";
import { Box, Spinner, Text, Center, Button } from "@chakra-ui/react";
import { useTheme } from "@chakra-ui/system";
import { useAuth } from "@/context/auth/useAuth";
import { CalendarService } from "../services/calendarService";
import type { GoogleEvent } from "../types/calendar";
import { MyCalendar } from "./MyCalendar";

type CalendarioTabProps = {
  active: boolean;
};

export const CalendarioTab = ({ active }: CalendarioTabProps) => {
  const [events, setEvents] = useState<GoogleEvent[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const { user, refreshUser } = useAuth();

  const connected = Boolean(user?.googleCalendar?.connected);
  const theme = useTheme();
  const bg = theme.colors?.white || "#fff";

  useEffect(() => {
    if (active) {
        const loadEvents = async () => {
        if (!connected || !user?.uuid) return;
        setLoading(true);
        setError(null);
        try {
          const res = await CalendarService.listEvents(user.uuid);
          setEvents(res ?? []);
        } catch (err) {
          console.error("Erro ao buscar eventos do Google Calendar", err);
          setError("Não foi possível carregar os eventos do Google Calendar.");
        } finally {
          setLoading(false);
        }
      };
      loadEvents();
    }
  }, [active]);

  const handleSyncGoogle = () => {
    if (!user?.uuid) return;
    CalendarService.syncGoogle(user.uuid);
    refreshUser();
  };

  const openGoogleCalendar = () => {
    window.open("https://calendar.google.com/calendar/r", "_blank", "noopener");
  };

  return (
    <Box
      w="100%"
      h="calc(100vh - 90px)"
      display="flex"
      flexDirection="column"
      alignItems="center"
      justifyContent="flex-start"
      background={bg}
      px={{ base: 2, md: 6 }}
      py={{ base: 4, md: 6 }}
      overflow="hidden"
    >
      <Text fontSize={{ base: "xl", md: "2xl" }} fontWeight="bold" mb={4} textAlign="center">
        Calendário de Entregas
      </Text>

      {!user ? (
        <Center w="100%" py={8}>
          <Spinner />
        </Center>
      ) : !connected ? (
        <Center w="100%" py={8} flexDirection="column">
          <Text mb={4} textAlign="center">
            Conecte seu Google Calendar para sincronizar suas atividades.
          </Text>
          <Button colorScheme="blue" onClick={handleSyncGoogle}>
            Sincronizar Google Calendar
          </Button>
        </Center>
      ) : (
        <>
          <Box w="100%" flex="1 1 auto" mb={4} minH="320px">
            <Box display="flex" justifyContent="flex-end" mb={2}>
              <Button size="sm" onClick={openGoogleCalendar}>
                Abrir Google Calendar
              </Button>
            </Box>

            {loading ? (
              <Center py={12}>
                <Spinner />
              </Center>
            ) : error ? (
              <Text color="red.500" textAlign="center" py={6}>
                {error}
              </Text>
            ) : events.length === 0 ? (
              <Center py={12}>
                <Text color="gray.500">Nenhum evento encontrado.</Text>
              </Center>
            ) : (
              <MyCalendar events={events} />
            )}
          </Box>
        </>
      )}
    </Box>
  );
};