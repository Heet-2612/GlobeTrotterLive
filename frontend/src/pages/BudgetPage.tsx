import React, { useEffect, useState } from 'react';
import { BudgetSummaryResponse, TripResponse } from '../types';
import { api } from '../services/api';
import { DollarSign, AlertTriangle, TrendingUp, Edit3, Eye, Clock, Calendar } from 'lucide-react';
import { Button, Card, Input, LoadingState } from '../components/common/UIComponents';
import { formatCurrency } from '../utils/currencyUtils';

interface BudgetPageProps {
  tripId: number;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const BudgetPage: React.FC<BudgetPageProps> = ({ tripId, onNavigate }) => {
  const [trip, setTrip] = useState<TripResponse | null>(null);
  const [budgetSummary, setBudgetSummary] = useState<BudgetSummaryResponse | null>(null);
  const [newBudget, setNewBudget] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [successMsg, setSuccessMsg] = useState<string | null>(null);

  useEffect(() => {
    loadBudgetData();
  }, [tripId]);

  const loadBudgetData = async () => {
    try {
      setLoading(true);
      setError(null);
      const tripData = await api.getTripById(tripId);
      setTrip(tripData);

      const summary = await api.getBudgetSummary(tripId);
      setBudgetSummary(summary);
      setNewBudget(summary.budget ? summary.budget.toString() : '0');
    } catch (err: any) {
      setError(err.message || 'Failed to load budget details');
    } finally {
      setLoading(false);
    }
  };

  const handleUpdateBudget = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);
    setSuccessMsg(null);
    setUpdating(true);
    try {
      const updated = await api.updateBudget(tripId, {
        budget: parseFloat(newBudget) || 0,
      });
      setBudgetSummary(updated);
      setSuccessMsg('Target budget updated successfully!');
      setTimeout(() => setSuccessMsg(null), 3000);
    } catch (err: any) {
      setError(err.message || 'Failed to update budget');
    } finally {
      setUpdating(false);
    }
  };

  if (loading) {
    return <LoadingState message="Loading budget summary..." />;
  }

  if (error || !trip || !budgetSummary) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error || 'Budget information not found.'}
        </div>
      </div>
    );
  }

  const isExceeded = budgetSummary.budgetExceeded;
  const percentage = Math.min(Math.max(budgetSummary.budgetUsedPercentage || 0, 0), 100);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header Bar */}
      <Card className="p-6 space-y-4 bg-white border border-slate-200 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <span className="text-xs font-semibold text-emerald-700 uppercase tracking-wider">
              Financial Dashboard
            </span>
            <h1 className="text-2xl font-extrabold text-slate-900 mt-1">{trip.name}</h1>
            <p className="text-xs text-slate-500">📅 {trip.startDate} to {trip.endDate}</p>
          </div>

          <div className="flex flex-wrap gap-2">
            <Button variant="secondary" size="sm" icon={<Edit3 size={14} />} onClick={() => onNavigate('builder', tripId)}>
              Builder
            </Button>
            <Button variant="secondary" size="sm" icon={<Eye size={14} />} onClick={() => onNavigate('view', tripId)}>
              Read View
            </Button>
            <Button variant="emerald" size="sm" icon={<DollarSign size={14} />} onClick={() => onNavigate('budget', tripId)}>
              Budget
            </Button>
            <Button variant="secondary" size="sm" icon={<Clock size={14} />} onClick={() => onNavigate('timeline', tripId)}>
              Timeline
            </Button>
          </div>
        </div>
      </Card>

      {/* Exceeded Alert Banner */}
      {isExceeded && (
        <div className="bg-rose-50 border border-rose-200 p-4 rounded-2xl text-rose-900 text-sm flex items-center space-x-3 shadow-xs">
          <AlertTriangle size={24} className="text-rose-600 shrink-0" />
          <div>
            <p className="font-bold text-rose-950">Budget Exceeded Alert!</p>
            <p className="text-xs text-rose-700">
              Total activity costs ({formatCurrency(budgetSummary.totalActivityCost)}) exceed your target budget ({formatCurrency(budgetSummary.budget)}).
            </p>
          </div>
        </div>
      )}

      {/* Metric Cards Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-4 gap-4">
        <Card className="p-5 bg-white border border-slate-200 shadow-xs">
          <p className="text-xs font-semibold text-slate-500 uppercase">Target Budget</p>
          <p className="text-2xl font-extrabold text-slate-900 mt-1">{formatCurrency(budgetSummary.budget)}</p>
        </Card>
        <Card className="p-5 bg-white border border-slate-200 shadow-xs">
          <p className="text-xs font-semibold text-slate-500 uppercase">Total Spent</p>
          <p className="text-2xl font-extrabold text-amber-700 mt-1">{formatCurrency(budgetSummary.totalActivityCost)}</p>
        </Card>
        <Card className="p-5 bg-white border border-slate-200 shadow-xs">
          <p className="text-xs font-semibold text-slate-500 uppercase">Remaining</p>
          <p className={`text-2xl font-extrabold mt-1 ${budgetSummary.remainingBudget < 0 ? 'text-rose-600' : 'text-emerald-700'}`}>
            {formatCurrency(budgetSummary.remainingBudget)}
          </p>
        </Card>
        <Card className="p-5 bg-white border border-slate-200 shadow-xs">
          <p className="text-xs font-semibold text-slate-500 uppercase">% Used</p>
          <p className="text-2xl font-extrabold text-teal-700 mt-1">{budgetSummary.budgetUsedPercentage}%</p>
        </Card>
      </div>

      {/* Progress Bar */}
      <Card className="p-6 space-y-3 bg-white border border-slate-200 shadow-xs">
        <div className="flex justify-between text-xs text-slate-700 font-semibold">
          <span>Budget Usage Progress</span>
          <span className="text-emerald-700">{budgetSummary.budgetUsedPercentage}% Used</span>
        </div>
        <div className="w-full bg-slate-100 rounded-full h-4 overflow-hidden p-0.5 border border-slate-200">
          <div
            className={`h-full rounded-full transition-all duration-500 ${
              isExceeded ? 'bg-rose-600' : 'gradient-accent'
            }`}
            style={{ width: `${percentage}%` }}
          ></div>
        </div>
      </Card>

      {/* Form & Table */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-8">
        {/* Update Form */}
        <Card className="p-6 space-y-4 bg-white border border-slate-200 shadow-xs">
          <h3 className="text-lg font-bold text-slate-900">Update Target Budget</h3>
          {successMsg && <p className="text-xs text-emerald-700 font-semibold">{successMsg}</p>}
          <form onSubmit={handleUpdateBudget} className="space-y-4">
            <Input
              label="New Target Budget (INR ₹)"
              type="number"
              min="0"
              step="50"
              value={newBudget}
              onChange={(e) => setNewBudget(e.target.value)}
              required
            />
            <Button type="submit" variant="emerald" size="md" loading={updating} className="w-full">
              Save New Budget
            </Button>
          </form>
        </Card>

        {/* Category Breakdown Table */}
        <Card className="lg:col-span-2 p-6 space-y-4 bg-white border border-slate-200 shadow-xs">
          <h3 className="text-lg font-bold text-slate-900">Category Cost Breakdown</h3>
          {!budgetSummary.categoryBreakdown || budgetSummary.categoryBreakdown.length === 0 ? (
            <p className="text-xs text-slate-500 italic">No scheduled activities with costs yet.</p>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-left text-xs text-slate-700">
                <thead className="bg-slate-50 text-slate-700 uppercase font-semibold border-b border-slate-200">
                  <tr>
                    <th className="p-3">Category</th>
                    <th className="p-3 text-center">Activities Count</th>
                    <th className="p-3 text-right">Total Cost (INR ₹)</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-slate-100">
                  {budgetSummary.categoryBreakdown.map((cat, i) => (
                    <tr key={i} className="hover:bg-slate-50">
                      <td className="p-3 font-bold text-slate-900">{cat.category}</td>
                      <td className="p-3 text-center">{cat.count}</td>
                      <td className="p-3 text-right font-bold text-emerald-700">{formatCurrency(cat.totalCost)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </Card>
      </div>
    </div>
  );
};


