import { axiosInstance } from "./axiosInstanceCalendar";
import type { GoogleEvent } from "../types/calendar";

export const CalendarService = {
  listEvents: async (userUuid: string): Promise<GoogleEvent[]> => {
    const res = await axiosInstance.get<GoogleEvent[]>(`/calendar/events/${userUuid}`);
    return res.data ?? [];
  },

  createEvent: async (userUuid: string, event: Partial<GoogleEvent>): Promise<GoogleEvent> => {
    const res = await axiosInstance.post<GoogleEvent>(`/calendar/events/${userUuid}`, event);
    return res.data;
  },

  updateEvent: async (userUuid: string, eventId: string, event: Partial<GoogleEvent>): Promise<GoogleEvent> => {
    const res = await axiosInstance.put<GoogleEvent>(`/calendar/events/${userUuid}/${eventId}`, event);
    return res.data;
  },

  deleteEvent: async (userUuid: string, eventId: string): Promise<void> => {
    await axiosInstance.delete(`/calendar/events/${userUuid}/${eventId}`);
  },

  syncGoogle: (userUuid: string) => {
    const w = window.open(
      `http://localhost:8080/calendar/auth/google/${userUuid}`,
      "googleAuth",
      "width=600,height=700,resizable=yes,scrollbars=yes"
    );
    return w;
  },
};
