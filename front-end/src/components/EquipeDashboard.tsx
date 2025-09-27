import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { EquipeTabs } from "./EquipeTabs";
import { useEffect, useState } from "react";
import axios from "axios";
import type { EquipeData } from "@/types/equipe";
import { useAuth } from "@/context/useAuth";

export type Equipe = {
  uuid: string;
  name: string;
};

export const EquipeDashboard = ({
  equipe,
  isActive,
}: {
  equipe: Equipe;
  isActive: boolean;
}) => {
  const [name, setName] = useState(equipe.name);
  const [equipeData, setEquipeData] = useState<EquipeData | undefined>();
  const [isLoading, setIsLoading] = useState(false);
  const { token } = useAuth();

  useEffect(() => {
    const fetchEquipe = async () => {
      setIsLoading(true);
      try {
        const response = await axios.get(
          `http://localhost:8080/api/equipes/${equipe.uuid}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        console.log("Equipe data fetched:", response.data );
        setName(response.data.name);
        setEquipeData(response.data);
      } catch (error) {
        console.error("Erro ao buscar equipe:", error);
        setName(equipe.name);
      }
    };
    if (isActive) {
      fetchEquipe();
    }
    setIsLoading(false);
  }, [isActive, equipe.uuid, token, equipe.name]);

  if (isLoading) {
    return <div>Carregando...</div>;
  }

  if (equipeData) {
    return (
      <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
        <SectionHeader title={name} equipe={equipeData} isTeamSection={true} />
        <EquipeTabs equipeData={equipeData} />
      </Box>
    );
  }
};
