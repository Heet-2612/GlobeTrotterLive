import React, { useEffect, useState } from 'react';
import { TripSharingResponse } from '../types';
import { api } from '../services/api';
import { Share2, Globe, Lock, Copy, Check } from 'lucide-react';
import { Button, Card, Badge, Input } from './common/UIComponents';

interface SharingSectionProps {
  tripId: number;
}

export const SharingSection: React.FC<SharingSectionProps> = ({ tripId }) => {
  const [sharing, setSharing] = useState<TripSharingResponse | null>(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  useEffect(() => {
    loadSharing();
  }, [tripId]);

  const loadSharing = async () => {
    try {
      setLoading(true);
      setError(null);
      const data = await api.getSharingStatus(tripId);
      setSharing(data);
    } catch (err: any) {
      setError(err.message || 'Failed to load sharing details');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleSharing = async (isPublic: boolean) => {
    try {
      setUpdating(true);
      setError(null);
      const updated = await api.updateSharing(tripId, { isPublic });
      setSharing(updated);
    } catch (err: any) {
      setError(err.message || 'Failed to update sharing');
    } finally {
      setUpdating(false);
    }
  };

  const getPublicLink = () => {
    if (!sharing || !sharing.shareToken) return '';
    return `${window.location.origin}/#public/${sharing.shareToken}`;
  };

  const handleCopyLink = () => {
    const link = getPublicLink();
    if (link) {
      navigator.clipboard.writeText(link);
      setCopied(true);
      setTimeout(() => setCopied(false), 2500);
    }
  };

  if (loading) {
    return (
      <div className="text-center py-8 text-slate-600 flex items-center justify-center space-x-2">
        <div className="animate-spin rounded-full h-5 w-5 border-2 border-emerald-500/30 border-t-emerald-600"></div>
        <span>Loading sharing status...</span>
      </div>
    );
  }

  return (
    <Card className="p-6 space-y-6 bg-white border border-slate-200 shadow-xs">
      <div className="flex items-center justify-between border-b border-slate-200 pb-4">
        <div>
          <h3 className="text-lg font-bold text-slate-900 flex items-center space-x-2">
            <Share2 size={18} className="text-emerald-700" />
            <span>Public Sharing Settings</span>
          </h3>
          <p className="text-xs text-slate-500 mt-1">
            Allow anyone with the link to view your trip itinerary without needing an account
          </p>
        </div>
        <div className="flex items-center space-x-3">
          <Badge variant={sharing?.isPublic ? 'emerald' : 'slate'}>
            {sharing?.isPublic ? 'Public' : 'Private'}
          </Badge>
          <button
            onClick={() => handleToggleSharing(!sharing?.isPublic)}
            disabled={updating}
            className={`w-12 h-6 flex items-center rounded-full p-1 transition-colors cursor-pointer ${
              sharing?.isPublic ? 'bg-emerald-600 justify-end' : 'bg-slate-300 justify-start'
            }`}
          >
            <div className="w-4 h-4 rounded-full bg-white shadow-xs"></div>
          </button>
        </div>
      </div>

      {error && (
        <div className="p-3 bg-rose-50 border border-rose-200 text-rose-800 text-xs rounded-xl font-medium">
          {error}
        </div>
      )}

      {sharing?.isPublic ? (
        <div className="space-y-3 bg-slate-50 p-4 rounded-xl border border-slate-200">
          <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">
            Public Share URL
          </label>
          <div className="flex gap-2">
            <input
              type="text"
              readOnly
              value={getPublicLink()}
              className="flex-1 bg-white border border-slate-300 rounded-xl px-3 py-2 text-xs text-slate-900 focus:outline-none font-mono"
            />
            <Button
              variant="emerald"
              size="sm"
              icon={copied ? <Check size={14} /> : <Copy size={14} />}
              onClick={handleCopyLink}
            >
              {copied ? 'Copied!' : 'Copy Link'}
            </Button>
          </div>
          <p className="text-[11px] text-slate-500 pt-1">
            Share Token: <code className="bg-white px-2 py-0.5 rounded border border-slate-300 font-mono text-emerald-800 font-bold">{sharing.shareToken}</code>
          </p>
        </div>
      ) : (
        <div className="text-center py-6 text-slate-500 text-xs">
          This trip is currently private. Enable sharing above to generate a public link.
        </div>
      )}
    </Card>
  );
};
