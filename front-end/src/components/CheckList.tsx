import { VStack, Text, Box } from "@chakra-ui/react";
import { CheckListItem } from "./CheckListItem";
import type { TaskProject, TaskUser } from "@/types/task";
import { Filtergroup } from "./Filtergroup";
import { useProject } from "@/context/project/useProject";
import { useTaskFilters } from "@/utils/useTaskFilters";

interface CheckListProps {
  tasks: TaskProject[] | TaskUser[] | undefined;
  hideStatusFilter?: boolean;
  isUserArea?: boolean;
  isTrashcan?: boolean;
}

export const CheckList = ({
  tasks,
  hideStatusFilter,
  isUserArea,
  isTrashcan,
}: CheckListProps) => {
  const { project } = useProject();
  const {
    filters,
    filteredTasks,
    updateSearch,
    updateStatus,
    updatePriority,
    updateDueDateRange,
    updateResponsible,
    clearAllFilters,
    hasActiveFilters,
    totalTasks,
    filteredCount,
  } = useTaskFilters(tasks);

  if (!tasks || tasks.length === 0) {
    return (
      <Box textAlign="center" py={8}>
        <Text color="gray.500" fontSize="lg">
          Sem tarefas para mostrar
        </Text>
      </Box>
    );
  }

  return (
    <VStack gap={4} align="stretch" w="100%" px={4} py={2}>
      <Filtergroup
        filters={filters}
        onSearchChange={updateSearch}
        onStatusChange={updateStatus}
        onPriorityChange={updatePriority}
        onDueDateRangeChange={updateDueDateRange}
        onResponsibleChange={updateResponsible}
        onClearFilters={clearAllFilters}
        hasActiveFilters={hasActiveFilters}
        filteredCount={filteredCount}
        totalCount={totalTasks}
        members={project?.members}
        showStatusFilter={!hideStatusFilter}
      />

      {filteredTasks.length === 0 ? (
        <Box textAlign="center" py={8}>
          <Text color="gray.500" fontSize="lg">
            {hasActiveFilters
              ? "Nenhuma tarefa encontrada com os filtros aplicados"
              : "Sem tarefas para mostrar"}
          </Text>
          {hasActiveFilters && (
            <Text color="gray.400" fontSize="sm" mt={2}>
              Tente ajustar os filtros ou limpar todos os filtros
            </Text>
          )}
        </Box>
      ) : (
        filteredTasks.map((task) => (
          <CheckListItem key={task.uuid} task={task} isUserArea={isUserArea} isTeashcan={isTrashcan} />
        ))
      )}
    </VStack>
  );
};
