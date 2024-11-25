import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Home from './pages/Home/Home';
import { SearchPage } from './pages/SearchPage/SearchPage';
import SearchesList from './pages/ListViews/SearchesList';
import AuthGuard from './components/Login/AuthGuard';
import useAuth from "./hooks/useAuth";
import AdminList from "./pages/ListViews/AdminList";
import AccessDeniedPage from "./pages/ErrorPages/AccessDeniedPage";
import React from "react";
import axios from "axios";
import SavedTripView from "./pages/TripPage/TripView";
import TripList from "./pages/ListViews/TripList";
import LoginFailedPage from './pages/ErrorPages/LoginFailedPage';

function App() {
    const { isAuthenticated, token, user, loading} = useAuth();

    axios.defaults.withCredentials = true;
    axios.defaults.baseURL = 'http://localhost:8080';
    axios.interceptors.response.use(
        response => response,
        error => {
            if (error.response?.status === 403) {
                const redirectPath = error.response?.data?.redirect || '/denied';
                window.location.href = redirectPath;
            }
            return Promise.reject(error);
        }
    );

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
                            <SearchesList user={user}/>
                        </AuthGuard>
                    }
                />
                <Route
                    path="/users"
                    element={
                        <AuthGuard isAuthenticated={isAuthenticated} loading={loading}>
                            <AdminList user={user} />
                        </AuthGuard>
                    }

                />
                <Route
                    path="/denied"
                    element={
                        <AccessDeniedPage/>
                    }
                />
                <Route
                    path="/loginFailed"
                    element={
                        <LoginFailedPage/>
                    }
                />
                <Route
                    path="/travels"
                    element={
                        <AuthGuard isAuthenticated={isAuthenticated} loading={loading}>
                            <TripList user={user}/>
                        </AuthGuard>
                    }
                />
                <Route
                    path="/trip/:id"
                    element={
                        <AuthGuard isAuthenticated={isAuthenticated} loading={loading}>
                            <SavedTripView/>
                        </AuthGuard>
                    }
                />
            </Routes>
        </Router>
    );
}
export default App;