import { FormEvent, useState } from 'react';

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

const Login = () => {
  const [email, setEmail] = useState('user');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [status, setStatus] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setError('');
    setStatus('');

    if (!email || !password) {
      setError('Please provide both username and password.');
      return;
    }

    setIsSubmitting(true);
    try {
      const body = new URLSearchParams({
        username: email,
        password,
      });

      const response = await fetch(`${API_BASE_URL}/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body,
      });

      if (response.ok) {
        setStatus('Login successful! You can now access the user page.');
      } else if (response.status === 401) {
        setError('Invalid credentials. Please try again.');
      } else {
        setError('Login failed. Please try again later.');
      }
    } catch (fetchError) {
      console.error(fetchError);
      setError('Unable to reach the server. Check your connection.');
    } finally {
      setIsSubmitting(false);
      setPassword('');
    }
  };

  return (
    <div>
      <h1>Login</h1>
      <form onSubmit={handleSubmit}>
        <div>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            placeholder="you@example.com"
          />
        </div>
        <div>
          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            placeholder="password"
          />
        </div>
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Logging in…' : 'Log in'}
        </button>
        {error && <p>{error}</p>}
        {status && <p>{status}</p>}
      </form>
    </div>
  );
};

export default Login;