import { Grid, GridItem } from "@chakra-ui/react";
import { ProdutividadeChart } from "./ProdutividadeChart";
import { ProgressoDisplay } from "./ProgressoDisplay";
import { RankingProdutividade } from "./RankingProdutividade";
import { ProximasTasks } from "./ProximasTasks";
import { TarefasAtribuidas } from "./TarefasAtribuidas";

export const TabDashboard = () => {
  return (
    <Grid
      w="100%"
      p={{ base: "1rem", md: "32px" }}
      templateColumns={{ base: "1fr", md: "repeat(3, 1fr)" }}
      templateRows="auto auto"
      gap={6}
    >
      <GridItem gridColumn="1 / 2" gridRow="1 / 2">
        <ProdutividadeChart />
      </GridItem>

      <GridItem gridColumn="1 / 2" gridRow="2 / 3">
        <RankingProdutividade />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "2 / 3" }}
        gridRow={{ base: "3", md: "1 / 3" }}
      >
        <ProximasTasks />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "3 / 4" }}
        gridRow={{ base: "4", md: "1 / 2" }}
      >
        <ProgressoDisplay />
      </GridItem>

      <GridItem
        gridColumn={{ base: "1", md: "3 / 4" }}
        gridRow={{ base: "5", md: "2 / 3" }}
      >
        <TarefasAtribuidas />
      </GridItem>
    </Grid>
  );
};
