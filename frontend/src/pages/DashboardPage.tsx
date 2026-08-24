import { formatCurrency } from '../utils/currencyUtils';
import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { TripResponse, CityResponse } from '../types';
import { api } from '../services/api';
import {
  Compass,
  Plus,
  Calendar,
  DollarSign,
  TrendingUp,
  MapPin,
  Sparkles,
  ArrowRight,
  Shield,
} from 'lucide-react';
import { Button, Card, TripCard, CityCard, LoadingState, EmptyState } from '../components/common/UIComponents';

interface DashboardPageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const DashboardPage: React.FC<DashboardPageProps> = ({ onNavigate }) => {
  const { user } = useAuth();
  const [trips, setTrips] = useState<TripResponse[]>([]);
  const [popularCities, setPopularCities] = useState<CityResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchDashboardData();
  }, []);

  const fetchDashboardData = async () => {
    try {
      setLoading(true);
      const [userTrips, cities] = await Promise.all([
        api.getTrips(),
        api.searchCities().catch(() => []),
      ]);
      setTrips(userTrips);
      setPopularCities(cities.slice(0, 3));
    } catch (err: any) {
      setError(err.message || 'Failed to load dashboard data');
    } finally {
      setLoading(false);
    }
  };

  const activeTrip = trips.length > 0 ? trips[0] : null;
  const totalBudget = trips.reduce((acc, t) => acc + (t.budget || 0), 0);

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Hero Welcome Banner (Light Mode) */}
      <div className="relative rounded-3xl overflow-hidden glass-panel border border-slate-200 p-8 sm:p-10 text-slate-900 shadow-md bg-white">
        <img
          src="https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=1200&q=80"
          alt="Travel Banner"
          className="absolute inset-0 w-full h-full object-cover opacity-15"
        />
        <div className="absolute inset-0 bg-gradient-to-r from-white via-white/90 to-transparent"></div>

        <div className="relative z-10 space-y-4 max-w-2xl">
          <div className="inline-flex items-center space-x-2 px-3 py-1 rounded-full bg-emerald-100 text-emerald-800 border border-emerald-300 text-xs font-semibold">
            <Sparkles size={13} />
            <span>GlobeTrotter Travel Hub</span>
          </div>

          <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight text-slate-900">
            Welcome back, <span className="gradient-text">{user?.name || 'Explorer'}</span> 👋
          </h1>
          <p className="text-slate-600 text-sm sm:text-base leading-relaxed font-medium">
            Curate multi-city itineraries, discover top attraction activities, and keep real-time control of your travel budget.
          </p>

          <div className="pt-2 flex flex-wrap gap-3">
            <Button variant="emerald" size="md" icon={<Plus size={16} />} onClick={() => onNavigate('create-trip')}>
              Create New Trip
            </Button>
            <Button variant="secondary" size="md" icon={<Calendar size={16} />} onClick={() => onNavigate('my-trips')}>
              My Trips ({trips.length})
            </Button>
          </div>
        </div>
      </div>

      {/* Metrics Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-3 gap-6">
        <Card className="flex items-center justify-between p-6 bg-white border border-slate-200 shadow-xs">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Total Trips</p>
            <p className="text-3xl font-extrabold text-slate-900 mt-1">{trips.length}</p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-emerald-50 text-emerald-700 border border-emerald-200 flex items-center justify-center">
            <Calendar size={22} />
          </div>
        </Card>

        <Card className="flex items-center justify-between p-6 bg-white border border-slate-200 shadow-xs">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Active Trip</p>
            <p className="text-lg font-extrabold text-emerald-700 mt-1 truncate max-w-[180px]">
              {activeTrip ? activeTrip.name : 'None'}
            </p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-sky-50 text-sky-700 border border-sky-200 flex items-center justify-center">
            <Compass size={22} />
          </div>
        </Card>

        <Card className="flex items-center justify-between p-6 bg-white border border-slate-200 shadow-xs">
          <div>
            <p className="text-xs font-semibold uppercase tracking-wider text-slate-500">Planned Budget</p>
            <p className="text-3xl font-extrabold text-teal-800 mt-1">{formatCurrency(totalBudget)}</p>
          </div>
          <div className="w-12 h-12 rounded-2xl bg-teal-50 text-teal-700 border border-teal-200 flex items-center justify-center">
            <DollarSign size={22} />
          </div>
        </Card>
      </div>

      {/* Main Content Sections */}
      {loading ? (
        <LoadingState message="Loading dashboard..." />
      ) : error ? (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error}
        </div>
      ) : (
        <div className="space-y-8">
          {/* Recent Trips Section */}
          <div className="space-y-4">
            <div className="flex items-center justify-between">
              <div>
                <h2 className="text-xl font-extrabold text-slate-900 tracking-tight">Recent Trips</h2>
                <p className="text-xs text-slate-500">Your recent travel itineraries & builder progress</p>
              </div>
              <Button variant="ghost" size="sm" icon={<ArrowRight size={14} />} onClick={() => onNavigate('my-trips')}>
                View All
              </Button>
            </div>

            {trips.length === 0 ? (
              <EmptyState
                title="No trips created yet"
                description="Start planning your dream trip by creating your first itinerary!"
                action={
                  <Button variant="emerald" size="sm" icon={<Plus size={14} />} onClick={() => onNavigate('create-trip')}>
                    Create Trip Now
                  </Button>
                }
              />
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {trips.slice(0, 3).map((trip) => (
                  <TripCard key={trip.id} trip={trip} onNavigate={onNavigate} />
                ))}
              </div>
            )}
          </div>

          {/* Popular Destinations Discovery Section */}
          {popularCities.length > 0 && (
            <div className="space-y-4 pt-6 border-t border-slate-200">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-xl font-extrabold text-slate-900 tracking-tight">Featured Destinations</h2>
                  <p className="text-xs text-slate-500">Explore global travel hotspots and cost index ranks</p>
                </div>
                <Button variant="ghost" size="sm" icon={<ArrowRight size={14} />} onClick={() => onNavigate('city-search')}>
                  Browse Cities
                </Button>
              </div>

              <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                {popularCities.map((city) => (
                  <CityCard key={city.id} city={city} onNavigate={onNavigate} />
                ))}
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
};
