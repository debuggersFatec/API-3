"use client";

import { Checkbox } from "@chakra-ui/react";
import { useState } from "react";

interface CheckItemProps {
    title: string;
    uuid: string;   
    status: "not-started" | "in-progress" | "completed";
    due_date?: string;

}

export const CheckListItem = ({title, status}: CheckItemProps) => {
  const [checked, setChecked] = useState(status === "completed");
  return (
    <Checkbox.Root
      checked={checked}
      variant={'outline'}
      colorScheme={'gray'}
      onCheckedChange={(e) => setChecked(!!e.checked)}
    >
      <Checkbox.HiddenInput />
      <Checkbox.Control />
      <Checkbox.Label>{title}</Checkbox.Label>
    </Checkbox.Root>
  );
};
