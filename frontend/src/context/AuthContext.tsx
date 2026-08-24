import React, { createContext, useContext, useState, useEffect, ReactNode } from 'react';
import { UserResponse, LoginRequest, SignupRequest, ForgotPasswordRequest } from '../types';
import { api } from '../services/api';

interface AuthContextType {
  user: UserResponse | null;
  token: string | null;
  isAuthenticated: boolean;
  loading: boolean;
  login: (data: LoginRequest) => Promise<void>;
  signup: (data: SignupRequest) => Promise<void>;
  forgotPassword: (data: ForgotPasswordRequest) => Promise<void>;
  logout: () => void;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<UserResponse | null>(null);
  const [token, setToken] = useState<string | null>(localStorage.getItem('globetrotter_token'));
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    const restoreSession = async () => {
      const storedToken = localStorage.getItem('globetrotter_token');
      if (storedToken) {
        try {
          const userData = await api.getCurrentUser();
          setUser(userData);
          setToken(storedToken);
        } catch {
          localStorage.removeItem('globetrotter_token');
          localStorage.removeItem('globetrotter_user');
          setUser(null);
          setToken(null);
        }
      }
      setLoading(false);
    };

    restoreSession();

    const handleUnauthorized = () => {
      setUser(null);
      setToken(null);
    };

    window.addEventListener('globetrotter_unauthorized', handleUnauthorized);
    return () => {
      window.removeEventListener('globetrotter_unauthorized', handleUnauthorized);
    };
  }, []);

  const login = async (data: LoginRequest) => {
    const response = await api.login(data);
    localStorage.setItem('globetrotter_token', response.token);
    localStorage.setItem('globetrotter_user', JSON.stringify(response.user));
    setToken(response.token);
    setUser(response.user);
  };

  const signup = async (data: SignupRequest) => {
    const response = await api.signup(data);
    localStorage.setItem('globetrotter_token', response.token);
    localStorage.setItem('globetrotter_user', JSON.stringify(response.user));
    setToken(response.token);
    setUser(response.user);
  };

  const forgotPassword = async (data: ForgotPasswordRequest) => {
    await api.forgotPassword(data);
  };

  const logout = () => {
    localStorage.removeItem('globetrotter_token');
    localStorage.removeItem('globetrotter_user');
    setToken(null);
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        token,
        isAuthenticated: !!token && !!user,
        loading,
        login,
        signup,
        forgotPassword,
        logout,
        isAdmin: user?.role === 'ADMIN',
      }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};
