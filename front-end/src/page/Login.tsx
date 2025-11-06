import {
  Box,
  Button,
  Flex,
  Input,
  Stack,
  Text,
  Image,
  Link,
} from "@chakra-ui/react";
import { Link as RouterLink } from "react-router-dom";
import { Alert, AlertIcon } from "@chakra-ui/alert";
import { useEffect, useState } from "react";
import logoSrc from "../assets/logotipo.svg";
import googleSrc from "../assets/google.svg";
import tileSrc from "../assets/login-lateral.svg";
import { authService } from "@/services/authService";
import { useAuth } from "@/context/auth/useAuth";
import { useNavigate, useLocation } from "react-router-dom"; // Import useLocation

export const Login = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const { setUser, setToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation(); // Get current location to read query params
  
  // Read redirect path from query parameter, default to dashboard
  const redirectPath = new URLSearchParams(location.search).get("redirect") || "/";

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    setErrorMessage("");

    if (!email || !password) {
      setErrorMessage("Preencha email e senha.");
      return;
    }

    authService
      .login({ email, password })
      .then((response) => {
        setToken(response.data.token);
        setUser(response.data.user);
        // If there is an explicit redirect, use it. Otherwise, prefer a pending invite token
        // saved in localStorage (fallback from JoinTeamPage).
        if (redirectPath && redirectPath !== "/") {
          navigate(decodeURIComponent(redirectPath));
          return;
        }

        const pending = localStorage.getItem('pendingInviteToken');
        if (pending) {
          // remove it and navigate to join-team with token
          localStorage.removeItem('pendingInviteToken');
          navigate(`/join-team?token=${encodeURIComponent(pending)}`);
          return;
        }

        // Default
        navigate(decodeURIComponent(redirectPath));
      })
      .catch((error) => {
        console.error("Erro no login:", error);
        setErrorMessage("Credenciais inválidas.");
      });
  };

  useEffect(() => {
    const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as string | undefined;
    if (!clientId) {
      setErrorMessage("VITE_GOOGLE_CLIENT_ID não configurado no frontend.");
      return;
    }
    if (!window.google?.accounts?.id) {
      // aguarda script carregar
      const t = setTimeout(() => setErrorMessage("Google Identity Services não carregou."), 1000);
      return () => clearTimeout(t);
    }

    try {
      if (window.google.accounts.id.setLogLevel) {
        window.google.accounts.id.setLogLevel('debug' as any);
      }

      window.google.accounts.id.initialize({
        client_id: clientId,
        callback: ({ credential }: { credential: string }) => {
          if (!credential) {
            setErrorMessage("Credencial do Google ausente.");
            return;
          }
          authService
            .googleLogin({ idToken: credential })
            .then((response) => {
              setToken(response.data.token);
              setUser(response.data.user);
              navigate(decodeURIComponent(redirectPath || "/"));
            })
            .catch((err) => {
              console.error("Erro no login Google:", err);
              const msg = err?.response?.data?.message || "Falha no login com Google.";
              setErrorMessage(msg);
            });
        },
        ux_mode: "popup",
      });

      const el = document.getElementById('gsi-button-container');
      if (el) {
        window.google.accounts.id.renderButton(el, {
          theme: 'outline',
          size: 'large',
          type: 'standard',
          text: 'signin_with',
          shape: 'rectangular',
        } as any);
      }

      // Se desejar ainda abrir One Tap, descomente abaixo
      // window.google.accounts.id.prompt((notification: any) => {
      //   try {
      //     if (notification?.isNotDisplayed?.()) {
      //       const reason = notification.getNotDisplayedReason?.();
      //       if (reason) setErrorMessage(`Google Sign-In não exibido: ${reason}`);
      //     }
      //     if (notification?.isSkippedMoment?.()) {
      //       const reason = notification.getSkippedReason?.();
      //       if (reason) setErrorMessage(`Google Sign-In ignorado: ${reason}`);
      //     }
      //   } catch {}
      // });
    } catch (e) {
      console.error(e);
      setErrorMessage("Falha ao iniciar Google Identity Services.");
    }
  }, [navigate, redirectPath, setToken, setUser]);

  return (
    <Flex
      w="100vw"
      minH="100vh"
      direction={{ base: "column", md: "row" }}
      align="stretch"
      bg="gray.900"
    >
      <Flex
        w={{ base: "100%", md: "50%" }}
        minH="100vh"
        align="center"
        justify="center"
        bg="white"
      >
        <Box
          maxW="480px"
          w="full"
          p={10}
          textAlign="center"
          backgroundImage={`url(${tileSrc})`}
          backgroundRepeat="no-repeat"
          backgroundPosition="left center"
          backgroundSize="120px"
        >
          <Image
            src={logoSrc}
            alt="Logo"
            mx="auto"
            mb={4}
            maxW="200px"
            maxH="88px"
            w="auto"
            h="auto"
            objectFit="contain"
          />

          <Text fontSize="2xl" fontWeight="bold" mb={1} color="gray.800">
            Bem vindo ao FASTASK
          </Text>

          <Text fontSize="sm" color="gray.600" mb={6}>
            Não tem uma conta?

            {/* Passa o redirect path para o registro */}
            <Link color="blue.500" href={`/register?redirect=${encodeURIComponent(redirectPath)}`}>
              Registre-se aqui
            </Link>
          </Text>

          <form onSubmit={onSubmit}>
            <Stack gap={4}>
              {errorMessage && (
                <Alert
                  status="error"
                  borderRadius="md"
                  fontWeight="500"
                  justifyContent="center"
                  gap="2"
                  mb={4}
                >
                  <Flex alignItems="center">
                    <AlertIcon color="red" w="20" h="20" mr="5" minW={"20"} />
                    <Text color="red.500" fontSize="sm">
                      {errorMessage}
                    </Text>
                  </Flex>
                </Alert>
              )}

              <Input
                type="email"
                placeholder="Indique seu email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: "black" }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              <Input
                type="password"
                placeholder="Coloque sua senha"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: "black" }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              <Flex justify="flex-end">
                <RouterLink to="/forgot-password">
                  <Text as="span" fontSize="sm" color="blue.500">Esqueceu a senha?</Text>
                </RouterLink>
              </Flex>

              <Button
                type="submit"
                w="full"
                bg="#2F80ED"
                color="white"
                _hover={{ bg: "#1E6FD8" }}
                _active={{ bg: "#155bb5" }}
                borderRadius="8px"
              >
                Entrar
              </Button>
            </Stack>
          </form>

          <Box my={6} display="flex" alignItems="center">
            <Box flex={1} height="1px" bg="gray.200" />
            <Text px={3} color="gray.400" fontSize="sm">
              ou
            </Text>
            <Box flex={1} height="1px" bg="gray.200" />
          </Box>

          {/* Único botão de login Google (oficial), renderizado pelo GIS */}
          <Box id="gsi-button-container" mt={3} display="flex" justifyContent="center" />
        </Box>
      </Flex>

      <Flex
        w={{ base: "100%", md: "50%" }}
        minH="100vh"
        bg="#0D1A36"
        align="center"
        justify="center"
        p={0}
      >
        <Image
          src={tileSrc}
          alt="login lateral"
          objectFit="contain"
          w="100%"
          h="100vh"
        />
      </Flex>
    </Flex>
  );
};