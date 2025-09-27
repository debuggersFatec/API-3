import { Box } from "@chakra-ui/react";
import { SectionHeader } from "./SectionHeader";
import { EquipeTabs } from "./EquipeTabs";
import { useEffect, useState, useCallback } from "react";
import axios from "axios";
import type { EquipeData } from "@/types/equipe";
import { EquipeContext } from "@/context/EquipeContext";
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

  const fetchEquipe = useCallback(async () => {
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
      setName(response.data.name);
      setEquipeData(response.data);
    } catch {
      setName(equipe.name);
    }
    setIsLoading(false);
  }, [equipe.uuid, token, equipe.name]);

  useEffect(() => {
    if (isActive) {
      fetchEquipe();
    }
  }, [isActive, fetchEquipe]);

  return (
    <EquipeContext.Provider value={{ equipeData, setEquipeData, name, setName, isLoading, setIsLoading, fetchEquipe }}>
      {isLoading ? (
        <div>Carregando...</div>
      ) : equipeData ? (
        <Box w={"100%"} display={"flex"} flexDir={"column"} alignItems={"center"}>
          <SectionHeader title={name} isTeamSection={true} equipe={equipeData} />
          <EquipeTabs />
        </Box>
      ) : null}
    </EquipeContext.Provider>
  );
};
