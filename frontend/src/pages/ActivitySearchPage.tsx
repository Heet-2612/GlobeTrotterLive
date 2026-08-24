import React, { useEffect, useState } from 'react';
import { ActivityResponse } from '../types';
import { api } from '../services/api';
import { Search, Sparkles } from 'lucide-react';
import { Button, Input, Card, ActivityCard, LoadingState, EmptyState } from '../components/common/UIComponents';

interface ActivitySearchPageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const ActivitySearchPage: React.FC<ActivitySearchPageProps> = ({ onNavigate }) => {
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');
  const [activities, setActivities] = useState<ActivityResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchActivities();
  }, []);

  const fetchActivities = async (query?: string, categoryFilter?: string) => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.searchActivities(undefined, query, categoryFilter);
      setActivities(data);
    } catch (err: any) {
      setError(err.message || 'Failed to search activities');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchActivities(search, category);
  };

  const categories = ['All', 'SIGHTSEEING', 'FOOD', 'ADVENTURE', 'CULTURE', 'NIGHTLIFE', 'SHOPPING'];

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-6 border-b border-slate-200">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Discover Activities & Attractions</h1>
          <p className="text-xs text-slate-500 mt-1">
            Browse tours, landmark visits, food experiences, and outdoor adventures
          </p>
        </div>
      </div>

      {/* Category Pills & Search */}
      <div className="space-y-3">
        <Card className="p-4 bg-white border border-slate-200 shadow-xs">
          <form onSubmit={handleSearchSubmit} className="flex flex-col sm:flex-row gap-3">
            <div className="flex-1">
              <Input
                type="text"
                value={search}
                onChange={(e) => setSearch(e.target.value)}
                placeholder="Search activities (e.g. Eiffel Tower, Wine Tasting, Museum)..."
              />
            </div>
            <Button type="submit" variant="emerald" size="md" icon={<Search size={16} />}>
              Search
            </Button>
          </form>
        </Card>

        {/* Category Pills */}
        <div className="flex flex-wrap gap-2 pt-1">
          {categories.map((cat) => {
            const isSelected = (cat === 'All' && !category) || category === cat;
            return (
              <button
                key={cat}
                type="button"
                onClick={() => {
                  const selectedCat = cat === 'All' ? '' : cat;
                  setCategory(selectedCat);
                  fetchActivities(search, selectedCat);
                }}
                className={`px-3.5 py-1.5 rounded-full text-xs font-semibold transition-all border ${
                  isSelected
                    ? 'bg-emerald-600 text-white border-emerald-500 shadow-xs'
                    : 'bg-white text-slate-700 hover:text-slate-900 hover:bg-slate-100 border-slate-200'
                }`}
              >
                {cat}
              </button>
            );
          })}
        </div>
      </div>

      {/* Results Grid */}
      {loading ? (
        <LoadingState message="Searching activities..." />
      ) : error ? (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error}
        </div>
      ) : activities.length === 0 ? (
        <EmptyState title="No activities found" description="Try selecting a different category or search term." />
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {activities.map((act) => (
            <ActivityCard key={act.id} activity={act} />
          ))}
        </div>
      )}
    </div>
  );
};
