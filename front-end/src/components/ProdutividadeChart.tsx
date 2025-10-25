"use client";

import { useProject } from "@/context/project/useProject";
import { Chart, useChart } from "@chakra-ui/charts";
import { Box, Text } from "@chakra-ui/react";
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts";

export const ProdutividadeChart = () => {
  const { project } = useProject();
  const tasks = project?.tasks ?? [];

  const notStarted = tasks.filter((t) => t.status === "NOT_STARTED").length;
  const inProgress = tasks.filter((t) => t.status === "IN_PROGRESS").length;
  const completed = tasks.filter((t) => t.status === "COMPLETED").length;

  const chart = useChart({
    data: [
      { tasksNumber: notStarted, label: "Não iniciada" },
      { tasksNumber: inProgress, label: "Em progresso" },
      { tasksNumber: completed, label: "Concluída" },
    ],
    series: [{ name: "tasksNumber", color: "blue", stackId: "a" }],
  });

  return (
    <Box h={"300px"} w={"100%"}>
      <Box
        w={"100%"}
        display={"flex"}
        flexDir={"column"}
        border={"1px solid"}
        borderColor={"gray.200"}
        borderRadius={"8px"}
        h={"100%"}
        pt={"24px"}
        px={"24px"}
      >
        <Text h={"5%"} fontSize={"lg"} fontWeight={"bold"}>
          Gráfico de Produtividade
        </Text>
        {tasks.length === 0 ? (
          <Box h={"95%"} display="flex" alignItems="center" px={2}>
            <Text>Sem tarefas para mostrar</Text>
          </Box>
        ) : (
          <Chart.Root chart={chart} h={"95%"} w={"100%"}>
            <BarChart
              layout="vertical"
              data={chart.data}
              margin={{ top: 0, right: 0, left: 0, bottom: 0 }}
            >
              <CartesianGrid horizontal={false} vertical={false} />
              <XAxis type="number" display="none" />
              <YAxis
                type="category"
                dataKey="label"
                orientation="left"
                width={110}
                axisLine={false}
                tickLine={false}
                tick={{
                  fill: "#333",
                  fontSize: 16,
                  style: { whiteSpace: "nowrap" },
                }}
              />
              {chart.series.map((item) => (
                <Bar
                  key={item.name}
                  stackId={item.stackId}
                  barSize={30}
                  isAnimationActive={true}
                  dataKey={chart.key(item.name)}
                  fill="#2391FF"
                  background={{
                    fill: "#E2E8F0",
                    radius: 8,
                  }}
                  radius={[8, 8, 8, 8]}
                />
              ))}
            </BarChart>
          </Chart.Root>
        )}
      </Box>
    </Box>
  );
};
