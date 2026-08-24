import React, { useState } from 'react';
import { api } from '../services/api';
import { Compass, Calendar, DollarSign, ArrowLeft, Plus } from 'lucide-react';
import { Button, Input, Card } from '../components/common/UIComponents';

interface CreateTripPageProps {
  onNavigate: (tab: string, param?: string | number) => void;
}

export const CreateTripPage: React.FC<CreateTripPageProps> = ({ onNavigate }) => {
  const [name, setName] = useState('');
  const [description, setDescription] = useState('');
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [budget, setBudget] = useState<string>('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (!name.trim()) {
      setError('Trip name is required.');
      return;
    }
    if (!startDate || !endDate) {
      setError('Start date and end date are required.');
      return;
    }
    if (new Date(startDate) > new Date(endDate)) {
      setError('Start date cannot be after end date.');
      return;
    }

    setLoading(true);
    try {
      const newTrip = await api.createTrip({
        name: name.trim(),
        description: description.trim() || undefined,
        startDate,
        endDate,
        budget: budget ? parseFloat(budget) : undefined,
      });

      onNavigate('builder', newTrip.id);
    } catch (err: any) {
      setError(err.message || 'Failed to create trip.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-2xl mx-auto px-4 py-10">
      <Card className="p-8 sm:p-10 space-y-6 bg-white border border-slate-200 shadow-md">
        <div className="flex items-center justify-between border-b border-slate-200 pb-5">
          <div className="flex items-center space-x-3">
            <div className="w-10 h-10 rounded-xl gradient-accent flex items-center justify-center text-white shadow-xs">
              <Compass size={20} />
            </div>
            <div>
              <h1 className="text-2xl font-extrabold text-slate-900">Create New Trip</h1>
              <p className="text-xs text-slate-500 mt-0.5">Define trip dates and budget to start building your itinerary</p>
            </div>
          </div>
          <Button variant="ghost" size="sm" icon={<ArrowLeft size={14} />} onClick={() => onNavigate('dashboard')}>
            Cancel
          </Button>
        </div>

        {error && (
          <div className="p-3.5 bg-rose-50 border border-rose-200 text-rose-800 text-xs rounded-xl font-medium">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-5">
          <Input
            label="Trip Name *"
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="e.g. Summer Euro Trip 2026"
            required
          />

          <div className="space-y-1">
            <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">Description</label>
            <textarea
              value={description}
              onChange={(e) => setDescription(e.target.value)}
              placeholder="Brief summary of your travel goals, destinations, or theme..."
              rows={3}
              className="w-full px-3.5 py-2.5 bg-white border border-slate-300 rounded-xl text-slate-900 text-sm focus:outline-none focus:border-emerald-600 focus:ring-1 focus:ring-emerald-600 transition-all placeholder-slate-400"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <Input
              label="Start Date *"
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              required
            />
            <Input
              label="End Date *"
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              required
            />
          </div>

          <Input
            label="Target Budget (INR ₹)"
            type="number"
            min="0"
            step="50"
            value={budget}
            onChange={(e) => setBudget(e.target.value)}
            placeholder="e.g. 2500"
          />

          <div className="pt-4 border-t border-slate-200 flex items-center justify-end space-x-3">
            <Button type="button" variant="secondary" size="md" onClick={() => onNavigate('dashboard')}>
              Cancel
            </Button>
            <Button type="submit" variant="emerald" size="md" loading={loading} icon={<Plus size={16} />}>
              Create & Build Itinerary →
            </Button>
          </div>
        </form>
      </Card>
    </div>
  );
};
