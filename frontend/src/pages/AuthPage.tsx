import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Compass, Sparkles, Shield, MapPin, ArrowRight, CheckCircle2 } from 'lucide-react';
import { Button, Input, Card } from '../components/common/UIComponents';
import { api } from '../services/api';

interface AuthPageProps {
  onSuccess: () => void;
}

export const AuthPage: React.FC<AuthPageProps> = ({ onSuccess }) => {
  const { login, signup } = useAuth();
  const [mode, setMode] = useState<'login' | 'signup' | 'forgot'>('login');

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    setLoading(true);

    try {
      if (mode === 'login') {
        if (!email || !password) throw new Error('Please enter email and password.');
        await login({ email, password });
        onSuccess();
      } else if (mode === 'signup') {
        if (!name || !email || !password) throw new Error('Please fill in all required fields.');
        await signup({ name, email, password });
        onSuccess();
      } else if (mode === 'forgot') {
        if (!email) throw new Error('Please enter your account email address.');
        const res = await api.forgotPassword({ email: email.trim() });
        setSuccessMsg(res.message || 'If an account exists for this email, password reset instructions have been sent.');
      }
    } catch (err: any) {
      setError(err.message || 'Authentication failed');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center p-4 sm:p-6 lg:p-8 gradient-hero">
      <div className="max-w-5xl w-full grid grid-cols-1 lg:grid-cols-12 rounded-3xl overflow-hidden glass-panel border border-slate-200 shadow-xl">
        {/* Left Side: Travel Hero Showcase */}
        <div className="lg:col-span-6 relative p-8 lg:p-12 flex flex-col justify-between overflow-hidden min-h-[340px] lg:min-h-[580px]">
          <img
            src="https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=1000&q=80"
            alt="GlobeTrotter Travel Showcase"
            className="absolute inset-0 w-full h-full object-cover opacity-30"
          />
          <div className="absolute inset-0 bg-gradient-to-t from-slate-900/90 via-slate-900/60 to-slate-900/30"></div>

          <div className="relative z-10 space-y-4">
            <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-emerald-500/20 text-emerald-300 border border-emerald-500/40 text-xs font-semibold">
              <Sparkles size={13} />
              <span>Smart Travel Companion</span>
            </div>
            <div className="flex items-center space-x-3">
              <div className="w-11 h-11 rounded-2xl gradient-accent flex items-center justify-center text-white shadow-md shadow-emerald-900/20">
                <Compass size={24} />
              </div>
              <span className="text-3xl font-extrabold text-white tracking-tight">
                Globe<span className="gradient-text">Trotter</span>
              </span>
            </div>
          </div>

          <div className="relative z-10 space-y-6">
            <h1 className="text-2xl sm:text-3xl font-extrabold text-white leading-tight">
              Plan your next multi-city journey with precision & style.
            </h1>

            <ul className="space-y-2.5 text-xs sm:text-sm text-slate-200 font-medium">
              <li className="flex items-center space-x-2.5">
                <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
                <span>Multi-stop itinerary planning & activity scheduling</span>
              </li>
              <li className="flex items-center space-x-2.5">
                <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
                <span>Real-time budget forecasting & cost breakdowns</span>
              </li>
              <li className="flex items-center space-x-2.5">
                <CheckCircle2 size={16} className="text-emerald-400 shrink-0" />
                <span>Public itinerary sharing & one-click trip cloning</span>
              </li>
            </ul>
          </div>
        </div>

        {/* Right Side: Auth Form (Light Neutral) */}
        <div className="lg:col-span-6 p-8 lg:p-12 bg-white/95 backdrop-blur-md flex flex-col justify-center border-l border-slate-200">
          <div className="max-w-sm w-full mx-auto space-y-6">
            <div className="text-center space-y-1">
              <h2 className="text-2xl font-extrabold text-slate-900 tracking-tight">
                {mode === 'login' && 'Welcome back'}
                {mode === 'signup' && 'Create an account'}
                {mode === 'forgot' && 'Reset your password'}
              </h2>
              <p className="text-xs text-slate-600">
                {mode === 'login' && 'Sign in to access your saved trips and itineraries'}
                {mode === 'signup' && 'Join GlobeTrotter to curate your dream vacations'}
                {mode === 'forgot' && 'Enter your email address to receive password reset instructions'}
              </p>
            </div>

            {/* Mode Switcher Tabs */}
            <div className="flex rounded-xl bg-slate-100 p-1 border border-slate-200 text-xs font-semibold">
              <button
                type="button"
                onClick={() => { setMode('login'); setError(null); setSuccessMsg(null); }}
                className={`flex-1 py-2 rounded-lg transition-all ${
                  mode === 'login' ? 'bg-emerald-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                Login
              </button>
              <button
                type="button"
                onClick={() => { setMode('signup'); setError(null); setSuccessMsg(null); }}
                className={`flex-1 py-2 rounded-lg transition-all ${
                  mode === 'signup' ? 'bg-emerald-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                Sign Up
              </button>
              <button
                type="button"
                onClick={() => { setMode('forgot'); setError(null); setSuccessMsg(null); }}
                className={`flex-1 py-2 rounded-lg transition-all ${
                  mode === 'forgot' ? 'bg-emerald-600 text-white shadow-xs' : 'text-slate-600 hover:text-slate-900'
                }`}
              >
                Forgot
              </button>
            </div>

            {error && (
              <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 text-xs rounded-xl font-medium">
                {error}
              </div>
            )}

            {successMsg && (
              <div className="p-3.5 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-xl font-semibold leading-relaxed">
                ✓ {successMsg}
              </div>
            )}

            <form onSubmit={handleSubmit} className="space-y-4">
              {mode === 'signup' && (
                <Input
                  label="Full Name"
                  type="text"
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="John Doe"
                  required
                />
              )}

              <Input
                label="Email Address"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="user@example.com"
                required
              />

              {mode !== 'forgot' && (
                <Input
                  label="Password"
                  type="password"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  placeholder="••••••••"
                  required
                />
              )}

              {mode === 'signup' && (
                <p className="text-[11px] text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-200">
                  💡 Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special character (e.g. Password123!).
                </p>
              )}

              <Button
                type="submit"
                variant="emerald"
                size="lg"
                loading={loading}
                className="w-full mt-2"
                icon={<ArrowRight size={16} />}
              >
                {mode === 'login' ? 'Sign In' : mode === 'signup' ? 'Create Account' : 'Send Instructions'}
              </Button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};
