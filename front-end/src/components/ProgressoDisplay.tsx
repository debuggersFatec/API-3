import type { TaskProject } from "@/types/task";
import { Chart, useChart } from "@chakra-ui/charts";
import { Box, Text } from "@chakra-ui/react";

import { Cell, Label, Pie, PieChart, Tooltip } from "recharts";

interface ProgressoDisplayProps {
  tasks: TaskProject[];
}

export const ProgressoDisplay = ({ tasks }: ProgressoDisplayProps) => {
  let concluidas = 0;
  let naoConcluidas = 0;
  tasks.forEach((t) => {
    const status = (t.status || "").toLowerCase();
    if (status === "completed" || status === "concluida") {
      concluidas++;
    } else {
      naoConcluidas++;
    }
  });
  const chart = useChart({
    data: [
      { name: "Não concluidas", value: naoConcluidas, color: "white" },
      { name: "Concluidas", value: concluidas, color: "blue.solid" },
    ],
  });

  if (tasks.length === 0) {
    return (
      <Box
        border={"1px solid"}
        borderColor={"gray.200"}
        w={"100%"}
        borderRadius={"8px"}
        p={4}
      >
        <Text fontWeight="bold" mb={8}>
          Progresso
        </Text>
        <Text>Sem tarefas para mostrar</Text>
      </Box>
    );
  }

  return (
    <Box
      border={"1px solid"}
      borderColor={"gray.200"}
      w={"100%"}
      borderRadius={"8px"}
      p={4}
    >
      <Chart.Root boxSize="200px" chart={chart} mx="auto">
        <PieChart>
          <Tooltip
            cursor={false}
            animationDuration={100}
            content={<Chart.Tooltip hideLabel />}
          />
          <Pie
            innerRadius={75}
            outerRadius={100}
            isAnimationActive={true}
            data={chart.data}
            dataKey={chart.key("value")}
            nameKey="name"
          >
            <Label
              content={({ viewBox }) => (
                <Chart.RadialText
                  viewBox={viewBox}
                  title={(() => {
                    const total = chart.getTotal("value");
                    if (total === 0) return "0%";

                    const concluidas =
                      chart.data.find((item) => item.name === "Concluidas")
                        ?.value || 0;

                    const porcentagem = Math.round((concluidas / total) * 100);
                    return `${porcentagem}%`;
                  })()}
                  description="Total realizado"
                />
              )}
            />
            {chart.data.map((item) => (
              <Cell key={item.color} fill={chart.color(item.color)} />
            ))}
          </Pie>
        </PieChart>
      </Chart.Root>
    </Box>
  );
};
