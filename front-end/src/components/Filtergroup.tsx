import { Box, Button } from "@chakra-ui/react";
import { FaFilter, FaSearch } from "react-icons/fa";
export const Filtergroup = () => {
  return (
    <Box gap={'12px'} display={"flex"} flexDirection={"row"}>
      <Button variant={"outline"} borderRadius={"full"}>
        Pesquisar <FaSearch />
      </Button>
      <Button variant={"outline"} borderRadius={"full"}>
        Filtrar <FaFilter />
      </Button>
    </Box>
  );
};
