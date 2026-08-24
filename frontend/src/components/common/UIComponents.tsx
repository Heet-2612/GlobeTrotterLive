import { formatCurrency } from '../../utils/currencyUtils';
import React, { ReactNode } from 'react';
import {
  MapPin,
  Calendar,
  DollarSign,
  Star,
  Globe,
  Lock,
  Trash2,
  Edit,
  Clock,
  ArrowRight,
  Eye,
  Settings,
} from 'lucide-react';
import { getCityImageUrl, getActivityImageUrl, getTripCoverUrl } from '../../utils/imageUtils';
import { TripResponse, CityResponse, ActivityResponse } from '../../types';

// Button Component
interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'secondary' | 'danger' | 'ghost' | 'emerald';
  size?: 'sm' | 'md' | 'lg';
  icon?: ReactNode;
  loading?: boolean;
}

export const Button: React.FC<ButtonProps> = ({
  children,
  variant = 'primary',
  size = 'md',
  icon,
  loading,
  className = '',
  disabled,
  ...props
}) => {
  const baseStyles = 'inline-flex items-center justify-center font-semibold rounded-xl transition-all duration-200 focus:outline-none disabled:opacity-50 cursor-pointer shadow-xs';

  const variants = {
    primary: 'bg-emerald-600 hover:bg-emerald-700 text-white shadow-emerald-900/10 border border-emerald-500/30',
    emerald: 'bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-700 hover:to-teal-700 text-white shadow-emerald-900/15',
    secondary: 'bg-white hover:bg-slate-100 text-slate-800 border border-slate-200 shadow-slate-200/50',
    danger: 'bg-rose-600 hover:bg-rose-700 text-white border border-rose-500/30',
    ghost: 'text-slate-600 hover:text-slate-900 hover:bg-slate-200/60',
  };

  const sizes = {
    sm: 'text-xs px-3 py-1.5 space-x-1.5',
    md: 'text-sm px-4 py-2 space-x-2',
    lg: 'text-base px-5 py-2.5 space-x-2.5',
  };

  return (
    <button
      className={`${baseStyles} ${variants[variant]} ${sizes[size]} ${className}`}
      disabled={disabled || loading}
      {...props}
    >
      {loading ? (
        <span className="animate-spin rounded-full h-4 w-4 border-2 border-white/30 border-t-white"></span>
      ) : (
        icon && <span className="shrink-0">{icon}</span>
      )}
      <span>{children}</span>
    </button>
  );
};

// Glass Card Component (Light Mode)
interface CardProps {
  children: ReactNode;
  className?: string;
  hoverable?: boolean;
  onClick?: () => void;
}

export const Card: React.FC<CardProps> = ({ children, className = '', hoverable = false, onClick }) => {
  return (
    <div
      onClick={onClick}
      className={`glass-panel rounded-2xl p-6 ${hoverable ? 'glass-panel-hover cursor-pointer' : ''} ${className}`}
    >
      {children}
    </div>
  );
};

// Badge Component (Light Mode)
interface BadgeProps {
  children: ReactNode;
  variant?: 'emerald' | 'blue' | 'slate' | 'amber' | 'rose';
  icon?: ReactNode;
  className?: string;
}

export const Badge: React.FC<BadgeProps> = ({ children, variant = 'slate', icon, className = '' }) => {
  const styles = {
    emerald: 'bg-emerald-50 text-emerald-800 border-emerald-200/80',
    blue: 'bg-sky-50 text-sky-800 border-sky-200/80',
    slate: 'bg-slate-100 text-slate-700 border-slate-200',
    amber: 'bg-amber-50 text-amber-800 border-amber-200/80',
    rose: 'bg-rose-50 text-rose-800 border-rose-200/80',
  };

  return (
    <span className={`inline-flex items-center space-x-1 text-xs font-semibold px-2.5 py-0.5 rounded-full border ${styles[variant]} ${className}`}>
      {icon && <span>{icon}</span>}
      <span>{children}</span>
    </span>
  );
};

// Input Component (Light Mode)
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
}

