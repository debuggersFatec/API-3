import { Box, Button, Flex, Input, Stack, Text, Link, Image } from "@chakra-ui/react";
import { Alert, AlertIcon } from "@chakra-ui/alert";
import { useState } from "react";
import logoSrc from "../assets/logotipo.svg";
import googleSrc from "../assets/google.svg";
import tileSrc from "../assets/login-lateral.svg";

export default function Register() {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [error, setError] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    // Validação simples
    if (!name || !email || !password || !confirmPassword) {
      setError("Preencha todos os campos.");
      return;
    }
    if (password !== confirmPassword) {
      setError("As senhas não conferem.");
      return;
    }

    // Limpar erro
    setError("");

    console.log({ name, email, password }); // Aqui depois vai para o backend
  };

  return (
    <Flex w="100vw" minH="100vh" direction={{ base: 'column', md: 'row' }} align="stretch" bg="gray.900">
      <Flex w={{ base: '100%', md: '50%' }} minH="100vh" align="center" justify="center" bg="white">
        <Box maxW="480px" w="full" p={10} textAlign="center">
          <Image src={logoSrc} alt="Logo" mx="auto" mb={4} maxW="200px" maxH="88px" w="auto" h="auto" objectFit="contain" />

          <Text fontSize="2xl" fontWeight="bold" mb={1} color="gray.800">Bem vindo ao FASTASK</Text>

          <Text fontSize="sm" color="gray.600" mb={6}>
            Já tem uma conta? {' '}
            <Link color="blue.500" href="/login">Faça login</Link>
          </Text>

          {error && (
            <Alert status="error" borderRadius="md" mb={4}>
              <AlertIcon />
              {error}
            </Alert>
          )}

          <form onSubmit={handleSubmit}>
            <Stack gap={4}>
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
                onChange={(e) => setPassword(e.target.value)}
                borderColor="gray.200"
                borderWidth="1px"
                borderRadius="8px"
                color="black"
                _placeholder={{ color: 'black' }}
                boxShadow="0 6px 18px rgba(13,26,54,0.06)"
              />

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
