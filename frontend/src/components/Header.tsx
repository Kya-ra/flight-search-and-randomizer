import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

export default function Header() {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    checkAuthStatus();
  }, []);

  const checkAuthStatus = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/user`, {
        credentials: 'include',
      });
      setIsAuthenticated(response.ok);
    } catch (error) {
      setIsAuthenticated(false);
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      const response = await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        setIsAuthenticated(false);
        navigate('/', { replace: true });
      }
    } catch (error) {
      console.error('Logout error', error);
    }
  };

  return (
    <header style={{
      width: '100%',
      position: 'relative',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '1.5rem',
      backgroundColor: '#1f2937',
      boxSizing: 'border-box'
    }}>
      <h1 style={{
        fontSize: '2.25rem',
        fontWeight: 'bold',
        color: 'white',
        margin: 0
      }}>SkySaver</h1>

      {!loading && (
        <div style={{
          position: 'absolute',
          right: '1.5rem',
          display: 'flex',
          gap: '0.75rem'
        }}>
          {isAuthenticated ? (
            <>
              <button
                onClick={() => navigate('/user')}
                className="px-4 py-2 bg-blue-600 rounded hover:bg-blue-700 text-white"
              >
                My Account
              </button>
              <button
                onClick={handleLogout}
                className="px-4 py-2 bg-red-600 rounded hover:bg-red-700 text-white"
              >
                Sign Out
              </button>
            </>
          ) : (
            <>
              <button
                onClick={() => navigate('/login')}
                className="px-4 py-2 bg-blue-600 rounded hover:bg-blue-700 text-white"
              >
                Sign In
              </button>
              <button
                onClick={() => navigate('/signup')}
                className="px-4 py-2 bg-green-600 rounded hover:bg-green-700 text-white"
              >
                Sign Up
              </button>
            </>
          )}
        </div>
      )}
    </header>
  );
}
