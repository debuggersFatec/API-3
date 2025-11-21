import { useEffect, useState } from "react";
import { Box, Spinner, Text, Center, Link as ChakraLink, Button } from "@chakra-ui/react";
import { useTheme } from "@chakra-ui/system";
import type { GoogleEvent } from "../services/googleAuthService";
import { getGoogleCalendarEvents } from "../services/googleAuthService";

export const GoogleCalendarPage = () => {
    const [events, setEvents] = useState<GoogleEvent[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [isAuthenticated, setIsAuthenticated] = useState<boolean | null>(null);

    const theme = useTheme();
    const bg = theme.colors?.white || "#fff";
    const cardBg = theme.colors?.gray?.[50] || "#f9f9f9";

    useEffect(() => {
        const checkAuth = async () => {
            try {
                const res = await fetch("/api/auth/status", { credentials: "include" });
                if (!res.ok) {
                    setIsAuthenticated(false);
                    return;
                }
                const json = await res.json();
                setIsAuthenticated(Boolean(json?.authenticated));
            } catch (err) {
                console.error("Erro ao checar autenticação", err);
                setIsAuthenticated(false);
            }
        };
        checkAuth();
    }, []);

    useEffect(() => {
        if (isAuthenticated === null) return;

        const load = async () => {
            setLoading(true);
            setError(null);
            try {
                const data = await getGoogleCalendarEvents();
                setEvents(data ?? []);
            } catch (err: unknown) {
                console.error("Erro ao buscar eventos do Google Calendar", err);
                setError("Não foi possível carregar os eventos do Google Calendar.");
            } finally {
                setLoading(false);
            }
        };

        if (isAuthenticated) {
            load();
        } else {
            setLoading(false);
            setEvents([]);
        }
    }, [isAuthenticated]);

    const handleLogin = () => {
        window.location.href = "http://localhost:8080/api/auth/google";
    };

    const handleLogout = async () => {
        try {
            await fetch("/api/auth/logout", { method: "POST", credentials: "include" });
        } catch (err) {
            console.warn("Logout falhou", err);
        } finally {
            setIsAuthenticated(false);
            setEvents([]);
        }
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
            <Text
                fontSize={{ base: "xl", md: "2xl" }}
                fontWeight="bold"
                mb={4}
                textAlign="center"
            >
                Calendário de Entregas
            </Text>

            {isAuthenticated === null ? (
                <Center w="100%" py={8}>
                    <Spinner />
                </Center>
            ) : isAuthenticated === false ? (
                <Center w="100%" py={8} flexDirection="column">
                    <Text mb={4} textAlign="center">
                        Conecte sua conta Google para ver o calendário com suas tasks.
                    </Text>

                    <Box display="flex" gap={3} mb={2}>
                        <Button colorScheme="blue" onClick={handleLogin}>
                            Entrar com Google
                        </Button>
                    </Box>

                    <Text fontSize="sm" color="gray.500" textAlign="center" maxW="640px">
                        Se você preferir abrir o Google Calendar diretamente, use o botão disponível após o login.
                    </Text>
                </Center>
            ) : (
                <>
                    <Box width="100%" flex="1 1 auto" mb={4} minH="320px" maxH="65%" display="flex" alignItems="center" justifyContent="center">
                        <Box w="100%" px={4}>
                            <Box display="flex" justifyContent="flex-end" mb={2}>
                                <Button size="sm" onClick={handleLogout} mr={2}>
                                    Sair
                                </Button>
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
                                    <Text color="gray.500">Nenhum evento encontrado no período.</Text>
                                </Center>
                            ) : (
                                <Box display="flex" flexDirection="column" gap={3}>
                                    {events.map((e, idx) => {
                                        const when = e.start?.dateTime || e.start?.date || "";
                                        const whenLabel = when ? new Date(when).toLocaleString() : "Sem data";
                                        return (
                                            <Box
                                                key={e.id || idx}
                                                bg={cardBg}
                                                border="1px solid #E2E8F0"
                                                borderRadius="md"
                                                p={3}
                                                _hover={{ shadow: "md" }}
                                            >
                                                <Text fontWeight="semibold">{e.summary || "Sem título"}</Text>
                                                <Text fontSize="sm" color="gray.500" mb={2}>
                                                    {whenLabel}
                                                </Text>
                                                {e.htmlLink && (
                                                    <ChakraLink href={e.htmlLink} target="_blank" rel="noopener noreferrer" color="blue.500" fontSize="sm">
                                                        Ver no Google Calendar
                                                    </ChakraLink>
                                                )}
                                            </Box>
                                        );
                                    })}
                                </Box>
                            )}
                        </Box>
                    </Box>
                </>
            )}
        </Box>
    );
};



