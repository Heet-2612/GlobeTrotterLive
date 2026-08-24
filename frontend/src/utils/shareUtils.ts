/**
 * Share utilities for generating reliable public preview URLs
 * and encoding/decoding self-contained shared travel itineraries.
 */

export function getPublicAppOrigin(): string {
  if (typeof window === 'undefined') return '';
  const origin = window.location.origin;

  // If running inside Google AI Studio dev sandbox (ais-dev-...), replace with the public preview domain (ais-pre-...)
  // This ensures external users and friends opening the link access the live app directly rather than the AI Studio studio IDE.
  if (origin.includes('ais-dev-')) {
    return origin.replace('ais-dev-', 'ais-pre-');
  }

  return origin;
}

export function buildShareUrl(shareToken: string): string {
  const origin = getPublicAppOrigin();
  return `${origin}?share=${encodeURIComponent(shareToken)}`;
}

/**
 * URL-safe Base64 encoding for Unicode JSON strings
 */
export function encodePayloadToBase64Url(obj: any): string {
  try {
    const json = JSON.stringify(obj);
    // Handle UTF-8 characters safely
    const bytes = new TextEncoder().encode(json);
    let binary = '';
    const len = bytes.byteLength;
    for (let i = 0; i < len; i++) {
      binary += String.fromCharCode(bytes[i]);
    }
    const base64 = btoa(binary);
    // Make URL safe: replace '+' with '-', '/' with '_', and remove '=' padding
    return base64.replace(/\+/g, '-').replace(/\//g, '_').replace(/=+$/, '');
  } catch (e) {
    console.error('Failed to encode share payload', e);
    return '';
  }
}

/**
 * URL-safe Base64 decoding for Unicode JSON strings
 */
export function decodePayloadFromBase64Url<T = any>(base64Url: string): T | null {
  try {
    // Restore standard base64 characters and padding
    let base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    while (base64.length % 4) {
      base64 += '=';
    }
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    const json = new TextDecoder().decode(bytes);
    return JSON.parse(json) as T;
  } catch (e) {
    console.error('Failed to decode share payload', e);
    return null;
  }
}
