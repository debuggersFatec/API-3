import {
  Box,
  Button,
  Flex,
  Input,
  Stack,
  Text,
  Image,
  Link,
  Heading,
} from "@chakra-ui/react";
import { Alert, AlertIcon } from "@chakra-ui/alert";
import { useState } from "react";
import logoSrc from "../assets/logotipo.svg";
import tileSrc from "../assets/login-lateral.svg";
import { authService } from "@/services/authService";
import { Link as RouterLink } from "react-router-dom";

export const ForgotPassword = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [email, setEmail] = useState("");
  const [loading, setLoading] = useState(false);

  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setSuccessMessage(null);
    setLoading(true);

    if (!email) {
      setErrorMessage("Por favor, insira seu e-mail.");
      setLoading(false);
      return;
    }

    authService
      .forgotPassword({ email })
      .then((response) => {
        setSuccessMessage(response.data.message || "E-mail enviado com sucesso!");
      })
      .catch((error) => {
        console.error("Erro ao solicitar redefinição:", error);
        // Mesmo em erro, mostramos sucesso para não vazar dados
        setSuccessMessage("Se o e-mail estiver cadastrado, um link de redefinição será enviado.");
      })
      .finally(() => {
        setLoading(false);
      });
  };

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

          <Heading size="lg" fontWeight="bold" mb={2} color="gray.800">
            Recuperar Senha
          </Heading>

          <Text fontSize="sm" color="gray.600" mb={6}>
            Digite seu e-mail e enviaremos um link para você
            resetar sua senha.
          </Text>

          <form onSubmit={onSubmit}>
            <Stack gap={4}>
              {errorMessage && (
                <Alert status="error" borderRadius="md" justifyContent="center">
                  <AlertIcon />
                  {errorMessage}
                </Alert>
              )}
              {successMessage && (
                <Alert status="success" borderRadius="md" justifyContent="center">
                  <AlertIcon />
                  {successMessage}
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
                disabled={!!successMessage} 
              />

              <Button
                type="submit"
                w="full"
                bg="#2F80ED"
                color="white"
                _hover={{ bg: "#1E6FD8" }}
                _active={{ bg: "#155bb5" }}
                borderRadius="8px"
                loading={loading}
                disabled={!!successMessage}
              >
                Enviar link
              </Button>
            </Stack>
          </form>

          <Box my={6} display="flex" alignItems="center" justifyContent="center">
             <RouterLink to="/login">
                <Link color="blue.500">
                    Voltar para o Login
                </Link>
             </RouterLink>
          </Box>
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

export default ForgotPassword;