import './App.css'
import Header from "./components/Header";
import MultiInputForm from './components/MultiInputForm';
import Card from './components/Card';
import Login from './pages/Login';
import User from './pages/User';
import { Routes, Route } from 'react-router-dom';

function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={
          <>
            <Header />
            <Card>
              <MultiInputForm />
            </Card>
          </>
        }
      />
      <Route path="/login" element={<Login />} />
      <Route path="/user" element={<User />} />
    </Routes>
  );
}

export default App;
