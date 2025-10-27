import { Grid } from "@chakra-ui/react";
import { QuadroDisplay } from "./Board/QuadroDisplay";
import { useProject } from "@/context/project/useProject";

export const TabQuadro = () => {
  const { project } = useProject();
  const tasks = project?.tasks || [];
  return (
    <Grid w={"100%"}>
      <QuadroDisplay tasks={tasks} />
    </Grid>
  );
};
