import { Grid } from "@chakra-ui/react";
import { QuadroDisplay } from "./Board/QuadroDisplay";

export const TabQuadro = () => {
  return (
    <Grid w={"100%"}>
      <QuadroDisplay />
    </Grid>
  );
};
