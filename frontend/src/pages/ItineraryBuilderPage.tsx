import { formatCurrency } from '../utils/currencyUtils';
import React, { useEffect, useState } from 'react';
import {
  TripResponse,
  TripStopResponse,
  CityResponse,
  ActivityResponse,
  TripActivityResponse,
} from '../types';
import { api } from '../services/api';
import {
  MapPin,
  Calendar,
  Plus,
  Trash2,
  Clock,
  DollarSign,
  Share2,
  Eye,
  Edit3,
  X,
  Search,
  Sparkles,
  ArrowLeft,
} from 'lucide-react';
import { Button, Card, Badge, Input, LoadingState } from '../components/common/UIComponents';
import { getCityImageUrl, getActivityImageUrl } from '../utils/imageUtils';

interface ItineraryBuilderPageProps {
  tripId: number;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const ItineraryBuilderPage: React.FC<ItineraryBuilderPageProps> = ({ tripId, onNavigate }) => {
  const [trip, setTrip] = useState<TripResponse | null>(null);
  const [stops, setStops] = useState<TripStopResponse[]>([]);
  const [activitiesMap, setActivitiesMap] = useState<Record<number, TripActivityResponse[]>>({});
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Add Stop Modal State
  const [showAddStopModal, setShowAddStopModal] = useState(false);
  const [citySearchQuery, setCitySearchQuery] = useState('');
  const [citySearchResults, setCitySearchResults] = useState<CityResponse[]>([]);
  const [selectedCity, setSelectedCity] = useState<CityResponse | null>(null);
  const [stopStartDate, setStopStartDate] = useState('');
  const [stopEndDate, setStopEndDate] = useState('');
  const [stopNotes, setStopNotes] = useState('');
  const [stopSubmitting, setStopSubmitting] = useState(false);

  // Add Activity Modal State
  const [activeStopForActivity, setActiveStopForActivity] = useState<TripStopResponse | null>(null);
  const [activitySearchQuery, setActivitySearchQuery] = useState('');
  const [activitySearchResults, setActivitySearchResults] = useState<ActivityResponse[]>([]);
  const [selectedActivity, setSelectedActivity] = useState<ActivityResponse | null>(null);
  const [scheduledDate, setScheduledDate] = useState('');
  const [startTime, setStartTime] = useState('');
  const [activityNotes, setActivityNotes] = useState('');
  const [customCost, setCustomCost] = useState<string>('');
  const [activitySubmitting, setActivitySubmitting] = useState(false);

  useEffect(() => {
    loadTripData();
  }, [tripId]);

  const loadTripData = async () => {
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
      setError(err.message || 'Failed to load trip builder data');
    } finally {
      setLoading(false);
    }
  };

