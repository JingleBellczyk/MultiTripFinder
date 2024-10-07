import {SearchPage} from "./pages/SearchPage"
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';

function App() {
  return (
      <Router>
        <Routes>
          {/*<Route path="/" element={<HomePage />} />  /!* Default *!/*/}
          <Route path="/" element={<SearchPage />} /> {/* Search page */}
          {/*<Route path="/about" element={<AboutPage />} /> /!* About us *!/*/}
        </Routes>
      </Router>
  );
}
export default App;