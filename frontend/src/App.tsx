import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home';
import { SearchPage } from './pages/SearchPage';
import SearchesList from './pages/SearchesList';
import AuthGuard from './components/Login/AuthGuard';
import useAuth from "./hooks/useAuth";

function App() {
    console.log("App rendered");
    const { isAuthenticated, token, user, loading} = useAuth();
    return (
        <Router>
            <Routes>
                <Route
                    path="/"
                    element={
                            <Home isAuthenticated={isAuthenticated} token={token} user={user}/>
                    }
                />
                <Route
                    path="/search"
                    element={
                        <SearchPage />
                    }
                />
                <Route
                    path="/searches"
                    element={
                        <AuthGuard isAuthenticated={isAuthenticated} loading={loading}>
                            <SearchesList />
                        </AuthGuard>
                    }
                />
            </Routes>
        </Router>
    );
}
export default App;