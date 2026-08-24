import React, { useEffect, useState } from 'react';
import { TripResponse } from '../types';
import { api } from '../services/api';
import { Plus, Search, Calendar, MapPin } from 'lucide-react';
import { Button, Input, Card, TripCard, LoadingState, EmptyState } from '../components/common/UIComponents';

interface MyTripsPageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const MyTripsPage: React.FC<MyTripsPageProps> = ({ onNavigate }) => {
  const [trips, setTrips] = useState<TripResponse[]>([]);
  const [search, setSearch] = useState('');
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchTrips();
  }, []);

  const fetchTrips = async () => {
    try {
      setLoading(true);
      const data = await api.getTrips();
      setTrips(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load trips');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (tripId: number, name: string) => {
    if (!window.confirm(`Are you sure you want to delete "${name}"?`)) return;
    try {
      await api.deleteTrip(tripId);
      setTrips((prev) => prev.filter((t) => t.id !== tripId));
    } catch (err: any) {
      alert(err.message || 'Failed to delete trip');
    }
  };

  const filteredTrips = trips.filter(
    (t) =>
      t.name.toLowerCase().includes(search.toLowerCase()) ||
      (t.description && t.description.toLowerCase().includes(search.toLowerCase()))
  );

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-6 border-b border-slate-200">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">My Trips</h1>
          <p className="text-xs text-slate-500 mt-1">
            Manage your travel itineraries, budgets, timeline schedules, and public shares
          </p>
        </div>
        <Button variant="emerald" size="md" icon={<Plus size={16} />} onClick={() => onNavigate('create-trip')}>
          Create New Trip
        </Button>
      </div>

      {/* Filter Bar */}
      <Card className="p-3.5 flex items-center space-x-3 bg-white border border-slate-200">
        <Search size={18} className="text-slate-400 ml-1" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Filter trips by name or description..."
          className="w-full bg-transparent text-slate-900 text-sm focus:outline-none placeholder-slate-400"
        />
      </Card>

      {/* Content Section */}
      {loading ? (
        <LoadingState message="Loading your trips..." />
      ) : error ? (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error}
        </div>
      ) : filteredTrips.length === 0 ? (
        <EmptyState
          title={search ? 'No trips match search' : 'No trips found'}
          description={search ? 'Try clearing your search query.' : 'You haven’t created any travel itineraries yet.'}
          action={
            !search && (
              <Button variant="emerald" size="sm" icon={<Plus size={14} />} onClick={() => onNavigate('create-trip')}>
                Create Trip Now
              </Button>
            )
          }
        />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredTrips.map((trip) => (
            <TripCard key={trip.id} trip={trip} onNavigate={onNavigate} onDelete={handleDelete} />
          ))}
        </div>
      )}
    </div>
  );
};
