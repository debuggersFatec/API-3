import { useEffect, useRef } from "react";
import { useAuth } from "@/context/auth/useAuth";

/**
 * Hook que automaticamente chama `refreshUser()` do AuthContext em um intervalo.
 * - Para quando não houver token (usuário deslogado)
 * - Pausa quando a aba estiver em background (Page Visibility API)
 * - Dispara uma atualização imediata ao montar
 *
 * Uso: chamar `useAutoRefreshUser()` em um componente de topo (por exemplo `App` ou `AuthProvider`)
 */
export function useAutoRefreshUser(intervalMs = 30_000) {
  const { token, refreshUser } = useAuth();
  const timerRef = useRef<number | null>(null);
  const refreshRef = useRef(refreshUser);
  useEffect(() => {
    refreshRef.current = refreshUser;
  }, [refreshUser]);

  useEffect(() => {
    if (!token) return;

    let mounted = true;

    const run = async () => {
      if (!mounted) return;
      if (typeof document !== "undefined" && document.hidden) return;
      try {
      
        await refreshRef.current();
      } catch {
        // don't break the interval loop; optionally log if needed
      }
    };

    // primeira execução imediata
    run();

    // intervalo regular
    timerRef.current = window.setInterval(run, intervalMs) as unknown as number;

    const onVisibility = () => {
      if (!document.hidden) run();
    };

    document.addEventListener("visibilitychange", onVisibility);

    return () => {
      mounted = false;
      if (timerRef.current) {
        clearInterval(timerRef.current);
        timerRef.current = null;
      }
      document.removeEventListener("visibilitychange", onVisibility);
    };
  }, [token, intervalMs]);
}
