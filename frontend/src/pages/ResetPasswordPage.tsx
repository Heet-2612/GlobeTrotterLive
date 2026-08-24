import React, { useState } from 'react';
import { api } from '../services/api';
import { Compass, CheckCircle2, Lock, ArrowRight } from 'lucide-react';
import { Button, Input, Card } from '../components/common/UIComponents';

interface ResetPasswordPageProps {
  token?: string;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const ResetPasswordPage: React.FC<ResetPasswordPageProps> = ({ token: tokenProp, onNavigate }) => {
  // Extract token from props, search query, or hash
  const getTokenFromUrl = (): string => {
    if (tokenProp) return tokenProp;
    const searchParams = new URLSearchParams(window.location.search);
    const searchToken = searchParams.get('token');
    if (searchToken) return searchToken;

    const hash = window.location.hash.replace(/^#/, '');
    if (hash.includes('token=')) {
      const parts = hash.split('token=');
      return parts[1] ? parts[1].split('&')[0] : '';
    }
    return '';
  };

  const token = getTokenFromUrl();

  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!token) {
      setError('Password reset token is missing from URL.');
      return;
    }

    if (!newPassword) {
      setError('Please enter a new password.');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match.');
      return;
    }

    const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
    if (!passwordRegex.test(newPassword)) {
      setError('Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one digit, and one special character (e.g. Password123!).');
      return;
    }

    setLoading(true);

    try {
      await api.resetPassword({
        token,
        newPassword,
      });
      setSuccess(true);
    } catch (err: any) {
      setError(err.message || 'Failed to reset password. The token may be expired or invalid.');
    } finally {
      setLoading(false);
    }
  };

  if (!token) {
    return (
      <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center p-4 gradient-hero">
        <Card className="max-w-md w-full p-8 text-center space-y-4 bg-white border border-slate-200 shadow-xl">
          <div className="w-12 h-12 rounded-full bg-rose-100 text-rose-600 flex items-center justify-center mx-auto text-xl font-bold">
            ⚠️
          </div>
          <h2 className="text-xl font-bold text-slate-900">Missing Reset Token</h2>
          <p className="text-xs text-slate-600">
            No password reset token was provided in the link URL. Please request a new password reset link.
          </p>
          <Button variant="emerald" size="md" onClick={() => onNavigate('login')}>
            Return to Login
          </Button>
        </Card>
      </div>
    );
  }

  return (
    <div className="min-h-[calc(100vh-4rem)] flex items-center justify-center p-4 sm:p-6 lg:p-8 gradient-hero">
      <Card className="max-w-md w-full p-8 sm:p-10 space-y-6 bg-white border border-slate-200 shadow-xl">
        <div className="text-center space-y-2">
          <div className="w-12 h-12 rounded-2xl gradient-accent flex items-center justify-center text-white mx-auto shadow-md">
            <Lock size={22} />
          </div>
          <h1 className="text-2xl font-extrabold text-slate-900 tracking-tight">Reset Your Password</h1>
          <p className="text-xs text-slate-600">Enter and confirm your new account password below</p>
        </div>

        {success ? (
          <div className="space-y-4 text-center py-4">
            <div className="p-4 bg-emerald-50 border border-emerald-200 text-emerald-800 text-sm rounded-xl font-semibold flex items-center justify-center space-x-2">
              <CheckCircle2 size={18} className="text-emerald-600" />
              <span>Your password has been reset successfully.</span>
            </div>
            <Button variant="emerald" size="md" className="w-full" icon={<ArrowRight size={16} />} onClick={() => onNavigate('login')}>
              Return to Login
            </Button>
          </div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4">
            {error && (
              <div className="p-3.5 bg-rose-50 border border-rose-200 text-rose-800 text-xs rounded-xl font-medium">
                {error}
              </div>
            )}

            <Input
              label="New Password *"
              type="password"
              value={newPassword}
              onChange={(e) => setNewPassword(e.target.value)}
              placeholder="••••••••"
              required
            />

            <Input
              label="Confirm New Password *"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="••••••••"
              required
            />

            <p className="text-[11px] text-slate-600 bg-slate-50 p-2.5 rounded-xl border border-slate-200">
              💡 Min 8 chars, 1 uppercase, 1 lowercase, 1 digit, 1 special character (e.g. Password123!).
            </p>

            <Button type="submit" variant="emerald" size="lg" loading={loading} className="w-full">
              Reset Password
            </Button>
          </form>
        )}
      </Card>
    </div>
  );
};
