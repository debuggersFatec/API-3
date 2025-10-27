import { Box, Button, Text, Input } from "@chakra-ui/react";
import { FaFilter, FaSearch } from "react-icons/fa";
import type { TaskFilters } from "@/utils/useTaskFilters";
import type { Status, Priority } from "@/types/task";
import type { UserRef } from "@/types/user";
import React from "react";

interface FiltergroupProps {
  filters: TaskFilters;
  onSearchChange: (search: string) => void;
  onStatusChange: (status: Status | "all") => void;
  onPriorityChange: (priority: Priority | "all") => void;
  onDueDateRangeChange: (from?: string, to?: string) => void;
  onResponsibleChange: (uuid?: string) => void;
  members?: UserRef[] | undefined;
  onClearFilters: () => void;
  hasActiveFilters: boolean;
  filteredCount: number;
  totalCount: number;
  showStatusFilter?: boolean;
}

export const Filtergroup: React.FC<FiltergroupProps> = ({
  filters,
  onSearchChange,
  onStatusChange,
  onPriorityChange,
  onDueDateRangeChange,
  onResponsibleChange,
  onClearFilters,
  filteredCount,
  totalCount,
  members,
  showStatusFilter = true,
}) => {
  const handleInputChange = (ev: React.ChangeEvent<HTMLInputElement>) => {
    onSearchChange(ev.target.value);
  };
  const [showFilters, setShowFilters] = React.useState(false);
  const [localStatus, setLocalStatus] = React.useState<Status | "all">(
    filters.status ?? "all"
  );
  const [localPriority, setLocalPriority] = React.useState<Priority | "all">(
    filters.priority ?? "all"
  );
  const [localFrom, setLocalFrom] = React.useState<string | undefined>(
    filters.dueDateFrom
  );
  const [localTo, setLocalTo] = React.useState<string | undefined>(
    filters.dueDateTo
  );
  const [localResponsible, setLocalResponsible] = React.useState<
    string | undefined
  >(filters.responsibleUuid);
  const applyFilters = () => {
    onStatusChange(localStatus);
    onPriorityChange(localPriority);
    onDueDateRangeChange(localFrom, localTo);
    onResponsibleChange(localResponsible);
  };

  const clearLocalAndParent = () => {
    setLocalStatus("all");
    setLocalPriority("all");
    setLocalFrom(undefined);
    setLocalTo(undefined);
    setLocalResponsible(undefined);
    onClearFilters();
  };

  return (
    <Box gap={"12px"} display={"flex"} flexDirection={"column"}>
      <Box
        display="flex"
        flexDirection={{ base: "column", md: "row" }}
        alignItems={{ base: "stretch", md: "center" }}
        gap={3}
      >
        <Box flex={1} display="flex" alignItems="center" gap={2} minW={0}>
          <FaSearch color="gray" />
          <Input
            placeholder="Pesquisar por nome"
            value={filters.search}
            onChange={handleInputChange}
            borderRadius="full"
            width="100%"
          />
        </Box>

        <Box display="flex" gap={2} alignItems="center">
          <Button
            size={{ base: "md", md: "sm" }}
            variant={"outline"}
            borderRadius={"full"}
            onClick={() => setShowFilters((s) => !s)}
          >
            <FaFilter style={{ marginRight: 8 }} /> Filtrar
          </Button>

          <Box display={{ base: "none", md: "block" }} ml="0">
            <Text fontSize="sm" color="gray.500">
              {filteredCount} / {totalCount}
            </Text>
          </Box>
        </Box>

        <Box display={{ base: "block", md: "none" }} mt={2}>
          <Text textAlign="right" fontSize="sm" color="gray.500">
            {filteredCount} / {totalCount}
          </Text>
        </Box>
      </Box>

      {showFilters && (
        <Box
          mt={2}
          p={3}
          borderRadius="8px"
          border="1px solid"
          borderColor="gray.100"
        >
          <Box
            display="flex"
            flexDirection={{ base: "column", md: "row" }}
            gap={4}
            alignItems="center"
            flexWrap="wrap"
          >
            {showStatusFilter && (
              <Box flex={{ base: "1 1 100%", md: "0 0 180px" }} minW={0}>
                <select
                  value={localStatus}
                  onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
                    setLocalStatus(e.target.value as Status | "all")
                  }
                  style={{ width: "100%", padding: "8px", borderRadius: 6 }}
                >
                  <option value="all">Todos os status</option>
                  <option value="not-started">Não iniciada</option>
                  <option value="in-progress">Em progresso</option>
                  <option value="COMPLETED">Concluída</option>
                </select>
              </Box>
            )}

            <Box flex={{ base: "1 1 100%", md: "0 0 160px" }} minW={0}>
              <select
                value={localPriority}
                onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
                  setLocalPriority(e.target.value as Priority | "all")
                }
                style={{ width: "100%", padding: "8px", borderRadius: 6 }}
              >
                <option value="all">Todas as prioridades</option>
                <option value="LOW">Baixa</option>
                <option value="MEDIUM">Média</option>
                <option value="HIGH">Alta</option>
              </select>
            </Box>

            <Box flex={{ base: "1 1 45%", md: "0 0 180px" }} minW={0}>
              <Input
                type="date"
                value={localFrom ?? ""}
                onChange={(e) => setLocalFrom(e.target.value || undefined)}
                width="100%"
              />
            </Box>

            <Box flex={{ base: "1 1 45%", md: "0 0 180px" }} minW={0}>
              <Input
                type="date"
                value={localTo ?? ""}
                onChange={(e) => setLocalTo(e.target.value || undefined)}
                width="100%"
              />
            </Box>

            {members && members.length > 0 && (
              <Box flex={{ base: "1 1 100%", md: "0 0 220px" }} minW={0}>
                <select
                  value={localResponsible ?? ""}
                  onChange={(e: React.ChangeEvent<HTMLSelectElement>) =>
                    setLocalResponsible(e.target.value || undefined)
                  }
                  style={{ width: "100%", padding: "8px", borderRadius: 6 }}
                >
                  <option value="">Todos os responsáveis</option>
                  <option value="unassigned">Sem responsável</option>
                  {members.map((m) => (
                    <option key={m.uuid} value={m.uuid}>
                      {m.name}
                    </option>
                  ))}
                </select>
              </Box>
            )}

            <Box display="flex" gap={2} ml="auto" mt={{ base: 2, md: 0 }}>
              <Button
                colorScheme="blue"
                onClick={applyFilters}
                size={{ base: "md", md: "sm" }}
              >
                Aplicar
              </Button>
              <Button
                variant="ghost"
                onClick={clearLocalAndParent}
                size={{ base: "md", md: "sm" }}
              >
                Limpar
              </Button>
            </Box>
          </Box>
        </Box>
      )}
    </Box>
  );
};
