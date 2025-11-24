import { useState } from "react";
import { Calendar, dateFnsLocalizer } from "react-big-calendar";
import { format, parse, startOfWeek, getDay } from "date-fns";
import ptBR from "date-fns/locale/pt-BR";
import "react-big-calendar/lib/css/react-big-calendar.css";

import { Button, HStack, Text } from "@chakra-ui/react";
import type { GoogleEvent } from "@/types/calendar";

const locales = { "pt-BR": ptBR };

const localizer = dateFnsLocalizer({
  format,
  parse,
  startOfWeek: () => startOfWeek(new Date(), { weekStartsOn: 0 }),
  getDay,
  locales,
});

type MyCalendarProps = {
  events: GoogleEvent[];
};

const fixUTCDate = (iso: string) => {
  const d = new Date(iso);
  return new Date(
    d.getUTCFullYear(),
    d.getUTCMonth(),
    d.getUTCDate()
  );
};

export const MyCalendar = ({ events }: MyCalendarProps) => {
  const [currentDate, setCurrentDate] = useState<Date>(() => new Date());

  const mappedEvents = events.map((e) => {
    let start: Date;

    if (!e.start) {
      start = new Date();
    } else if (/^\d{4}-\d{2}-\d{2}$/.test(e.start)) {
      const [y, m, d] = e.start.split("-").map(Number);
      start = new Date(y, m - 1, d);
    } else {
      start = fixUTCDate(e.start);
    }

    return {
      title: e.summary,
      start,
      end: new Date(start),
      id: e.id,
    };
  });

  const goPrevMonth = () =>
    setCurrentDate((d) => new Date(d.getFullYear(), d.getMonth() - 1, 1));

  const goNextMonth = () =>
    setCurrentDate((d) => new Date(d.getFullYear(), d.getMonth() + 1, 1));

  const goToday = () => setCurrentDate(new Date());

  const handleNavigate = (date: Date) => {
    setCurrentDate(date);
  };

  return (
    <div style={{ height: "750px" }}>
      <HStack gap={4} mb={4}>
        <Button onClick={goPrevMonth}>← Mês anterior</Button>

        <Text fontSize="lg" fontWeight="bold">
          {currentDate.toLocaleDateString("pt-BR", {
            month: "long",
            year: "numeric",
          })}
        </Text>

        <Button onClick={goNextMonth}>Próximo mês →</Button>

        <Button onClick={goToday} variant="outline">
          Hoje
        </Button>
      </HStack>

      <Calendar
        localizer={localizer}
        events={mappedEvents}
        startAccessor="start"
        endAccessor="end"
        culture="pt-BR"
        date={currentDate}
        onNavigate={handleNavigate}
        view="month"
        toolbar={false}
      />
    </div>
  );
};
