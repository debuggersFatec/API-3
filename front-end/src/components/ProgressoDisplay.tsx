import { Chart, useChart } from "@chakra-ui/charts";
import { Cell, Label, Pie, PieChart, Tooltip } from "recharts";

export const ProgressoDisplay = () => {
  const chart = useChart({
    data: [
      { name: "Não concluidas", value: 25, color: "white" },
      { name: "Concluidas", value: 22, color: "blue.solid" },
    ],
  });

  return (
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
  );
};