  // City Search handler
  useEffect(() => {
    if (!citySearchQuery.trim()) {
      setCitySearchResults([]);
      return;
    }
    const timer = setTimeout(async () => {
      try {
        const results = await api.searchCities(citySearchQuery);
        setCitySearchResults(results);
      } catch {
        setCitySearchResults([]);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [citySearchQuery]);

  // Activity Search handler
  useEffect(() => {
    if (!activeStopForActivity) return;
    const timer = setTimeout(async () => {
      try {
        const results = await api.searchActivities(
          activeStopForActivity.city.id,
          activitySearchQuery || undefined
        );
        setActivitySearchResults(results);
      } catch {
        setActivitySearchResults([]);
      }
    }, 300);
    return () => clearTimeout(timer);
  }, [activitySearchQuery, activeStopForActivity]);

  const handleAddStop = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedCity || !stopStartDate || !stopEndDate) {
      alert('Please select a city and valid start/end dates.');
      return;
    }
    setStopSubmitting(true);
    try {
      await api.createTripStop(tripId, {
        cityId: selectedCity.id,
        startDate: stopStartDate,
        endDate: stopEndDate,
        notes: stopNotes.trim() || undefined,
      });
      setShowAddStopModal(false);
      setSelectedCity(null);
      setCitySearchQuery('');
      setStopStartDate('');
      setStopEndDate('');
      setStopNotes('');
      await loadTripData();
    } catch (err: any) {
      alert(err.message || 'Failed to add stop');
    } finally {
      setStopSubmitting(false);
    }
  };

  const handleDeleteStop = async (stopId: number) => {
    if (!window.confirm('Delete this stop and all its scheduled activities?')) return;
    try {
      await api.deleteTripStop(tripId, stopId);
      await loadTripData();
    } catch (err: any) {
      alert(err.message || 'Failed to delete stop');
    }
  };

  const handleAddActivity = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!activeStopForActivity || !selectedActivity || !scheduledDate) {
      alert('Please select an activity and scheduled date.');
      return;
    }
    setActivitySubmitting(true);
    try {
      await api.createTripActivity(tripId, activeStopForActivity.id, {
        activityId: selectedActivity.id,
        scheduledDate,
        startTime: startTime ? (startTime.length === 5 ? `${startTime}:00` : startTime) : undefined,
        notes: activityNotes.trim() || undefined,
        customCost: customCost ? parseFloat(customCost) : undefined,
      });
      setActiveStopForActivity(null);
      setSelectedActivity(null);
      setActivitySearchQuery('');
      setScheduledDate('');
      setStartTime('');
      setActivityNotes('');
      setCustomCost('');
      await loadTripData();
    } catch (err: any) {
      alert(err.message || 'Failed to add activity');
    } finally {
      setActivitySubmitting(false);
    }
  };

  const handleDeleteActivity = async (stopId: number, tripActivityId: number) => {
    if (!window.confirm('Remove this activity from stop?')) return;
    try {
      await api.deleteTripActivity(tripId, stopId, tripActivityId);
      await loadTripData();
    } catch (err: any) {
      alert(err.message || 'Failed to delete activity');
    }
  };

  if (loading) {
    return <LoadingState message="Loading itinerary builder..." />;
  }

