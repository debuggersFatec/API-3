import { Route, Routes } from "react-router-dom";
import { Dashboard } from "./page/Dashboard";

export const App = () => {
  return (
    <Routes>
      <Route path="/" element={<Dashboard/>} />
      {/* <Route path="/" element={<App />} /> */}
    </Routes>
  );
};
