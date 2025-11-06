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
import { useState, useEffect } from "react";
import logoSrc from "../assets/logotipo.svg";
import tileSrc from "../assets/login-lateral.svg";
import { authService } from "@/services/authService";
import { Link as RouterLink, useParams, useNavigate } from "react-router-dom";

export const ResetPassword = () => {
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);
  const [email, setEmail] = useState<string | null>(null);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(true); 
  const [isTokenValid, setIsTokenValid] = useState(false);

  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();

  // Validar o token assim que a página carregar
  useEffect(() => {
    if (!token) {
      setErrorMessage("Token não fornecido.");
      setLoading(false);
      return;
    }

    authService.validateResetToken(token)
      .then(response => {
        setEmail(response.data.email || "usuário"); 
        setIsTokenValid(true);
      })
      .catch(error => {
        setErrorMessage(error.response?.data?.message || "Link inválido ou expirado.");
      })
      .finally(() => {
        setLoading(false);
      });
  }, [token]);

  // 2. Enviar a nova senha
  const onSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMessage(null);
    setSuccessMessage(null);

    if (!password || !confirmPassword) {
      setErrorMessage("Por favor, preencha as senhas.");
      return;
    }
    if (password !== confirmPassword) {
      setErrorMessage("As senhas não conferem.");
      return;
    }
    // Adicionar validação de força da senha (opcional, mas recomendado)
    if (password.length < 8) {
         setErrorMessage("A senha deve ter no mínimo 8 caracteres.");
         return;
    }

    if (!token) {
        setErrorMessage("Token não encontrado.");
        return;
    }

    setLoading(true);

    authService
      .resetPassword(token, { novaSenha: password })
      .then((response) => {
        setSuccessMessage(response.data.message || "Senha alterada com sucesso!");
        // Redireciona para o login após 3 segundos
        setTimeout(() => {
            navigate("/login");
        }, 3000);
      })
      .catch((error) => {
        setErrorMessage(error.response?.data?.message || "Erro ao resetar a senha.");
      })
      .finally(() => {
        setLoading(false);
      });
  };

  const renderContent = () => {
    if (loading) {
      return <Text color="gray.600">Validando link...</Text>;
    }

    if (!isTokenValid) {
        return (
            <Stack gap={4}>
                 <Alert status="error" borderRadius="md" justifyContent="center">
                    <AlertIcon />
                    {errorMessage || "Link inválido ou expirado."}
                </Alert>
                <RouterLink to="/login">
                    <Link color="blue.500" w="full" textAlign="center" display="block">
                        Voltar para o Login
                    </Link>
                </RouterLink>
            </Stack>
        );
    }

    // Token é válido, mostrar formulário
    return (
        <>
            <Text fontSize="sm" color="gray.600" mb={6}>
                Redefinindo senha para: <strong>{email}</strong>
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
                    type="password"
                    placeholder="Digite sua nova senha"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    borderColor="gray.200"
                    borderWidth="1px"
                    borderRadius="8px"
                    color="black"
                    _placeholder={{ color: "black" }}
                    boxShadow="0 6px 18px rgba(13,26,54,0.06)"
                    disabled={!!successMessage}
                />
                <Input
                    type="password"
                    placeholder="Confirme sua nova senha"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
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
                    Redefinir Senha
                </Button>
                </Stack>
            </form>
        </>
    );
  }

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
            Criar Nova Senha
          </Heading>

          {renderContent()}

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

export default ResetPassword;