  if (error || !trip) {
    return (
      <div className="max-w-4xl mx-auto py-12 px-4">
        <div className="p-4 bg-rose-50 border border-rose-200 rounded-2xl text-rose-800 text-sm">
          {error || 'Trip not found.'}
        </div>
        <Button variant="ghost" size="sm" icon={<ArrowLeft size={14} />} onClick={() => onNavigate('my-trips')} className="mt-4">
          Back to My Trips
        </Button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-6">
      {/* Header View Switcher Card */}
      <Card className="p-6 sm:p-8 space-y-4 bg-white border border-slate-200 shadow-sm">
        <div className="flex flex-col lg:flex-row lg:items-center lg:justify-between gap-4">
          <div>
            <div className="flex items-center space-x-2 text-xs font-semibold text-emerald-700 uppercase tracking-wider mb-1">
              <Sparkles size={14} />
              <span>Itinerary Builder • Trip #{trip.id}</span>
            </div>
            <h1 className="text-2xl sm:text-3xl font-extrabold text-slate-900">{trip.name}</h1>
            <p className="text-xs sm:text-sm text-slate-600 mt-1">{trip.description || 'No description provided.'}</p>
            <div className="flex items-center space-x-4 text-xs text-slate-600 mt-2">
              <span className="flex items-center space-x-1">
                <Calendar size={13} className="text-emerald-600" />
                <span>{trip.startDate} - {trip.endDate}</span>
              </span>
              <span className="flex items-center space-x-1">
                <DollarSign size={13} className="text-emerald-600" />
                <span>Budget: {trip.budget ? formatCurrency(trip.budget) : formatCurrency(0)}</span>
              </span>
            </div>
          </div>

          {/* View Toolbar Switcher */}
          <div className="flex flex-wrap gap-2 pt-2 lg:pt-0">
            <Button variant="emerald" size="sm" icon={<Edit3 size={14} />} onClick={() => onNavigate('builder', tripId)}>
              Builder
            </Button>
            <Button variant="secondary" size="sm" icon={<Eye size={14} />} onClick={() => onNavigate('view', tripId)}>
              Read View
            </Button>
            <Button variant="secondary" size="sm" icon={<DollarSign size={14} />} onClick={() => onNavigate('budget', tripId)}>
              Budget
            </Button>
            <Button variant="secondary" size="sm" icon={<Clock size={14} />} onClick={() => onNavigate('timeline', tripId)}>
              Timeline
            </Button>
            <Button variant="secondary" size="sm" icon={<Share2 size={14} />} onClick={() => onNavigate('sharing', tripId)}>
              Share
            </Button>
          </div>
        </div>
      </Card>

      {/* Action Header */}
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-extrabold text-slate-900">Trip Destinations ({stops.length} Stops)</h2>
        <Button variant="emerald" size="sm" icon={<Plus size={14} />} onClick={() => setShowAddStopModal(true)}>
          Add City Stop
        </Button>
      </div>

      {/* Stops List */}
      {stops.length === 0 ? (
        <Card className="p-10 text-center text-slate-500 space-y-3 bg-white border border-slate-200">
          <MapPin size={32} className="mx-auto text-emerald-600 opacity-60" />
          <h3 className="text-base font-bold text-slate-900">No stops added yet</h3>
          <p className="text-xs text-slate-500">Add your first destination city to start scheduling activities!</p>
          <Button variant="emerald" size="sm" icon={<Plus size={14} />} onClick={() => setShowAddStopModal(true)}>
            Add First Stop
          </Button>
        </Card>
      ) : (
        <div className="space-y-6">
          {stops.map((stop, index) => {
            const stopActivities = activitiesMap[stop.id] || [];
            const cityImage = getCityImageUrl(stop.city.name, stop.city.imageUrl);

            return (
              <Card key={stop.id} className="p-6 space-y-4 shadow-sm bg-white border border-slate-200">
                <div className="flex flex-col sm:flex-row sm:items-center justify-between border-b border-slate-200 pb-4 gap-4">
                  <div className="flex items-center space-x-4">
                    <div className="w-12 h-12 rounded-xl overflow-hidden shrink-0 relative border border-slate-200">
                      <img src={cityImage} alt={stop.city.name} className="w-full h-full object-cover" />
                      <div className="absolute inset-0 bg-slate-900/30"></div>
                      <span className="absolute inset-0 flex items-center justify-center font-extrabold text-white text-sm drop-shadow">
                        #{index + 1}
                      </span>
                    </div>
                    <div>
                      <h3 className="text-xl font-bold text-slate-900">
                        {stop.city.name}, <span className="text-slate-500 text-sm font-normal">{stop.city.country}</span>
                      </h3>
                      <p className="text-xs text-slate-500 mt-0.5">
                        📅 {stop.startDate} to {stop.endDate} {stop.notes && `• ${stop.notes}`}
                      </p>
                    </div>
                  </div>

                  <div className="flex items-center space-x-2">
                    <Button
                      variant="secondary"
                      size="sm"
                      icon={<Plus size={13} />}
                      onClick={() => {
                        setActiveStopForActivity(stop);
                        setScheduledDate(stop.startDate);
                      }}
                    >
                      Add Activity
                    </Button>
                    <button
                      onClick={() => handleDeleteStop(stop.id)}
                      className="p-2 text-rose-600 hover:text-rose-700 hover:bg-rose-50 rounded-xl transition-colors"
                      title="Delete Stop"
                    >
                      <Trash2 size={16} />
                    </button>
                  </div>
                </div>

                {/* Stop Activities Section */}
                <div className="space-y-3 pl-2 sm:pl-4 border-l-2 border-slate-200">
                  <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                    Scheduled Activities ({stopActivities.length})
                  </h4>

                  {stopActivities.length === 0 ? (
                    <p className="text-xs text-slate-500 italic">No activities added to this stop yet.</p>
                  ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-3">
                      {stopActivities.map((act) => {
                        const actImg = getActivityImageUrl(act.activity.category, act.activity.imageUrl);
                        return (
                          <div
                            key={act.id}
                            className="bg-slate-50 border border-slate-200 rounded-xl p-3.5 space-y-2 text-xs relative group hover:border-emerald-500/50 transition-all"
                          >
                            <div className="flex items-start justify-between">
                              <span className="font-bold text-slate-900 text-sm line-clamp-1">{act.activity.name}</span>
                              <button
                                onClick={() => handleDeleteActivity(stop.id, act.id)}
                                className="text-rose-600 hover:text-rose-700 p-0.5 rounded transition-colors"
                              >
                                <X size={14} />
                              </button>
                            </div>

                            <Badge variant="emerald">{act.activity.category || 'Sightseeing'}</Badge>

                            <p className="text-slate-600 flex items-center space-x-1 pt-1">
                              <Calendar size={12} className="text-emerald-600" />
                              <span>{act.scheduledDate} {act.startTime && `at ${act.startTime}`}</span>
                            </p>

                            <div className="pt-1 flex items-center justify-between border-t border-slate-200">
                              <span className="text-slate-500">Cost:</span>
                              <span className="font-bold text-emerald-700">
                                {formatCurrency(act.customCost ?? act.activity.estimatedCost ?? 0)}
                              </span>
                            </div>
                          </div>
                        );
                      })}
                    </div>
                  )}
                </div>
              </Card>
            );
          })}
        </div>
      )}

      {/* Add Stop Modal */}
      {showAddStopModal && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <Card className="max-w-md w-full p-6 space-y-4 shadow-xl bg-white border border-slate-200">
            <div className="flex items-center justify-between border-b border-slate-200 pb-3">
              <h3 className="text-lg font-bold text-slate-900">Add City Stop</h3>
              <button onClick={() => setShowAddStopModal(false)} className="text-slate-500 hover:text-slate-900">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddStop} className="space-y-4">
              <div className="space-y-1">
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">Search City *</label>
                <Input
                  type="text"
                  value={citySearchQuery}
                  onChange={(e) => setCitySearchQuery(e.target.value)}
                  placeholder="e.g. Paris, Tokyo, London..."
                />
                {citySearchResults.length > 0 && (
                  <div className="mt-1 max-h-40 overflow-y-auto bg-white border border-slate-200 rounded-xl divide-y divide-slate-100 shadow-md">
                    {citySearchResults.map((city) => (
                      <div
                        key={city.id}
                        onClick={() => {
                          setSelectedCity(city);
                          setCitySearchQuery(`${city.name}, ${city.country}`);
                          setCitySearchResults([]);
                        }}
                        className="p-2.5 hover:bg-slate-50 cursor-pointer text-xs text-slate-900 flex justify-between font-medium"
                      >
                        <span>{city.name}, {city.country}</span>
                        <span className="text-slate-500">{city.region || ''}</span>
                      </div>
                    ))}
                  </div>
                )}
                {selectedCity && (
                  <p className="text-xs text-emerald-700 font-semibold mt-1">
                    ✓ Selected: {selectedCity.name}, {selectedCity.country}
                  </p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Start Date *"
                  type="date"
                  value={stopStartDate}
                  onChange={(e) => setStopStartDate(e.target.value)}
                  required
                />
                <Input
                  label="End Date *"
                  type="date"
                  value={stopEndDate}
                  onChange={(e) => setStopEndDate(e.target.value)}
                  required
                />
              </div>

              <Input
                label="Notes"
                type="text"
                value={stopNotes}
                onChange={(e) => setStopNotes(e.target.value)}
                placeholder="e.g. Hotel reservation near city center"
              />

              <div className="flex items-center justify-end space-x-2 pt-3 border-t border-slate-200">
                <Button type="button" variant="secondary" size="sm" onClick={() => setShowAddStopModal(false)}>
                  Cancel
                </Button>
                <Button type="submit" variant="emerald" size="sm" loading={stopSubmitting} disabled={!selectedCity}>
                  Add Stop
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}

      {/* Add Activity Modal */}
      {activeStopForActivity && (
        <div className="fixed inset-0 bg-slate-900/50 backdrop-blur-xs z-50 flex items-center justify-center p-4">
          <Card className="max-w-md w-full p-6 space-y-4 shadow-xl bg-white border border-slate-200">
            <div className="flex items-center justify-between border-b border-slate-200 pb-3">
              <h3 className="text-lg font-bold text-slate-900">
                Add Activity to {activeStopForActivity.city.name}
              </h3>
              <button onClick={() => setActiveStopForActivity(null)} className="text-slate-500 hover:text-slate-900">
                <X size={18} />
              </button>
            </div>

            <form onSubmit={handleAddActivity} className="space-y-4">
              <div className="space-y-1">
                <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">Search Activity *</label>
                <Input
                  type="text"
                  value={activitySearchQuery}
                  onChange={(e) => setActivitySearchQuery(e.target.value)}
                  placeholder="e.g. Museum, Tour, Wine Tasting..."
                />
                {activitySearchResults.length > 0 && (
                  <div className="mt-1 max-h-40 overflow-y-auto bg-white border border-slate-200 rounded-xl divide-y divide-slate-100 shadow-md">
                    {activitySearchResults.map((act) => (
                      <div
                        key={act.id}
                        onClick={() => {
                          setSelectedActivity(act);
                          setActivitySearchQuery(act.name);
                          setCustomCost(act.estimatedCost ? act.estimatedCost.toString() : '0');
                          setActivitySearchResults([]);
                        }}
                        className="p-2.5 hover:bg-slate-50 cursor-pointer text-xs text-slate-900 flex justify-between font-medium"
                      >
                        <span>{act.name} ({act.category || 'General'})</span>
                        <span className="text-emerald-700 font-semibold">{formatCurrency(act.estimatedCost || 0)}</span>
                      </div>
                    ))}
                  </div>
                )}
                {selectedActivity && (
                  <p className="text-xs text-emerald-700 font-semibold mt-1">
                    ✓ Selected: {selectedActivity.name} ({formatCurrency(selectedActivity.estimatedCost || 0)})
                  </p>
                )}
              </div>

              <div className="grid grid-cols-2 gap-3">
                <Input
                  label="Scheduled Date *"
                  type="date"
                  value={scheduledDate}
                  onChange={(e) => setScheduledDate(e.target.value)}
                  required
                />
                <Input
                  label="Start Time"
                  type="time"
                  value={startTime}
                  onChange={(e) => setStartTime(e.target.value)}
                />
              </div>

              <Input
                label="Custom Cost (INR ?)"
                type="number"
                min="0"
                step="5"
                value={customCost}
                onChange={(e) => setCustomCost(e.target.value)}
                placeholder="0"
              />

              <Input
                label="Notes"
                type="text"
                value={activityNotes}
                onChange={(e) => setActivityNotes(e.target.value)}
                placeholder="e.g. Online voucher booked"
              />

              <div className="flex items-center justify-end space-x-2 pt-3 border-t border-slate-200">
                <Button type="button" variant="secondary" size="sm" onClick={() => setActiveStopForActivity(null)}>
                  Cancel
                </Button>
                <Button type="submit" variant="emerald" size="sm" loading={activitySubmitting} disabled={!selectedActivity}>
                  Assign Activity
                </Button>
              </div>
            </form>
          </Card>
        </div>
      )}
    </div>
  );
};