export const Input: React.FC<InputProps> = ({ label, error, className = '', ...props }) => {
  return (
    <div className="space-y-1">
      {label && <label className="block text-xs font-semibold text-slate-700 uppercase tracking-wider">{label}</label>}
      <input
        className={`w-full px-3.5 py-2.5 bg-white border border-slate-300 rounded-xl text-slate-900 text-sm focus:outline-none focus:border-emerald-600 focus:ring-1 focus:ring-emerald-600 transition-all placeholder-slate-400 ${className}`}
        {...props}
      />
      {error && <p className="text-xs text-rose-600 mt-1">{error}</p>}
    </div>
  );
};

// Loading State
export const LoadingState: React.FC<{ message?: string }> = ({ message = 'Loading...' }) => (
  <div className="text-center py-16 text-slate-600 flex flex-col items-center justify-center space-y-3">
    <div className="animate-spin rounded-full h-8 w-8 border-3 border-emerald-500/30 border-t-emerald-600"></div>
    <span className="text-sm font-medium">{message}</span>
  </div>
);

// Empty State
export const EmptyState: React.FC<{ title: string; description: string; action?: ReactNode }> = ({
  title,
  description,
  action,
}) => (
  <div className="glass-panel rounded-2xl p-12 text-center text-slate-600 space-y-3 max-w-md mx-auto my-8">
    <div className="w-12 h-12 rounded-full bg-emerald-100 text-emerald-700 flex items-center justify-center mx-auto text-xl font-bold">
      📍
    </div>
    <h3 className="text-lg font-bold text-slate-900">{title}</h3>
    <p className="text-xs text-slate-500">{description}</p>
    {action && <div className="pt-2">{action}</div>}
  </div>
);

// Trip Card Component (Light Mode)
interface TripCardProps {
  trip: TripResponse;
  onNavigate: (tab: string, param?: string | number) => void;
  onDelete?: (tripId: number, name: string) => void;
}

