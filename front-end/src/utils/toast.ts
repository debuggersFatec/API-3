import { toaster } from "@/components/ui/toasterClient";

export const toast = (status: string, message: string) => {
  toaster.create({
    description: message,
    type:
      status === "error"
        ? "danger"
        : status === "success"
        ? "success"
        : "default",
  });
};
