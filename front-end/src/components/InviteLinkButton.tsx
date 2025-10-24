import { Button, Input, HStack, Text, Box, VStack } from "@chakra-ui/react";
import { FaLink } from "react-icons/fa";
import type { AxiosError } from "axios";
import { useAuth } from "@/context/auth/useAuth";
import { toaster } from "@/components/ui/toaster";
import { useEffect, useState } from "react";
import axios from "axios";

interface InviteLinkButtonProps {
  teamUuid: string;
}

// Assumindo a URL base do frontend (de acordo com a configuração de CORS em WebConfig.java)
const FRONTEND_BASE_URL = "http://localhost:5173";

export const InviteLinkButton = ({ teamUuid }: InviteLinkButtonProps) => {
  const { token: authToken } = useAuth();
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const onOpen = () => setIsOpen(true);
  const onClose = () => setIsOpen(false);
  // Removido: const [email, setEmail] = useState("");

  // timer id to clear on unmount
  useEffect(() => {
    let t: number | undefined;
    if (copied) {
      t = window.setTimeout(() => setCopied(false), 3000);
    }
    return () => {
      if (t) window.clearTimeout(t);
    };
  }, [copied]);
  
  const handleGenerateLink = async () => {
    if (!authToken) {
      toaster.error({ title: "Erro de autenticação", description: "Faça login novamente." });
      return;
    }
    setLoading(true);
    
    try {
      // 1. Chamar o backend para gerar o token JWT (endpoint em TeamsController.java)
      const response = await axios.post(
        `http://localhost:8080/api/teams/${teamUuid}/invite`, 
        {}, 
        {
          headers: { Authorization: `Bearer ${authToken}` },
        }
      );

      // O backend retorna {"token": "..."}
      const data = response.data as { token: string };
      const inviteToken = data.token; 

      if (!inviteToken) {
        toaster.error({ title: "Erro", description: "Token não gerado pelo servidor." });
        return;
      }
      
      // 2. Construir o link de convite que aponta para a rota do frontend
      // Simplificado para link genérico.
      const inviteLink = `${FRONTEND_BASE_URL}/join-team?token=${inviteToken}`;
      
      // 3. Copiar para a área de transferência (navigator.clipboard com fallback)
      try {
        await navigator.clipboard.writeText(inviteLink);
      } catch {
        // Fallback para navegadores antigos
        const textarea = document.createElement("textarea");
        textarea.value = inviteLink;
        // Avoid scrolling to bottom
        textarea.style.position = "fixed";
        textarea.style.top = "0";
        textarea.style.left = "0";
        textarea.style.width = "1px";
        textarea.style.height = "1px";
        textarea.style.padding = "0";
        textarea.style.border = "none";
        textarea.style.outline = "none";
        textarea.style.boxShadow = "none";
        document.body.appendChild(textarea);
        textarea.select();
        try {
          document.execCommand("copy");
        } catch (e) {
          console.warn("Fallback: unable to copy", e);
        }
        document.body.removeChild(textarea);
      }

      // show success toast and a temporary button state
      setCopied(true);
      // fechar modal caso esteja aberto
      onClose();
      toaster.success({
        title: "Link de Convite Gerado!",
        description: "O link foi copiado para a sua área de transferência. Compartilhe!",
        duration: 5000,
      });

    } catch (error) {
      console.error("Erro ao gerar link de convite:", error);
      const err = error as AxiosError;
      let errorMessage = "Erro ao gerar o link de convite.";

      if (err.response?.status === 403) {
          errorMessage = "Acesso negado. Você não tem permissão para convidar.";
      } else if (err.response?.status === 404) {
          errorMessage = "Equipe não encontrada.";
      }
      
      toaster.error({ title: "Falha", description: errorMessage });
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Button onClick={onOpen} loading={loading} size="sm" variant="outline">
        <FaLink style={{ marginRight: 8 }} />
        {copied ? "Link copiado!" : "Convidar Membro"}
      </Button>

      {isOpen && (
        <Box position="fixed" inset={0} bg="rgba(0,0,0,0.45)" zIndex={60} display="flex" alignItems="center" justifyContent="center">
          <Box bg="white" borderRadius="md" p={6} minW={{ base: "90%", md: "420px" }} boxShadow="lg">
            <VStack align="stretch" gap={4}>
              <HStack justify="space-between">
                <Text fontSize="lg" fontWeight="600">Convidar membro</Text>
                <Button variant="ghost" onClick={onClose}>Fechar</Button>
              </HStack>

              <Text color="gray.600">Copie e compartilhe o link de convite com o membro que deseja adicionar à equipe.</Text>
              
              <Box p={3} bg="gray.50" borderRadius="md" borderWidth="1px" borderColor="gray.200">
                <Text fontSize="sm" color="gray.700">Link de Convite Genérico (após clicar no botão)</Text>
              </Box>

              <HStack justify="flex-end" gap={2}>
                <Button variant="ghost" onClick={onClose}>Cancelar</Button>
                <Button colorScheme="blue" onClick={handleGenerateLink} loading={loading}>Gerar e Copiar Link</Button>
              </HStack>
            </VStack>
          </Box>
        </Box>
      )}
    </>
  );
};