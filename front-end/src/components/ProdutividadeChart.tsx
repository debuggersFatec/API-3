"use client";

import { Chart, useChart } from "@chakra-ui/charts";
import { Box, Text } from "@chakra-ui/react";
import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts";

export const ProdutividadeChart = () => {
  const chart = useChart({
    data: [
      { tasksNumber: 16, status: "Não atribuído" },
      { tasksNumber: 55, status: "Atribuído" },
      { tasksNumber: 190, status: "Concluído" },
    ],
    series: [{ name: "tasksNumber", color: "blue", stackId: "a" }],
  });

  return (
    <Box h={"300px"} w={"100%"} >
      <Box
        w={"100%"}
        display={"flex"}
        flexDir={"column"}
        border={"1px solid"}
        borderColor={"gray.200"}
        h={"100%"}
        pt={"24px"}
        px={"24px"}
      >
        <Text h={"5%"} fontSize={"lg"} fontWeight={"bold"}>
          Gráfico de Produtividade
        </Text>
        <Chart.Root chart={chart} h={"95%"} w={"100%"} >
          <BarChart
            layout="vertical"
            data={chart.data}
            margin={{ top: 0, right: 0, left: 0, bottom: 0 }}
          >
            <CartesianGrid horizontal={false} vertical={false} />
            <XAxis type="number" display="none" />
            <YAxis
              type="category"
              dataKey="status"
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
