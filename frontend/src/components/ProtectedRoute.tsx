import { ReactNode, useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';

type ProtectedRouteProps = {
  children: ReactNode;
};

const API_BASE_URL = import.meta.env.VITE_BACKEND_URL ?? 'http://localhost:8080';

const ProtectedRoute = ({ children }: ProtectedRouteProps) => {
  const [authStatus, setAuthStatus] = useState<'loading' | 'authorized' | 'unauthorized'>('loading');

  useEffect(() => {
    let isMounted = true;

    const checkAuth = async () => {
      try {
        const response = await fetch(`${API_BASE_URL}/user`, {
          credentials: 'include',
        });

        if (!isMounted) {
          return;
        }

        if (response.ok) {
          setAuthStatus('authorized');
        } else {
          setAuthStatus('unauthorized');
        }
      } catch {
        if (isMounted) {
          setAuthStatus('unauthorized');
        }
      }
    };

    checkAuth();

    return () => {
      isMounted = false;
    };
  }, []);

  if (authStatus === 'loading') {
    return <p>Checking your session...</p>;
  }

  if (authStatus === 'unauthorized') {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;

