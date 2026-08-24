import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { User, LogOut, Settings, Globe, Bell, Check, Shield } from 'lucide-react';
import { Button, Card, Badge, Input } from '../components/common/UIComponents';

interface ProfilePageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const ProfilePage: React.FC<ProfilePageProps> = ({ onNavigate }) => {
  const { user, logout } = useAuth();
  const [language, setLanguage] = useState(user?.languagePreference || 'en');
  const [notificationsEnabled, setNotificationsEnabled] = useState(true);
  const [savedMsg, setSavedMsg] = useState(false);

  const handleSavePreferences = (e: React.FormEvent) => {
    e.preventDefault();
    setSavedMsg(true);
    setTimeout(() => setSavedMsg(false), 2500);
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header */}
      <div className="flex items-center justify-between pb-6 border-b border-slate-200">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">User Profile & Settings</h1>
          <p className="text-xs text-slate-500 mt-1">Manage your account information and travel application preferences</p>
        </div>
        <Button variant="danger" size="sm" icon={<LogOut size={14} />} onClick={logout}>
          Logout
        </Button>
      </div>

      {/* Account Info Card */}
      <Card className="p-8 flex flex-col sm:flex-row items-center sm:items-start space-y-4 sm:space-y-0 sm:space-x-6 bg-white border border-slate-200 shadow-xs">
        <div className="w-20 h-20 rounded-2xl gradient-accent text-white font-extrabold text-3xl flex items-center justify-center shadow-md shadow-emerald-900/10 shrink-0">
          {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
        </div>

        <div className="flex-1 text-center sm:text-left space-y-2">
          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2">
            <h2 className="text-2xl font-extrabold text-slate-900">{user?.name}</h2>
            <Badge variant="emerald" icon={<Shield size={12} />}>
              Active Account
            </Badge>
          </div>
          <p className="text-sm text-slate-600 font-medium">✉️ {user?.email}</p>
          <div className="pt-2 flex flex-wrap gap-2 justify-center sm:justify-start text-xs">
            <Badge variant="slate">User ID: #{user?.id}</Badge>
            <Badge variant="blue">Language: {user?.languagePreference?.toUpperCase() || 'EN'}</Badge>
            {user?.createdAt && (
              <Badge variant="slate">Joined: {new Date(user.createdAt).toLocaleDateString()}</Badge>
            )}
          </div>
        </div>
      </Card>

      {/* Settings & Preferences Form */}
      <Card className="p-8 space-y-6 bg-white border border-slate-200 shadow-xs">
        <div className="flex items-center space-x-2 border-b border-slate-200 pb-4">
          <Settings size={20} className="text-emerald-700" />
          <h3 className="text-lg font-bold text-slate-900">Application Preferences</h3>
        </div>

        {savedMsg && (
          <div className="p-3.5 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-xl font-semibold flex items-center space-x-2">
            <Check size={16} />
            <span>Preferences saved successfully!</span>
          </div>
        )}

        <form onSubmit={handleSavePreferences} className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
                Language Preference
              </label>
              <select
                value={language}
                onChange={(e) => setLanguage(e.target.value)}
                className="w-full bg-white border border-slate-300 rounded-xl px-3.5 py-2.5 text-slate-900 text-sm focus:outline-none focus:border-emerald-600 transition-all font-medium"
              >
                <option value="en">English (US)</option>
                <option value="es">Spanish (Español)</option>
                <option value="fr">French (Français)</option>
                <option value="de">German (Deutsch)</option>
              </select>
            </div>

            <div className="space-y-2">
              <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
                Notifications
              </label>
              <div className="flex items-center space-x-3 pt-2">
                <input
                  type="checkbox"
                  id="notifications"
                  checked={notificationsEnabled}
                  onChange={(e) => setNotificationsEnabled(e.target.checked)}
                  className="w-4 h-4 rounded border-slate-300 bg-white text-emerald-600 focus:ring-emerald-600"
                />
                <label htmlFor="notifications" className="text-sm text-slate-700 cursor-pointer font-medium">
                  Receive itinerary updates & budget notifications
                </label>
              </div>
            </div>
          </div>

          <div className="pt-4 border-t border-slate-200 flex justify-end">
            <Button type="submit" variant="emerald" size="md" icon={<Check size={16} />}>
              Save Preferences
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
