export interface UserResponse {
  id: number;
  name: string;
  email: string;
  profilePhoto?: string;
  languagePreference?: string;
  createdAt?: string;
  role: string;
}

export interface AuthResponse {
  token: string;
  user: UserResponse;
}

export interface SignupRequest {
  name: string;
  email: string;
  password: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface ForgotPasswordRequest {
  email: string;
}

export interface ForgotPasswordResponse {
  message: string;
}

export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

export interface TripResponse {
  id: number;
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  coverPhoto?: string;
  budget?: number;
  isPublic: boolean;
  shareToken?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface CreateTripRequest {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  coverPhoto?: string;
  budget?: number;
}

export interface UpdateTripRequest {
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  coverPhoto?: string;
  budget?: number;
}

export interface CityResponse {
  id: number;
  name: string;
  country: string;
  region?: string;
  costIndex?: number;
  popularity?: number;
  imageUrl?: string;
}

export interface TripStopResponse {
  id: number;
  tripId: number;
  city: CityResponse;
  stopOrder: number;
  startDate: string;
  endDate: string;
  notes?: string;
}

export interface CreateTripStopRequest {
  cityId: number;
  startDate: string;
  endDate: string;
  notes?: string;
}

export interface ActivityResponse {
  id: number;
  cityId?: number;
  cityName?: string;
  name: string;
  description?: string;
  category?: string;
  estimatedDurationMinutes?: number;
  estimatedCost?: number;
  currency?: string;
  imageUrl?: string;
}

export interface TripActivityResponse {
  id: number;
  tripStopId: number;
  activity: ActivityResponse;
  scheduledDate: string;
  startTime?: string;
  notes?: string;
  customCost?: number;
  activityOrder: number;
}

export interface CreateTripActivityRequest {
  activityId: number;
  scheduledDate: string;
  startTime?: string;
  notes?: string;
  customCost?: number;
}

export interface CategoryCostSummary {
  category: string;
  totalCost: number;
  count: number;
}

export interface BudgetSummaryResponse {
  tripId: number;
  budget: number;
  totalActivityCost: number;
  remainingBudget: number;
  budgetUsedPercentage: number;
  budgetExceeded: boolean;
  currency: string;
  categoryBreakdown: CategoryCostSummary[];
}

export interface SetBudgetRequest {
  budget: number;
}

export interface TripSharingResponse {
  tripId: number;
  isPublic: boolean;
  shareToken?: string;
  publicUrl?: string;
}

export interface UpdateSharingRequest {
  isPublic: boolean;
}

export interface PublicTripActivityResponse {
  id: number;
  name: string;
  category: string;
  scheduledDate: string;
  startTime?: string;
  cost: number;
  currency: string;
}

export interface PublicTripStopResponse {
  id: number;
  cityName: string;
  country: string;
  startDate: string;
  endDate: string;
  notes?: string;
  activities: PublicTripActivityResponse[];
}

export interface PublicTripItineraryResponse {
  tripId: number;
  shareToken: string;
  name: string;
  description?: string;
  startDate: string;
  endDate: string;
  coverPhoto?: string;
  creatorName: string;
  budget?: number;
  stops: PublicTripStopResponse[];
}
