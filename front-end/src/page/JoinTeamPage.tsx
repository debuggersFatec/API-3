import { useEffect, useState } from "react";
import { useParams, useNavigate, useLocation } from "react-router-dom";
import { Box, Text, Center, Spinner, VStack, Button, Heading, HStack } from "@chakra-ui/react";
import { Alert, AlertIcon } from "@chakra-ui/alert";
import { useAuth } from "@/context/auth/useAuth";
import { teamServices } from "@/services/teamServices";
import { AxiosError } from "axios";

export const JoinTeamPage = () => {
  const { token: userToken, refreshUser } = useAuth();
  const { token: urlToken } = useParams<{ token: string }>();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [teamName, setTeamName] = useState<string | null>(null);

  // Fallback to query parameter if path variable is not used
  const queryToken = new URLSearchParams(location.search).get("token");
  const finalToken = urlToken || queryToken;

  useEffect(() => {
    if (!finalToken) {
      setError("Token de convite não encontrado na URL.");
      setLoading(false);
      return;
    }

    if (!userToken) {
      // If there's no logged user, stop loading and wait for user interaction (login/register).
      // Persist the invite token as a fallback so the flow still works if redirect params
      // are lost during authentication (e.g., some OAuth providers).
      try {
        localStorage.setItem('pendingInviteToken', finalToken);
      } catch (e) {
        console.warn('Falha ao salvar pendingInviteToken:', e);
      }
      setLoading(false);
      return;
    }

    // --- User is authenticated: proceed to join the team ---
    const handleJoin = async () => {
      if (!userToken) return;

      try {
        const team = await teamServices.joinTeamWithInvite(finalToken, userToken);
        setTeamName(team.name);
        // Refresh user data to update their team list in the sidebar
        await refreshUser();
        
        // Success: Redirect to dashboard
        setTimeout(() => {
          navigate("/", { replace: true });
        }, 3000);

      } catch (err) {
        setLoading(false);
        const axiosError = err as AxiosError;
        let errorMessage = "Erro desconhecido ao entrar na equipe.";
        
        if (axiosError.response) {
            // Use the custom Error-Message header for more precise errors
            errorMessage = axiosError.response.headers['error-message'] || errorMessage;

            if (axiosError.response.status === 401) {
                errorMessage = errorMessage || "Sessão expirada ou token de convite inválido.";
            } else if (axiosError.response.status === 404) {
                errorMessage = errorMessage || "Equipe não encontrada ou token inválido.";
            } else if (axiosError.response.status === 400) {
                errorMessage = errorMessage || "Token inválido ou malformado.";
            }
        } else if (axiosError.request) {
            errorMessage = "Erro de conexão com o servidor. Tente novamente.";
        } else {
            errorMessage = "Erro interno ao processar a requisição.";
        }
        
        setError(errorMessage);
      } finally {
        setLoading(false);
      }
    };

    handleJoin();
  }, [finalToken, userToken, navigate, refreshUser]);

  // If user is not logged in, show options to Login or Register
  if (!loading && !userToken) {
    // A rota atual com o token deve ser o alvo do redirecionamento após o login/cadastro
    const redirectPath = `/join-team?token=${finalToken}`;

    return (
      <Center w="100vw" h="100vh" bg="gray.50">
        <VStack gap={4} p={8} bg="white" boxShadow="xl" borderRadius="lg" w="md">
          <Heading size="xl" color="blue.500">Convite para Equipe</Heading>
          <Text fontSize="md" color="gray.600" textAlign="center">
            Você precisa estar logado para aceitar o convite.
          </Text>
          
          <Text>Faça login ou crie uma conta para entrar na equipe automaticamente.</Text>
          
          <HStack gap={3} justify="center">
            {/* Redirect to login, passing the current path with token as the redirect target */}
            <Button 
              onClick={() => navigate(`/login?redirect=${encodeURIComponent(redirectPath)}`)} 
              colorScheme="blue"
            >
              Entrar
            </Button>
            {/* Redirect to register, passing the current path with token as the redirect target */}
            <Button 
              onClick={() => navigate(`/register?redirect=${encodeURIComponent(redirectPath)}`)} 
              variant="outline"
            >
              Criar Conta
            </Button>
          </HStack>
        </VStack>
      </Center>
    );
  }

  // If the component is still loading (i.e., we have a token and are trying to join)
  if (loading) {
    return (
      <Center w="100vw" h="100vh" bg="gray.50">
        <VStack gap={4} p={8} bg="white" boxShadow="xl" borderRadius="lg" w="md">
          <Heading size="lg" color="blue.500">Convite para Equipe</Heading>
          <Text fontSize="md" color="gray.600" textAlign="center">
            Processando sua entrada na equipe...
          </Text>
          <Spinner size="xl" color="blue.500" />
        </VStack>
      </Center>
    );
  }

  // If we reach here, we are not loading and either finished with success or error (while logged in)

  return (
    <Center w="100vw" h="100vh" bg="gray.50">
      <VStack gap={4} p={8} bg="white" boxShadow="xl" borderRadius="lg" w="md">
        <Heading size="lg" color="blue.500">Convite para Equipe</Heading>
        
        {error && (
          <Alert status="error" borderRadius="md">
            <AlertIcon />
            <Text>{error}</Text>
          </Alert>
        )}

        {teamName && (
          <Box textAlign="center">
            <Alert status="success" borderRadius="md">
              <AlertIcon />
              <Text>Bem-vindo(a) à equipe: <Text as="span" fontWeight="bold">{teamName}</Text>!</Text>
            </Alert>
            <Text mt={4} color="gray.500">Redirecionando para o Dashboard...</Text>
          </Box>
        )}
        
        {!error && !teamName && (
             <Alert status="info" borderRadius="md">
                <AlertIcon />
                <Text>Token processado, mas não recebemos o nome da equipe. Redirecionando...</Text>
            </Alert>
        )}
        
        <Button onClick={() => navigate("/")} colorScheme="gray" variant="outline">
            Ir para Dashboard
        </Button>

      </VStack>
    </Center>
  );
};