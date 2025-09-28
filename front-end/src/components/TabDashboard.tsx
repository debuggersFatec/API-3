import { Grid, GridItem } from "@chakra-ui/react";
import { ProdutividadeChart } from "./ProdutividadeChart";
import { ProgressoDisplay } from "./ProgressoDisplay";
import { RankingProdutividade } from "./RankingProdutividade";
import { ProximasTasks } from "./ProximasTasks";
import { TarefasAtribuidas } from "./TarefasAtribuidas";
import type { TaskTeam } from "@/types/task";

interface TabDashboardProps {
  tasks: TaskTeam[];
}

export const TabDashboard = ({ tasks }: TabDashboardProps) => {
  return (
    <Grid
      w="100%"
      p={{ base: "1rem", md: "32px" }}
      templateColumns={{ base: "1fr", md: "repeat(3, 1fr)" }}
      templateRows="auto auto"
      gap={6}
    >
      <GridItem gridColumn="1 / 2" gridRow="1 / 2">
        <ProdutividadeChart tasks={tasks ?? []} />
      </GridItem>

      <GridItem gridColumn="1 / 2" gridRow="2 / 3">
        <RankingProdutividade tasks={tasks ?? []} />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "2 / 3" }}
        gridRow={{ base: "3", md: "1 / 3" }}
      >
        <ProximasTasks tasks={tasks} />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "3 / 4" }}
        gridRow={{ base: "4", md: "1 / 2" }}
      >
        <ProgressoDisplay tasks={tasks ?? []} />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "3 / 4" }}
        gridRow={{ base: "5", md: "2 / 3" }}
      >
        <TarefasAtribuidas tasks={tasks ?? []} />
      </GridItem>
    </Grid>
  );
};
