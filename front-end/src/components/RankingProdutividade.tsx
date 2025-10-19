"use client";

import { useProject } from "@/context/project/useProject";
import type { TaskProject } from "@/types/task";
import { Chart, useChart } from "@chakra-ui/charts";
import { Box, Text } from "@chakra-ui/react";
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts";



export const RankingProdutividade = () => {
  const {project} = useProject();
  const tasks: TaskProject[] = project?.tasks || [];
  const ranking: Record<string, { name: string; tasksNumber: number }> = {};
  tasks.forEach((t) => {
    const status = (t.status || "").toLowerCase();
    const isConcluida = status === "completed" || status === "concluida";
    const nome = t.responsible?.name?.trim();
    if (isConcluida && nome) {
      if (!ranking[nome]) {
        ranking[nome] = { name: nome, tasksNumber: 0 };
      }
      ranking[nome].tasksNumber++;
    }
  });

  const sortedData = Object.values(ranking)
    .sort((a, b) => b.tasksNumber - a.tasksNumber)
    .slice(0, 3);

  const chart = useChart({
    data: sortedData,
    series: [{ name: "tasksNumber", color: "blue", stackId: "a" }],
  });

  if (tasks.length === 0) {
    return ( <Box h={"300px"} w={"100%"}>
      <Box
        w={"100%"}
        display={"flex"}
        flexDir={"column"}
        border={"1px solid"}
        borderColor={"gray.200"}
        h={"100%"}
        pt={"24px"}
        px={"24px"}
        borderRadius={"8px"}
      >
        <Text h={"5%"} fontSize={"lg"} fontWeight={"bold"}mb={8}>
          Ranking de Produtividade
        </Text>
        <Text>Sem tarefas para mostrar</Text>
      </Box>
    </Box>);
  }

  return (
    <Box h={"300px"} w={"100%"}>
      <Box
        w={"100%"}
        display={"flex"}
        flexDir={"column"}
        border={"1px solid"}
        borderColor={"gray.200"}
        h={"100%"}
        pt={"24px"}
        px={"24px"}
        borderRadius={"8px"}
      >
        <Text h={"5%"} fontSize={"lg"} fontWeight={"bold"}>
          Ranking de Produtividade
        </Text>
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
              dataKey="name"
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
      </Box>
    </Box>
  );
};
