import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

const User = () => {
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const handleLogout = async () => {
    setError('');
    setMessage('');
    setIsSubmitting(true);

    try {
      const response = await fetch(`${API_BASE_URL}/logout`, {
        method: 'POST',
        credentials: 'include',
      });

      if (response.ok) {
        setMessage('You have been logged out.');
        navigate('/login', { replace: true });
      } else {
        setError('Logout failed. Please try again.');
      }
    } catch (err) {
      console.error('Logout error', err);
      setError('Unable to reach the server.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="p-4">
      <h1 className="text-2xl font-semibold mb-4">Hello</h1>
      <button
        onClick={handleLogout}
        disabled={isSubmitting}
        className="bg-red-600 text-white px-4 py-2 rounded disabled:opacity-50"
      >
        {isSubmitting ? 'Logging out…' : 'Log out'}
      </button>
      {message && <p className="text-green-600 mt-3">{message}</p>}
      {error && <p className="text-red-600 mt-3">{error}</p>}
    </div>
  );
};

export default User;

