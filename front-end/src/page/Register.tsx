import { Box, Button, Flex, Input, Stack, Text, Link, Image } from "@chakra-ui/react";
import { Alert, AlertIcon } from "@chakra-ui/alert";
import { useState } from "react";
import logoSrc from "../assets/logotipo.svg";
import googleSrc from "../assets/google.svg";
import tileSrc from "../assets/login-lateral.svg";
import axios from "axios";
import { useNavigate, useLocation } from "react-router-dom"; // Import useLocation
import { useAuth } from "@/context/auth/useAuth";

const PasswordStrengthMeter = ({ value }: { value: number }) => {
  const color = value < 3 ? "red.500" : value < 5 ? "yellow.500" : "green.500";
  const text = value === 0 ? "" : value < 3 ? "Fraca" : value < 5 ? "Boa" : "Forte";

  return (
    <Box mt={2}>
      <Flex w="full" bg="gray.200" borderRadius="md" h="4px">
        <Box w={`${value * 20}%`} bg={color} borderRadius="md" transition="width 0.3s" />
      </Flex>
      <Text fontSize="sm" mt={1} color={color}>{text}</Text>
    </Box>
  );
};

const existingEmails = ["teste@teste.com", "admin@fastask.com"];

const calculatePasswordStrength = (password: string) => {
  let score = 0;
  if (!password) return 0;
  if (password.length >= 8) score++;
  if (/[a-z]/.test(password)) score++;
  if (/[A-Z]/.test(password)) score++;
  if (/\d/.test(password)) score++;
  if (/[^a-zA-Z\d]/.test(password)) score++;
  return score;
};

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState<string>("");
  // const [loading, setLoading] = useState(false);
  const [passwordStrength, setPasswordStrength] = useState(0);
  const { setUser, setToken } = useAuth();
  const navigate = useNavigate();
  const location = useLocation(); // Get current location to read query params

  const strongPasswordRegex =
    /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  // Read redirect path from query parameter, default to dashboard
  const redirectPath = new URLSearchParams(location.search).get("redirect") || "/";

  // The original component had logic to pre-fill email from query param, I'll keep it simple for now, as the user only asked for the link logic.
  // I will read the initial email value from the 'email' query parameter if it exists.
  const initialEmail = new URLSearchParams(location.search).get('email') || '';
  useState(() => {
    if (initialEmail) setEmail(initialEmail);
  });
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");

    if (!name || !email || !password || !confirmPassword) {
      setError("Preencha todos os campos.");
      return;
    }
    if (!emailRegex.test(email)) {
      setError("Digite um email válido.");
      return;
    }
    if (existingEmails.includes(email.toLowerCase())) {
      setError("Este email já está em uso.");
      return;
    }
    if (!strongPasswordRegex.test(password)) {
      setError(
        "A senha deve ter no mínimo 8 caracteres, incluindo letra maiúscula, minúscula, número e símbolo."
      );
      return;
    }
    if (password !== confirmPassword) {
      setError("As senhas não conferem.");
      return;
    }

    try {
      // setLoading(true);

      axios
        .post("http://localhost:8080/api/auth/register", {
          email: email,
          password: password,
          name: name,
        })
        .then((response) => {
          setToken(response.data.token);
          setUser(response.data.user);
          // If there is an explicit redirect, use it. Otherwise use any pending invite token.
          if (redirectPath && redirectPath !== "/") {
            navigate(decodeURIComponent(redirectPath));
            return;
          }

          const pending = localStorage.getItem('pendingInviteToken');
          if (pending) {
            localStorage.removeItem('pendingInviteToken');
            navigate(`/join-team?token=${encodeURIComponent(pending)}`);
            return;
          }

          // Default
          navigate(decodeURIComponent(redirectPath));
        })
        .catch((error) => {
          setError(
            error?.response?.data?.message ||
              error.message ||
              "Erro ao registrar."
          );
          console.log(error);
        });
      // setLoading(false);
    } catch (err) {
      setError("Erro ao verificar email. Tente novamente.");
      console.log(err);
      // setLoading(false);
    }
  };
  return (
    <Flex w="100vw" minH="100vh" direction={{ base: 'column', md: 'row' }} align="stretch" bg="gray.900">
      <Flex w={{ base: '100%', md: '50%' }} minH="100vh" align="center" justify="center" bg="white">
        <Box maxW="480px" w="full" p={10} textAlign="center">
          <Image src={logoSrc} alt="Logo" mx="auto" mb={4} maxW="200px" maxH="88px" w="auto" h="auto" objectFit="contain" />

          <Text fontSize="2xl" fontWeight="bold" mb={1} color="gray.800">Bem vindo ao FASTASK</Text>

          <Text fontSize="sm" color="gray.600" mb={6}>
            Já tem uma conta?
            {/* Passa o redirect path para o login */}
            <Link color="blue.500" href={`/login?redirect=${encodeURIComponent(redirectPath)}`}>Faça login</Link>
          </Text>

          <form onSubmit={handleSubmit}>
            <Stack gap={4}>
              {error && (
                <Alert
                  status="error"
                  borderRadius="md"
                  fontWeight="500"
                  justifyContent="center"
                  gap="2"
                  mb={4}
                >
                  <Flex alignItems="center">
                    <AlertIcon color="red" w="20" h="20" mr="5" minW={'20'}/>
                    <Text color="red.500" fontSize="sm">{error}</Text>
                  </Flex>
                </Alert>
              )}

              <Input
                placeholder="Coloque seu nome"
                value={name}
                onChange={(e) => setName(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: 'black' }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              <Input
                type="email"
                placeholder="Indique seu email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: 'black' }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              <Input
                type="password"
                placeholder="Coloque sua senha"
                value={password}
                onChange={(e) => {
                  setPassword(e.target.value);
                  setPasswordStrength(calculatePasswordStrength(e.target.value));
                }}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: 'black' }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              {password.length > 0 && (
                <PasswordStrengthMeter value={passwordStrength} />
              )}

              <Input
                type="password"
                placeholder="Repita sua senha"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: 'black' }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

              <Button type="submit" w="full" bg="#2F80ED" color="white" _hover={{ bg: '#1E6FD8' }} _active={{ bg: '#155bb5' }} borderRadius="8px">
                Registrar
              </Button>
            </Stack>
          </form>

          <Box my={6} display="flex" alignItems="center">
            <Box flex={1} height="1px" bg="gray.200" />
            <Text px={3} color="gray.400" fontSize="sm">ou</Text>
            <Box flex={1} height="1px" bg="gray.200" />
          </Box>

          <Button w="full" bg="white" color="gray.800" borderRadius="8px" borderWidth="1px" borderColor="gray.200" _hover={{ bg: '#f6f6f6' }} fontFamily="Inter, sans-serif" fontWeight="400" fontStyle="normal" fontSize="14px" lineHeight="100%" letterSpacing="0" boxShadow="0 6px 18px rgba(13,26,54,0.06)"><Image src={googleSrc} alt="Google" boxSize="18px" mr={2} />Continue com Google</Button>
        </Box>
      </Flex>


      <Flex w={{ base: '100%', md: '50%' }} minH="100vh" bg="#0D1A36" align="center" justify="center" p={0}>
        <Image src={tileSrc} alt="register lateral" objectFit="contain" w="100%" h="100vh" />
      </Flex>
    </Flex>
  );
}