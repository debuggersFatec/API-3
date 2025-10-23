import { useCallback, useRef, useState } from "react";
import axios from "axios";
import { useAuth } from "@/context/auth/useAuth";
import type { Team } from "@/types/team";
import { TeamContext } from "./TeamContext";

export const TeamProvider = ({ children }: { children: React.ReactNode }) => {
  const { token } = useAuth();

  const [name, setName] = useState("");
  const [teamData, setTeamData] = useState<Team | undefined>();
  const [isLoading, setIsLoading] = useState(false);

  const lastUuidRef = useRef<string | undefined>(undefined);
  const lastFallbackNameRef = useRef<string | undefined>(undefined);

  const fetchTeam = useCallback(
    async (uuid?: string, fallbackName?: string) => {
      if (!uuid) return;
      const showLoading = !teamData || teamData.uuid !== uuid;
      if (showLoading) setIsLoading(true);
      lastUuidRef.current = uuid;
      lastFallbackNameRef.current = fallbackName;

      try {
        const response = await axios.get(
          `http://localhost:8080/api/teams/${uuid}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setName(response.data.name);
        setTeamData(response.data);
      } catch {
        if (fallbackName) setName(fallbackName);
      } finally {
        if (showLoading) setIsLoading(false);
      }
    },
    [token, teamData]
  );

  const refreshTeam = useCallback(async () => {
    if (!lastUuidRef.current) return;
    await fetchTeam(lastUuidRef.current, lastFallbackNameRef.current);
  }, [fetchTeam]);

  return (
    <TeamContext.Provider
      value={{
        teamData,
        name,
        isLoading,
        fetchTeam,
        refreshTeam,
      }}
    >
      {children}
    </TeamContext.Provider>
  );
};
