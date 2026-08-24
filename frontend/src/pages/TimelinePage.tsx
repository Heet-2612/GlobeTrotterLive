import { formatCurrency } from '../utils/currencyUtils';
import React, { useEffect, useState } from 'react';
import { TripResponse, TripStopResponse, TripActivityResponse } from '../types';
import { api } from '../services/api';
import { Calendar, Clock, Edit3, Eye, DollarSign, MapPin } from 'lucide-react';
import { Button, Card, Badge, LoadingState } from '../components/common/UIComponents';

interface TimelinePageProps {
  tripId: number;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const TimelinePage: React.FC<TimelinePageProps> = ({ tripId, onNavigate }) => {
  const [trip, setTrip] = useState<TripResponse | null>(null);
  const [stops, setStops] = useState<TripStopResponse[]>([]);
  const [activitiesMap, setActivitiesMap] = useState<Record<number, TripActivityResponse[]>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTimeline();
  }, [tripId]);

  const loadTimeline = async () => {
    try {
      setLoading(true);
      setError(null);
      const tripData = await api.getTripById(tripId);
      setTrip(tripData);

      const stopsData = await api.getTripStops(tripId);
      setStops(stopsData);

      const map: Record<number, TripActivityResponse[]> = {};
      for (const stop of stopsData) {
        try {
          const acts = await api.getTripActivities(tripId, stop.id);
          map[stop.id] = acts;
        } catch {
          map[stop.id] = [];
        }
      }
      setActivitiesMap(map);
    } catch (err: any) {
      setError(err.message || 'Failed to load timeline');
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return <LoadingState message="Loading trip timeline..." />;
  }

  if (error || !trip) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error || 'Trip not found.'}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Header Bar */}
      <Card className="p-6 space-y-4 bg-white border border-slate-200 shadow-sm">
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <span className="text-xs font-semibold text-emerald-700 uppercase tracking-wider">
              Chronological Travel Timeline
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
            <Button variant="secondary" size="sm" icon={<DollarSign size={14} />} onClick={() => onNavigate('budget', tripId)}>
              Budget
            </Button>
            <Button variant="emerald" size="sm" icon={<Clock size={14} />} onClick={() => onNavigate('timeline', tripId)}>
              Timeline
            </Button>
          </div>
        </div>
      </Card>

      {/* Vertical Timeline Section */}
      <div className="space-y-6">
        <h2 className="text-xl font-extrabold text-slate-900">Day-by-Day Timeline</h2>

        {stops.length === 0 ? (
          <Card className="p-8 text-center text-slate-500 bg-white border border-slate-200">
            No stops or scheduled events in timeline yet.
          </Card>
        ) : (
          <div className="relative border-l-2 border-emerald-400/60 pl-6 ml-4 space-y-8">
            {stops.map((stop, idx) => {
              const stopActivities = activitiesMap[stop.id] || [];
              return (
                <div key={stop.id} className="relative group">
                  {/* Timeline Dot Marker */}
                  <div className="absolute -left-[31px] top-1.5 w-4 h-4 rounded-full bg-emerald-600 border-2 border-white shadow-xs"></div>

                  <Card className="p-5 space-y-3 bg-white border border-slate-200 shadow-xs">
                    <div className="flex justify-between items-start">
                      <div>
                        <span className="text-[10px] font-extrabold uppercase tracking-wider text-emerald-700">
                          Stop #{idx + 1}
                        </span>
                        <h3 className="text-lg font-bold text-slate-900">
                          {stop.city.name}, {stop.city.country}
                        </h3>
                        <p className="text-xs text-slate-500">📅 {stop.startDate} - {stop.endDate}</p>
                      </div>
                    </div>

                    <div className="pt-2 border-t border-slate-100 space-y-2">
                      <p className="text-xs font-semibold text-slate-500 uppercase tracking-wider">Scheduled Activities</p>
                      {stopActivities.length === 0 ? (
                        <p className="text-xs text-slate-500 italic">No scheduled activities for this stop.</p>
                      ) : (
                        <div className="space-y-2">
                          {stopActivities.map((act) => (
                            <div
                              key={act.id}
                              className="bg-slate-50 border border-slate-200 rounded-xl p-3 text-xs flex items-center justify-between"
                            >
                              <div className="space-y-0.5">
                                <span className="font-bold text-slate-900 block text-sm">{act.activity.name}</span>
                                <span className="text-slate-600 flex items-center space-x-1">
                                  <Calendar size={12} className="text-emerald-600" />
                                  <span>{act.scheduledDate} {act.startTime && `• ⏰ ${act.startTime}`}</span>
                                </span>
                              </div>
                              <span className="font-bold text-emerald-700 text-sm">
                                {formatCurrency(act.customCost ?? act.activity.estimatedCost ?? 0)}
                              </span>
                            </div>
                          ))}
                        </div>
                      )}
                    </div>
                  </Card>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
};
