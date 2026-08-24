import React, { useEffect, useState } from 'react';
import { CityResponse } from '../types';
import { api } from '../services/api';
import { Search, MapPin, Sparkles } from 'lucide-react';
import { Button, Input, Card, CityCard, LoadingState, EmptyState } from '../components/common/UIComponents';

interface CitySearchPageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const CitySearchPage: React.FC<CitySearchPageProps> = ({ onNavigate }) => {
  const [search, setSearch] = useState('');
  const [country, setCountry] = useState('');
  const [cities, setCities] = useState<CityResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetchCities();
  }, []);

  const fetchCities = async (query?: string, countryFilter?: string) => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.searchCities(query, countryFilter);
      setCities(data);
    } catch (err: any) {
      setError(err.message || 'Failed to search cities');
    } finally {
      setLoading(false);
    }
  };

  const handleSearchSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    fetchCities(search, country);
  };

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 pb-6 border-b border-slate-200">
        <div>
          <h1 className="text-3xl font-extrabold text-slate-900 tracking-tight">Explore Destination Cities</h1>
          <p className="text-xs text-slate-500 mt-1">
            Browse global travel destinations, popularity indices, and relative cost metrics
          </p>
        </div>
      </div>

      {/* Integrated Search Bar Form */}
      <Card className="p-4 bg-white border border-slate-200 shadow-xs">
        <form onSubmit={handleSearchSubmit} className="grid grid-cols-1 sm:grid-cols-3 gap-3">
          <Input
            type="text"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            placeholder="Search by city name (e.g. Paris, Tokyo)..."
          />
          <Input
            type="text"
            value={country}
            onChange={(e) => setCountry(e.target.value)}
            placeholder="Filter by country (e.g. France, Japan)..."
          />
          <Button type="submit" variant="emerald" size="md" icon={<Search size={16} />}>
            Search Cities
          </Button>
        </form>
      </Card>

      {/* Results Section */}
      {loading ? (
        <LoadingState message="Searching cities..." />
      ) : error ? (
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error}
        </div>
      ) : cities.length === 0 ? (
        <EmptyState title="No cities found" description="Try broadening your search query or country filter." />
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {cities.map((city) => (
            <CityCard key={city.id} city={city} onNavigate={onNavigate} />
          ))}
        </div>
      )}
    </div>
  );
};
