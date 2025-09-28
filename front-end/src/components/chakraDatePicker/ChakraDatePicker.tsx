import DatePicker from "react-datepicker";
import { Input, Box } from "@chakra-ui/react";
import "react-datepicker/dist/react-datepicker.css";
import "./chakra-datepicker.css";

interface ChakraDatePickerProps {
  selected: Date | null;
  onChange: (date: Date | null) => void;
}
const ChakraDatePicker = ({
  selected,
  onChange,
  ...props
}: ChakraDatePickerProps) => {
  return (
    <Box w="100%" style={{ width: "100%" }}>
      <DatePicker
        wrapperClassName="chakra-datepicker-wrapper"
        customInput={<Input w="100%" style={{ width: "100%" }} />}
        selected={selected}
        onChange={onChange}
        dateFormat="dd/MM/yyyy"
        placeholderText={"Defina um prazo"}
        {...props}
      />
    </Box>
  );
};

export default ChakraDatePicker;
