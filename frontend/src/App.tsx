import React, { useState, useEffect } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { Navbar } from './components/Navbar';
import { AuthPage } from './pages/AuthPage';
import { ResetPasswordPage } from './pages/ResetPasswordPage';
import { DashboardPage } from './pages/DashboardPage';
import { CreateTripPage } from './pages/CreateTripPage';
import { MyTripsPage } from './pages/MyTripsPage';
import { ItineraryBuilderPage } from './pages/ItineraryBuilderPage';
import { ItineraryViewPage } from './pages/ItineraryViewPage';
import { CitySearchPage } from './pages/CitySearchPage';
import { ActivitySearchPage } from './pages/ActivitySearchPage';
import { BudgetPage } from './pages/BudgetPage';
import { TimelinePage } from './pages/TimelinePage';
import { SharedItineraryPage } from './pages/SharedItineraryPage';
import { ProfilePage } from './pages/ProfilePage';
import { AdminDashboardPage } from './pages/AdminDashboardPage';
import { SharingSection } from './components/SharingSection';
import { Button, LoadingState } from './components/common/UIComponents';
import { ArrowLeft } from 'lucide-react';

const MainAppContent: React.FC = () => {
  const { isAuthenticated, loading } = useAuth();
  const [currentTab, setCurrentTab] = useState<string>('dashboard');
  const [activeParam, setActiveParam] = useState<string | number | undefined>(undefined);

  // Handle hash & path routing for public share tokens (#public/token) and password reset (#reset-password?token=... or /reset-password?token=...)
  useEffect(() => {
    const handleUrlChange = () => {
      const pathname = window.location.pathname;
      const searchParams = new URLSearchParams(window.location.search);
      const hash = window.location.hash.replace(/^#/, '');

      if (pathname === '/reset-password' || hash.startsWith('reset-password')) {
        const token = searchParams.get('token') || (hash.includes('token=') ? hash.split('token=')[1]?.split('&')[0] : '');
        setCurrentTab('reset-password');
        setActiveParam(token);
        return;
      }

      if (hash.startsWith('public/')) {
        const token = hash.replace('public/', '');
        setCurrentTab('public');
        setActiveParam(token);
        return;
      }
    };

    handleUrlChange();
    window.addEventListener('hashchange', handleUrlChange);
    window.addEventListener('popstate', handleUrlChange);
    return () => {
      window.removeEventListener('hashchange', handleUrlChange);
      window.removeEventListener('popstate', handleUrlChange);
    };
  }, []);

  const handleNavigate = (tab: string, param?: string | number) => {
    setCurrentTab(tab);
    setActiveParam(param);
    window.scrollTo(0, 0);
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-[#f5f7f6] text-slate-900 flex items-center justify-center">
        <LoadingState message="Initializing GlobeTrotter..." />
      </div>
    );
  }

  // Reset Password Screen (Accessible publicly with token)
  if (currentTab === 'reset-password') {
    return (
      <div className="min-h-screen bg-[#f5f7f6] text-slate-900 flex flex-col font-sans">
        <Navbar currentTab="login" onNavigate={handleNavigate} />
        <main className="flex-1">
          <ResetPasswordPage token={activeParam ? String(activeParam) : undefined} onNavigate={handleNavigate} />
        </main>
      </div>
    );
  }

  // Public screen (accessible without login)
  if (currentTab === 'public' && activeParam) {
    return (
      <div className="min-h-screen bg-[#f5f7f6] text-slate-900 flex flex-col font-sans">
        <Navbar currentTab={currentTab} onNavigate={handleNavigate} />
        <main className="flex-1">
          <SharedItineraryPage shareToken={String(activeParam)} onNavigate={handleNavigate} />
        </main>
      </div>
    );
  }

  // Unauthenticated users are shown AuthPage
  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-[#f5f7f6] text-slate-900 flex flex-col font-sans">
        <Navbar currentTab="login" onNavigate={handleNavigate} />
        <main className="flex-1">
          <AuthPage onSuccess={() => handleNavigate('dashboard')} />
        </main>
      </div>
    );
  }

  // Render Protected Views for Authenticated Users
  return (
    <div className="min-h-screen bg-[#f5f7f6] text-slate-900 flex flex-col font-sans">
      <Navbar currentTab={currentTab} onNavigate={handleNavigate} />
      <main className="flex-1 pb-12">
        {currentTab === 'dashboard' && <DashboardPage onNavigate={handleNavigate} />}
        {currentTab === 'create-trip' && <CreateTripPage onNavigate={handleNavigate} />}
        {currentTab === 'my-trips' && <MyTripsPage onNavigate={handleNavigate} />}
        {currentTab === 'builder' && activeParam && (
          <ItineraryBuilderPage tripId={Number(activeParam)} onNavigate={handleNavigate} />
        )}
        {currentTab === 'view' && activeParam && (
          <ItineraryViewPage tripId={Number(activeParam)} onNavigate={handleNavigate} />
        )}
        {currentTab === 'city-search' && <CitySearchPage onNavigate={handleNavigate} />}
        {currentTab === 'activity-search' && <ActivitySearchPage onNavigate={handleNavigate} />}
        {currentTab === 'budget' && activeParam && (
          <BudgetPage tripId={Number(activeParam)} onNavigate={handleNavigate} />
        )}
        {currentTab === 'timeline' && activeParam && (
          <TimelinePage tripId={Number(activeParam)} onNavigate={handleNavigate} />
        )}
        {currentTab === 'sharing' && activeParam && (
          <div className="max-w-4xl mx-auto px-4 py-8 space-y-6">
            <div className="flex items-center justify-between">
              <h2 className="text-2xl font-extrabold text-slate-900">Public Sharing Settings</h2>
              <Button
                variant="ghost"
                size="sm"
                icon={<ArrowLeft size={14} />}
                onClick={() => handleNavigate('builder', activeParam)}
              >
                Back to Builder
              </Button>
            </div>
            <SharingSection tripId={Number(activeParam)} />
          </div>
        )}
        {currentTab === 'profile' && <ProfilePage onNavigate={handleNavigate} />}
        {currentTab === 'admin' && <AdminDashboardPage />}
      </main>
    </div>
  );
};

export function App() {
  return (
    <AuthProvider>
      <MainAppContent />
    </AuthProvider>
  );
}

export default App;
