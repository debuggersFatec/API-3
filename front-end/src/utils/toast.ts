import { toaster } from "@/components/ui/toasterClient";

export const toast = (status: string, message: string) => {
  const type =
    status === "error"
      ? "error"
      : status === "success"
      ? "success"
      : "default";

  try {
    toaster.create({
      description: message,
      type,
      closable: true,
    });
  } catch (err) {
    console.warn("toast: falha ao criar toast:", err);
  }
};