export const TripCard: React.FC<TripCardProps> = ({ trip, onNavigate, onDelete }) => {
  const imageUrl = getTripCoverUrl(trip.id, trip.coverPhoto);

  return (
    <Card hoverable className="p-0 overflow-hidden flex flex-col justify-between group bg-white border border-slate-200 shadow-sm">
      <div className="relative h-44 overflow-hidden">
        <img
          src={imageUrl}
          alt={trip.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent"></div>
        <div className="absolute top-3 right-3 flex space-x-2">
          <Badge variant={trip.isPublic ? 'emerald' : 'slate'} icon={trip.isPublic ? <Globe size={12} /> : <Lock size={12} />}>
            {trip.isPublic ? 'Public' : 'Private'}
          </Badge>
        </div>
        <div className="absolute bottom-3 left-4 right-4">
          <h3 className="text-xl font-extrabold text-white truncate drop-shadow">{trip.name}</h3>
        </div>
      </div>

      <div className="p-5 space-y-4 flex-1 flex flex-col justify-between">
        <p className="text-xs text-slate-600 line-clamp-2 min-h-[32px]">
          {trip.description || 'No description provided.'}
        </p>

        <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs text-slate-700">
          <div className="flex items-center space-x-1.5 text-slate-600">
            <Calendar size={13} className="text-emerald-600" />
            <span>{trip.startDate} - {trip.endDate}</span>
          </div>
          <div className="font-extrabold text-emerald-700 text-sm">
            ${trip.budget ? trip.budget.toLocaleString() : '0'}
          </div>
        </div>

        <div className="grid grid-cols-2 gap-2 pt-2 border-t border-slate-100">
          <Button variant="emerald" size="sm" icon={<Edit size={13} />} onClick={() => onNavigate('builder', trip.id)}>
            Builder
          </Button>
          <Button variant="secondary" size="sm" icon={<Eye size={13} />} onClick={() => onNavigate('view', trip.id)}>
            Read View
          </Button>
        </div>

        <div className="flex items-center justify-between pt-2 text-xs">
          <button
            onClick={() => onNavigate('budget', trip.id)}
            className="text-slate-500 hover:text-emerald-700 flex items-center space-x-1 transition-colors font-medium"
          >
            <DollarSign size={12} />
            <span>Budget</span>
          </button>
          <button
            onClick={() => onNavigate('timeline', trip.id)}
            className="text-slate-500 hover:text-emerald-700 flex items-center space-x-1 transition-colors font-medium"
          >
            <Clock size={12} />
            <span>Timeline</span>
          </button>

          {onDelete && (
            <button
              onClick={() => onDelete(trip.id, trip.name)}
              className="text-rose-600 hover:text-rose-700 flex items-center space-x-1 transition-colors font-medium"
            >
              <Trash2 size={12} />
              <span>Delete</span>
            </button>
          )}
        </div>
      </div>
    </Card>
  );
};

// City Card Component (Light Mode)
interface CityCardProps {
  city: CityResponse;
  onNavigate: (tab: string, param?: string | number) => void;
}

export const CityCard: React.FC<CityCardProps> = ({ city, onNavigate }) => {
  const imageUrl = getCityImageUrl(city.name, city.imageUrl);

  return (
    <Card hoverable className="p-0 overflow-hidden group flex flex-col justify-between bg-white border border-slate-200 shadow-sm">
      <div className="relative h-40 overflow-hidden">
        <img
          src={imageUrl}
          alt={city.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-900/80 via-slate-900/20 to-transparent"></div>
        <div className="absolute top-3 right-3">
          <Badge variant="amber" icon={<Star size={11} className="fill-amber-500 text-amber-500" />}>
            Rank #{city.popularity ?? 'N/A'}
          </Badge>
        </div>
        <div className="absolute bottom-3 left-4">
          <h3 className="text-xl font-bold text-white drop-shadow">{city.name}</h3>
          <p className="text-xs text-slate-200 font-medium">{city.country}</p>
        </div>
      </div>

      <div className="p-4 flex items-center justify-between border-t border-slate-100 text-xs">
        <span className="text-slate-600 font-medium">
          Cost Index: <strong className="text-emerald-700">{city.costIndex ?? 1.0}x</strong>
        </span>
        <Button variant="primary" size="sm" icon={<ArrowRight size={12} />} onClick={() => onNavigate('create-trip')}>
          Plan Trip
        </Button>
      </div>
    </Card>
  );
};

// Activity Card Component (Light Mode)
interface ActivityCardProps {
  activity: ActivityResponse;
}

export const ActivityCard: React.FC<ActivityCardProps> = ({ activity }) => {
  const imageUrl = getActivityImageUrl(activity.category, activity.imageUrl);

  return (
    <Card hoverable className="p-0 overflow-hidden flex flex-col justify-between group bg-white border border-slate-200 shadow-sm">
      <div className="relative h-36 overflow-hidden">
        <img
          src={imageUrl}
          alt={activity.name}
          className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
        />
        <div className="absolute inset-0 bg-gradient-to-t from-slate-950/70 via-slate-950/10 to-transparent"></div>
        <div className="absolute top-3 left-3">
          <Badge variant="emerald">{activity.category || 'Sightseeing'}</Badge>
        </div>
        <div className="absolute bottom-2 left-3 right-3 flex items-center justify-between text-xs text-slate-200">
          <span className="flex items-center space-x-1 text-slate-200 font-medium">
            <MapPin size={12} className="text-emerald-400" />
            <span>{activity.cityName || 'Destination'}</span>
          </span>
        </div>
      </div>

      <div className="p-4 space-y-2 flex-1 flex flex-col justify-between">
        <div>
          <h4 className="font-bold text-slate-900 text-base">{activity.name}</h4>
          <p className="text-xs text-slate-600 line-clamp-2 mt-1">
            {activity.description || 'No description available.'}
          </p>
        </div>

        <div className="pt-3 border-t border-slate-100 flex items-center justify-between text-xs">
          <span className="text-slate-500 flex items-center space-x-1 font-medium">
            <Clock size={12} />
            <span>{activity.estimatedDurationMinutes ? `${activity.estimatedDurationMinutes}m` : 'Flexible'}</span>
          </span>
          <span className="font-extrabold text-emerald-700 text-sm">${activity.estimatedCost || 0}</span>
        </div>
      </div>
    </Card>
  );
};
