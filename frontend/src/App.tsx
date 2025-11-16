import './App.css'
import Header from "./components/Header";
import MultiInputForm from './components/MultiInputForm';
import Card from './components/Card';
import Login from './pages/Login';
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
    </Routes>
  );
}

export default App;
