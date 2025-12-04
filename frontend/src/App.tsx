import './App.css'
import Header from "./components/Header";
import MultiInputForm from './components/MultiInputForm';
import Card from './components/Card';
import Login from './pages/Login';
import Signup from './pages/Signup';
import User from './pages/User';
import ProtectedRoute from './components/ProtectedRoute';
import { Routes, Route } from 'react-router-dom';

function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <div className="app-container">
            <Header />
            <main className="main-content">
              <Card>
                <MultiInputForm />
              </Card>
            </main>
          </div>
        }
      />
      <Route path="/login" element={<Login />} />
      <Route path="/signup" element={<Signup />} />
      <Route
        path="/user"
        element={
          <ProtectedRoute>
            <User />
          </ProtectedRoute>
        }
      />
    </Routes>
  );
}

export default App;
