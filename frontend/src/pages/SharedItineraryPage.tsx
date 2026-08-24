import { formatCurrency } from '../utils/currencyUtils';
import React, { useEffect, useState } from 'react';
import { PublicTripItineraryResponse } from '../types';
import { api } from '../services/api';
import { useAuth } from '../context/AuthContext';
import { Globe, Calendar, DollarSign, Copy, Check, Sparkles, MapPin } from 'lucide-react';
import { Button, Card, Badge, LoadingState } from '../components/common/UIComponents';
import { getTripCoverUrl, getCityImageUrl } from '../utils/imageUtils';

interface SharedItineraryPageProps {
  shareToken: string;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const SharedItineraryPage: React.FC<SharedItineraryPageProps> = ({ shareToken, onNavigate }) => {
  const { isAuthenticated } = useAuth();
  const [itinerary, setItinerary] = useState<PublicTripItineraryResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [copying, setCopying] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copySuccessMsg, setCopySuccessMsg] = useState<string | null>(null);

  useEffect(() => {
    loadPublicTrip();
  }, [shareToken]);

  const loadPublicTrip = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.getPublicTrip(shareToken);
      setItinerary(data);
    } catch (err: any) {
      setError(err.message || 'Public itinerary not found or sharing has been disabled.');
    } finally {
      setLoading(false);
    }
  };

  const handleCopyTrip = async () => {
    if (!isAuthenticated) {
      onNavigate('login');
      return;
    }
    setCopying(true);
    setCopySuccessMsg(null);
    try {
      const copiedTrip = await api.copyPublicTrip(shareToken);
      setCopySuccessMsg('Trip copied to your account successfully!');
      setTimeout(() => {
        onNavigate('builder', copiedTrip.id);
      }, 1500);
    } catch (err: any) {
      alert(err.message || 'Failed to copy public trip');
    } finally {
      setCopying(false);
    }
  };

  if (loading) {
    return <LoadingState message="Loading public itinerary..." />;
  }

  if (error || !itinerary) {
    return (
      <div className="max-w-3xl mx-auto py-16 px-4">
        <Card className="p-8 text-center space-y-4 bg-white border border-slate-200">
          <Globe size={40} className="mx-auto text-rose-500 opacity-60" />
          <h2 className="text-xl font-bold text-slate-900">Public Itinerary Unavailable</h2>
          <p className="text-sm text-rose-700 font-medium">
            {error || 'The requested public share link is invalid or has expired.'}
          </p>
          <Button variant="emerald" size="md" onClick={() => onNavigate('dashboard')}>
            Go to GlobeTrotter Home
          </Button>
        </Card>
      </div>
    );
  }

  const coverUrl = getTripCoverUrl(itinerary.tripId, itinerary.coverPhoto);

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8 space-y-8">
      {/* Public Travel Guide Hero (Light Mode) */}
      <div className="relative rounded-3xl overflow-hidden glass-panel border border-slate-200 p-8 sm:p-10 text-slate-900 shadow-md bg-white space-y-4">
        <img src={coverUrl} alt={itinerary.name} className="absolute inset-0 w-full h-full object-cover opacity-15" />
        <div className="absolute inset-0 bg-gradient-to-t from-white via-white/80 to-transparent"></div>

        <div className="relative z-10 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div>
            <Badge variant="emerald" icon={<Globe size={12} />}>
              Shared Travel Itinerary by {itinerary.creatorName || 'GlobeTrotter Explorer'}
            </Badge>
            <h1 className="text-3xl sm:text-4xl font-extrabold tracking-tight mt-2 text-slate-900">{itinerary.name}</h1>
            <p className="text-slate-600 text-sm mt-1 max-w-2xl font-medium">
              {itinerary.description || 'No description provided.'}
            </p>
          </div>

          <div className="flex flex-col sm:items-end space-y-2">
            <Button variant="emerald" size="md" icon={<Copy size={16} />} loading={copying} onClick={handleCopyTrip}>
              Copy to My Trips
            </Button>
            {!isAuthenticated && (
              <span className="text-[11px] text-slate-500">Sign in to clone this trip to your account</span>
            )}
          </div>
        </div>

        {copySuccessMsg && (
          <div className="relative z-10 p-3.5 bg-emerald-50 border border-emerald-200 text-emerald-800 text-xs rounded-xl font-semibold">
            ✓ {copySuccessMsg}
          </div>
        )}

        <div className="relative z-10 pt-4 border-t border-slate-200 flex flex-wrap gap-6 text-sm text-slate-700">
          <div>
            <span className="text-slate-500 text-xs block font-semibold uppercase">TRIP DATES</span>
            <span className="font-semibold text-slate-900 flex items-center space-x-1 mt-0.5">
              <Calendar size={14} className="text-emerald-600" />
              <span>{itinerary.startDate} to {itinerary.endDate}</span>
            </span>
          </div>
          <div>
            <span className="text-slate-500 text-xs block font-semibold uppercase">ESTIMATED BUDGET</span>
            <span className="font-extrabold text-emerald-700 flex items-center space-x-1 mt-0.5">
              <DollarSign size={14} />
              <span>{itinerary.budget ? formatCurrency(itinerary.budget) : formatCurrency(0)}</span>
            </span>
          </div>
        </div>
      </div>

      {/* Stops & Activities Public View */}
      <div className="space-y-6">
        <h2 className="text-xl font-extrabold text-slate-900">Itinerary Stops ({itinerary.stops.length})</h2>

        {itinerary.stops.length === 0 ? (
          <Card className="p-8 text-center text-slate-500 bg-white border border-slate-200">
            No stops defined in this shared itinerary.
          </Card>
        ) : (
          itinerary.stops.map((stop, idx) => {
            const cityImg = getCityImageUrl(stop.cityName);
            return (
              <Card key={stop.id || idx} className="p-6 space-y-4 shadow-xs bg-white border border-slate-200">
                <div className="flex items-center space-x-4 border-b border-slate-200 pb-4">
                  <div className="w-14 h-14 rounded-2xl overflow-hidden shrink-0 relative border border-slate-200">
                    <img src={cityImg} alt={stop.cityName} className="w-full h-full object-cover" />
                    <div className="absolute inset-0 bg-slate-900/30"></div>
                    <span className="absolute inset-0 flex items-center justify-center font-extrabold text-white text-base">
                      #{idx + 1}
                    </span>
                  </div>
                  <div>
                    <h3 className="text-xl font-bold text-slate-900">
                      {stop.cityName}, <span className="text-slate-500 text-base font-normal">{stop.country}</span>
                    </h3>
                    <p className="text-xs text-slate-500 mt-0.5">
                      📅 {stop.startDate} - {stop.endDate} {stop.notes && `• ${stop.notes}`}
                    </p>
                  </div>
                </div>

                <div className="space-y-3 pl-2 sm:pl-4">
                  <h4 className="text-xs font-semibold uppercase text-slate-500 tracking-wider">Activities</h4>
                  {!stop.activities || stop.activities.length === 0 ? (
                    <p className="text-xs text-slate-500 italic">No scheduled activities for this city stop.</p>
                  ) : (
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                      {stop.activities.map((act, actIdx) => (
                        <div key={act.id || actIdx} className="bg-slate-50 border border-slate-200 rounded-xl p-4 space-y-2">
                          <div className="flex justify-between items-start">
                            <span className="font-bold text-slate-900 text-sm">{act.name}</span>
                            <span className="text-xs font-extrabold text-emerald-700">{formatCurrency(act.cost)}</span>
                          </div>
                          <Badge variant="emerald">{act.category}</Badge>
                          <p className="text-xs text-slate-600 pt-1">
                            📅 {act.scheduledDate} {act.startTime && `• ⏰ ${act.startTime}`}
                          </p>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              </Card>
            );
          })
        )}
      </div>
    </div>
  );
};
