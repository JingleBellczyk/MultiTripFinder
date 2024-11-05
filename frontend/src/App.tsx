import {SearchPage} from "./pages/SearchPage"
import SearchesList from "./pages/SearchesList"
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import AuthGuard from "./components/Login/AuthGuard";

function App() {
    return (
        <div>
            <Router>
                <Routes>
                    {/*<Route path="/" element={<HomePage />} />  /!* Default *!/*/}
                    <Route path="/" element={<SearchPage />} /> {/* Search page */}
                    <Route path="/searches" element={
                        // <AuthGuard>
                            <SearchesList/>
                        // </AuthGuard>
                    }  />
                    {/*<Route path="/about" element={<AboutPage />} /> /!* About us *!/*/}
                </Routes>
            </Router>
        </div>
    );
}
export default App;