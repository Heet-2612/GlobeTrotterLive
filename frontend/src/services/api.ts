import {
  AuthResponse,
  LoginRequest,
  SignupRequest,
  ForgotPasswordRequest,
  ForgotPasswordResponse,
  ResetPasswordRequest,
  UserResponse,
  TripResponse,
  CreateTripRequest,
  UpdateTripRequest,
  CityResponse,
  TripStopResponse,
  CreateTripStopRequest,
  ActivityResponse,
  TripActivityResponse,
  CreateTripActivityRequest,
  BudgetSummaryResponse,
  SetBudgetRequest,
  TripSharingResponse,
  UpdateSharingRequest,
  PublicTripItineraryResponse,
} from '../types';

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

export class ApiError extends Error {
  status: number;
  data: any;

  constructor(status: number, message: string, data?: any) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.data = data;
  }
}

async function request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const token = localStorage.getItem('globetrotter_token');
  const headers: Record<string, string> = {
    'Content-Type': 'application/json',
    Accept: 'application/json',
    ...(options.headers as Record<string, string>),
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    ...options,
    headers,
  });

  if (response.status === 401) {
    localStorage.removeItem('globetrotter_token');
    localStorage.removeItem('globetrotter_user');
    window.dispatchEvent(new Event('globetrotter_unauthorized'));
    throw new ApiError(401, 'Session expired. Please log in again.');
  }

  if (!response.ok) {
    let errorData: any;
    try {
      errorData = await response.json();
    } catch {
      errorData = { message: response.statusText };
    }
    const message = errorData.message || errorData.error || 'An unexpected error occurred.';
    throw new ApiError(response.status, message, errorData);
  }

  if (response.status === 2400 || response.status === 204) {
    return {} as T;
  }

  const text = await response.text();
  if (!text || text.trim() === '') {
    return {} as T;
  }

  return JSON.parse(text) as T;
}

export const api = {
  // Auth
  login: (data: LoginRequest) =>
    request<AuthResponse>('/auth/login', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  signup: (data: SignupRequest) =>
    request<AuthResponse>('/auth/signup', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  forgotPassword: (data: ForgotPasswordRequest) =>
    request<ForgotPasswordResponse>('/auth/forgot-password', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  resetPassword: (data: ResetPasswordRequest) =>
    request<ForgotPasswordResponse>('/auth/reset-password', {
      method: 'POST',
      body: JSON.stringify(data),
    }),

  getCurrentUser: () => request<UserResponse>('/users/me'),

  // Health
  checkHealth: () => request<{ status: string; service: string }>('/health'),

  // Trips
  getTrips: () => request<TripResponse[]>('/trips'),
  getTripById: (tripId: number) => request<TripResponse>(`/trips/${tripId}`),
  createTrip: (data: CreateTripRequest) =>
    request<TripResponse>('/trips', {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  updateTrip: (tripId: number, data: UpdateTripRequest) =>
    request<TripResponse>(`/trips/${tripId}`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  deleteTrip: (tripId: number) =>
    request<void>(`/trips/${tripId}`, {
      method: 'DELETE',
    }),

  // Cities
  searchCities: (query?: string, country?: string, region?: string) => {
    const params = new URLSearchParams();
    if (query) params.append('search', query);
    if (country) params.append('country', country);
    if (region) params.append('region', region);
    const queryString = params.toString();
    return request<CityResponse[]>(`/cities${queryString ? `?${queryString}` : ''}`);
  },
  getCityById: (cityId: number) => request<CityResponse>(`/cities/${cityId}`),

  // Stops
  getTripStops: (tripId: number) => request<TripStopResponse[]>(`/trips/${tripId}/stops`),
  createTripStop: (tripId: number, data: CreateTripStopRequest) =>
    request<TripStopResponse>(`/trips/${tripId}/stops`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  deleteTripStop: (tripId: number, stopId: number) =>
    request<void>(`/trips/${tripId}/stops/${stopId}`, {
      method: 'DELETE',
    }),

  // Activities
  searchActivities: (cityId?: number, search?: string, category?: string) => {
    const params = new URLSearchParams();
    if (cityId) params.append('cityId', cityId.toString());
    if (search) params.append('search', search);
    if (category) params.append('category', category);
    const queryString = params.toString();
    return request<ActivityResponse[]>(`/activities${queryString ? `?${queryString}` : ''}`);
  },
  getActivityById: (activityId: number) => request<ActivityResponse>(`/activities/${activityId}`),

  // Trip Activities
  getTripActivities: (tripId: number, stopId: number) =>
    request<TripActivityResponse[]>(`/trips/${tripId}/stops/${stopId}/activities`),
  createTripActivity: (tripId: number, stopId: number, data: CreateTripActivityRequest) =>
    request<TripActivityResponse>(`/trips/${tripId}/stops/${stopId}/activities`, {
      method: 'POST',
      body: JSON.stringify(data),
    }),
  deleteTripActivity: (tripId: number, stopId: number, tripActivityId: number) =>
    request<void>(`/trips/${tripId}/stops/${stopId}/activities/${tripActivityId}`, {
      method: 'DELETE',
    }),

  // Budget
  getBudgetSummary: (tripId: number) => request<BudgetSummaryResponse>(`/trips/${tripId}/budget`),
  updateBudget: (tripId: number, data: SetBudgetRequest) =>
    request<BudgetSummaryResponse>(`/trips/${tripId}/budget`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),

  // Sharing
  getSharingStatus: (tripId: number) => request<TripSharingResponse>(`/trips/${tripId}/sharing`),
  updateSharing: (tripId: number, data: UpdateSharingRequest) =>
    request<TripSharingResponse>(`/trips/${tripId}/sharing`, {
      method: 'PUT',
      body: JSON.stringify(data),
    }),
  getPublicTrip: (shareToken: string) =>
    request<PublicTripItineraryResponse>(`/public/trips/${shareToken}`),
  copyPublicTrip: (shareToken: string) =>
    request<TripResponse>(`/public/trips/${shareToken}/copy`, {
      method: 'POST',
    }),

  // Admin
  getAdminStats: () => request<any>('/admin/stats'),
  getAdminUsers: () => request<UserResponse[]>('/admin/users'),
  getAdminTrips: () => request<any[]>('/admin/trips'),
  promoteUser: (id: number) =>
    request<UserResponse>(`/admin/users/${id}/promote`, { method: 'POST' }),
  demoteUser: (id: number) =>
    request<UserResponse>(`/admin/users/${id}/demote`, { method: 'POST' }),
};
