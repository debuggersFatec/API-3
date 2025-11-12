import { axiosInstance } from "./axiosInstance"

export const notificationServices = {
    markAsRead: async (notificationUuid: string): Promise<void> => {
        try {
            await axiosInstance.post(`/notifications/${notificationUuid}/read`);
        } catch (error) {
            console.error("Error marking notification as read:", error);
            throw error;
        }

    },

    delete: async (notificationUuid: string): Promise<void> => {
        try {
            await axiosInstance.delete(`/notifications/${notificationUuid}`);
        } catch (error) {
            console.error("Error deleting notification:", error);
            throw error;
        }
    }
}