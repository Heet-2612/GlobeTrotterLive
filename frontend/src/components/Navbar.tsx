import React, { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { Compass, LayoutDashboard, MapPin, Sparkles, User, LogOut, Menu, X, Globe, Calendar } from 'lucide-react';

interface NavbarProps {
  currentTab: string;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const Navbar: React.FC<NavbarProps> = ({ currentTab, onNavigate }) => {
  const { user, logout, isAuthenticated } = useAuth();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  const handleNavClick = (tab: string, param?: string | number) => {
    onNavigate(tab, param);
    setMobileMenuOpen(false);
  };

  const navItems = [
    { id: 'dashboard', label: 'Dashboard', icon: <LayoutDashboard size={16} /> },
    { id: 'my-trips', label: 'My Trips', icon: <Calendar size={16} /> },
    { id: 'city-search', label: 'Cities', icon: <MapPin size={16} /> },
    { id: 'activity-search', label: 'Activities', icon: <Sparkles size={16} /> },
    { id: 'profile', label: 'Profile', icon: <User size={16} /> },
  ];

  if (user?.role === 'ADMIN') {
    navItems.push({ id: 'admin', label: 'Admin', icon: <Globe size={16} /> });
  }

  return (
    <nav className="glass-panel border-b border-slate-200/90 sticky top-0 z-50 bg-white/90 backdrop-blur-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo Branding */}
          <div
            className="flex items-center space-x-3 cursor-pointer group"
            onClick={() => handleNavClick(isAuthenticated ? 'dashboard' : 'login')}
          >
            <div className="w-10 h-10 rounded-xl gradient-accent flex items-center justify-center text-white shadow-md shadow-emerald-900/10 group-hover:scale-105 transition-transform duration-300">
              <Compass size={22} />
            </div>
            <span className="font-extrabold text-xl tracking-tight text-slate-900">
              Globe<span className="gradient-text">Trotter</span>
            </span>
          </div>

          {/* Desktop Navigation Links */}
          {isAuthenticated ? (
            <div className="hidden md:flex items-center space-x-1">
              {navItems.map((item) => {
                const isActive = currentTab === item.id;
                return (
                  <button
                    key={item.id}
                    onClick={() => handleNavClick(item.id)}
                    className={`flex items-center space-x-2 px-3.5 py-2 rounded-xl text-sm font-semibold transition-all duration-200 ${
                      isActive
                        ? 'bg-emerald-50 text-emerald-700 border border-emerald-200 shadow-2xs'
                        : 'text-slate-600 hover:text-slate-900 hover:bg-slate-100'
                    }`}
                  >
                    <span className={isActive ? 'text-emerald-700' : 'text-slate-400'}>{item.icon}</span>
                    <span>{item.label}</span>
                  </button>
                );
              })}
            </div>
          ) : null}

          {/* User Profile & Actions */}
          {isAuthenticated ? (
            <div className="hidden md:flex items-center space-x-4 border-l border-slate-200 pl-5">
              <div className="flex items-center space-x-2.5 cursor-pointer" onClick={() => handleNavClick('profile')}>
                <div className="w-8 h-8 rounded-full bg-emerald-100 border border-emerald-300 text-emerald-800 font-bold text-xs flex items-center justify-center">
                  {user?.name ? user.name.charAt(0).toUpperCase() : 'U'}
                </div>
                <span className="text-sm font-semibold text-slate-800">
                  {user?.name}
                </span>
              </div>
              <button
                onClick={logout}
                className="text-slate-500 hover:text-rose-600 p-2 hover:bg-slate-100 rounded-xl transition-colors"
                title="Logout"
              >
                <LogOut size={18} />
              </button>
            </div>
          ) : (
            <div className="flex items-center space-x-3">
              <button
                onClick={() => handleNavClick('login')}
                className="text-slate-700 hover:text-slate-900 px-4 py-2 text-sm font-semibold"
              >
                Login
              </button>
            </div>
          )}

          {/* Mobile Menu Toggle Button */}
          {isAuthenticated && (
            <div className="md:hidden flex items-center">
              <button
                onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
                className="p-2 text-slate-600 hover:text-slate-900 rounded-xl focus:outline-none"
              >
                {mobileMenuOpen ? <X size={24} /> : <Menu size={24} />}
              </button>
            </div>
          )}
        </div>
      </div>

      {/* Mobile Drawer Navigation */}
      {mobileMenuOpen && isAuthenticated && (
        <div className="md:hidden border-t border-slate-200 bg-white/95 backdrop-blur-md px-4 pt-3 pb-6 space-y-2">
          {navItems.map((item) => {
            const isActive = currentTab === item.id;
            return (
              <button
                key={item.id}
                onClick={() => handleNavClick(item.id)}
                className={`w-full flex items-center space-x-3 px-4 py-3 rounded-xl text-sm font-semibold transition-all ${
                  isActive
                    ? 'bg-emerald-50 text-emerald-700 border border-emerald-200'
                    : 'text-slate-700 hover:bg-slate-100'
                }`}
              >
                {item.icon}
                <span>{item.label}</span>
              </button>
            );
          })}
          <div className="pt-4 border-t border-slate-200 flex items-center justify-between px-2">
            <span className="text-sm font-semibold text-slate-800">{user?.name}</span>
            <button
              onClick={logout}
              className="text-xs font-semibold text-rose-600 hover:text-rose-700 flex items-center space-x-1"
            >
              <LogOut size={14} />
              <span>Logout</span>
            </button>
          </div>
        </div>
      )}
    </nav>
  );
};
