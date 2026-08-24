// Curated high-resolution Unsplash travel imagery for destinations & activities

const CITY_IMAGES: Record<string, string> = {
  paris: 'https://images.unsplash.com/photo-1502602898657-3e91760cbb34?auto=format&fit=crop&w=800&q=80',
  tokyo: 'https://images.unsplash.com/photo-1503899036084-c55cdd92da26?auto=format&fit=crop&w=800&q=80',
  london: 'https://images.unsplash.com/photo-1513635269975-59663e0ac1ad?auto=format&fit=crop&w=800&q=80',
  rome: 'https://images.unsplash.com/photo-1552832230-c0197dd311b5?auto=format&fit=crop&w=800&q=80',
  'new york': 'https://images.unsplash.com/photo-1496442226666-8d4d0e62e6e9?auto=format&fit=crop&w=800&q=80',
  sydney: 'https://images.unsplash.com/photo-1506973035872-a4ec16b8e8d9?auto=format&fit=crop&w=800&q=80',
  bali: 'https://images.unsplash.com/photo-1537996194471-e657df975ab4?auto=format&fit=crop&w=800&q=80',
  dubai: 'https://images.unsplash.com/photo-1512453979798-5ea266f8880c?auto=format&fit=crop&w=800&q=80',
  amsterdam: 'https://images.unsplash.com/photo-1512470876302-972faa2aa9a4?auto=format&fit=crop&w=800&q=80',
  barcelona: 'https://images.unsplash.com/photo-1539037116277-4db20889f2d4?auto=format&fit=crop&w=800&q=80',
  kyoto: 'https://images.unsplash.com/photo-1493976040374-85c8e12f0c0e?auto=format&fit=crop&w=800&q=80',
  venice: 'https://images.unsplash.com/photo-1514890547357-a9ee288728e0?auto=format&fit=crop&w=800&q=80',
  singapore: 'https://images.unsplash.com/photo-1525625293386-3f8f99389edd?auto=format&fit=crop&w=800&q=80',
  prague: 'https://images.unsplash.com/photo-1541849546-216549ae216d?auto=format&fit=crop&w=800&q=80',
  cairo: 'https://images.unsplash.com/photo-1503177119275-0aa32b3a9368?auto=format&fit=crop&w=800&q=80',
};

const CATEGORY_IMAGES: Record<string, string> = {
  sightseeing: 'https://images.unsplash.com/photo-1527631746610-bca00a040d60?auto=format&fit=crop&w=600&q=80',
  food: 'https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=600&q=80',
  adventure: 'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=600&q=80',
  culture: 'https://images.unsplash.com/photo-1461360370896-922624d12aa1?auto=format&fit=crop&w=600&q=80',
  nightlife: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=600&q=80',
  shopping: 'https://images.unsplash.com/photo-1483985988355-763728e1935b?auto=format&fit=crop&w=600&q=80',
};

const DEFAULT_TRIP_IMAGES = [
  'https://images.unsplash.com/photo-1488646953014-85cb44e25828?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1469854523086-cc02fe5d8800?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1476514525535-ce74f452899f?auto=format&fit=crop&w=800&q=80',
  'https://images.unsplash.com/photo-1507525428034-b723cf961d3e?auto=format&fit=crop&w=800&q=80',
];

export function getCityImageUrl(cityName?: string, fallbackUrl?: string): string {
  if (fallbackUrl && fallbackUrl.startsWith('http')) return fallbackUrl;
  if (!cityName) return DEFAULT_TRIP_IMAGES[0];
  const key = cityName.trim().toLowerCase();
  return CITY_IMAGES[key] || DEFAULT_TRIP_IMAGES[Math.abs(hashString(cityName)) % DEFAULT_TRIP_IMAGES.length];
}

export function getActivityImageUrl(category?: string, fallbackUrl?: string): string {
  if (fallbackUrl && fallbackUrl.startsWith('http')) return fallbackUrl;
  if (!category) return CATEGORY_IMAGES['sightseeing'];
  const key = category.trim().toLowerCase();
  return CATEGORY_IMAGES[key] || CATEGORY_IMAGES['sightseeing'];
}

export function getTripCoverUrl(tripId?: number, coverPhoto?: string): string {
  if (coverPhoto && coverPhoto.startsWith('http')) return coverPhoto;
  const idx = (tripId || 0) % DEFAULT_TRIP_IMAGES.length;
  return DEFAULT_TRIP_IMAGES[idx];
}

function hashString(str: string): number {
  let hash = 0;
  for (let i = 0; i < str.length; i++) {
    hash = (hash << 5) - hash + str.charCodeAt(i);
    hash |= 0;
  }
  return hash;
}
