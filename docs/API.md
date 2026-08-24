# GlobeTrotter — REST API Specifications

> **Document Status**: Complete API Contract Specification (Phase 8 Audited)  
> **Source of Truth**: GlobeTrotter Backend Implementation & PRD  
> **Base URL**: `/api`  

---

## 1. Authentication Endpoints (`/api/auth`)

### 1.1 `POST /api/auth/signup`
- **Purpose**: Register a new traveler account.
- **Auth**: Public
- **Request Body**:
  ```json
  {
    "name": "Jane Doe",
    "email": "jane@example.com",
    "password": "Password123!"
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "profilePhoto": null,
      "languagePreference": "en"
    }
  }
  ```

### 1.2 `POST /api/auth/login`
- **Purpose**: Authenticate existing user and issue stateless JWT token.
- **Auth**: Public
- **Request Body**:
  ```json
  {
    "email": "jane@example.com",
    "password": "Password123!"
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "Jane Doe",
      "email": "jane@example.com",
      "profilePhoto": "https://example.com/avatar.jpg",
      "languagePreference": "en"
    }
  }
  ```

---

## 2. User Profile & Settings Endpoints (`/api/users`)

### 2.1 `GET /api/users/me`
- **Purpose**: Retrieve current logged-in user profile.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`
  ```json
  {
    "id": 1,
    "name": "Jane Doe",
    "email": "jane@example.com",
    "profilePhoto": "https://example.com/photo.jpg",
    "languagePreference": "en"
  }
  ```

---

## 3. Trip Management Endpoints (`/api/trips`)

### 3.1 `GET /api/trips`
- **Purpose**: Retrieve list of all trips created by the logged-in user.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK` (Array of Trip objects)

### 3.2 `POST /api/trips`
- **Purpose**: Create a new trip.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "name": "European Tour 2026",
    "description": "Multi-city tour across France and Italy.",
    "startDate": "2026-10-10",
    "endDate": "2026-10-20",
    "coverPhoto": "https://example.com/paris.jpg",
    "budget": 1500.00
  }
  ```
- **Response**: `201 Created`
  ```json
  {
    "id": 101,
    "name": "European Tour 2026",
    "description": "Multi-city tour across France and Italy.",
    "startDate": "2026-10-10",
    "endDate": "2026-10-20",
    "coverPhoto": "https://example.com/paris.jpg",
    "budget": 1500.00,
    "isPublic": false,
    "shareToken": null,
    "createdAt": "2026-08-22T10:00:00",
    "updatedAt": "2026-08-22T10:00:00"
  }
  ```

### 3.3 `GET /api/trips/{id}`
- **Purpose**: Get metadata details of a specific trip.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 3.4 `PUT /api/trips/{id}`
- **Purpose**: Edit trip details.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 3.5 `DELETE /api/trips/{id}`
- **Purpose**: Delete a trip and its associated stops/activities.
- **Auth**: Bearer JWT Token
- **Response**: `204 No Content`

---

## 4. City Search & Discovery Endpoints (`/api/cities`)

### 4.1 `GET /api/cities`
- **Purpose**: Search cities by name/country/region.
- **Auth**: Public / Authenticated
- **Query Parameters**: `search`, `country`, `region`
- **Response**: `200 OK` (Array of City objects)

### 4.2 `GET /api/cities/{id}`
- **Purpose**: Fetch single city details.
- **Auth**: Public / Authenticated
- **Response**: `200 OK`

---

## 5. Trip Stops Endpoints (`/api/trips/{tripId}/stops`)

### 5.1 `GET /api/trips/{tripId}/stops`
- **Purpose**: List stops for a trip ordered by `stopOrder`.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 5.2 `POST /api/trips/{tripId}/stops`
- **Purpose**: Add a city stop to an itinerary with travel dates.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "cityId": 12,
    "startDate": "2026-10-10",
    "endDate": "2026-10-15",
    "notes": "Exploring Paris"
  }
  ```
- **Response**: `201 Created`

### 5.3 `PUT /api/trips/{tripId}/stops/{stopId}`
- **Purpose**: Update dates or notes for a stop.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 5.4 `PUT /api/trips/{tripId}/stops/reorder`
- **Purpose**: Reorder sequence of stops in an itinerary.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "orderedStopIds": [2, 1, 3]
  }
  ```
- **Response**: `200 OK`

### 5.5 `DELETE /api/trips/{tripId}/stops/{stopId}`
- **Purpose**: Remove a city stop from the trip.
- **Auth**: Bearer JWT Token
- **Response**: `204 No Content`

---

## 6. Activity Search & Trip Activity Endpoints

### 6.1 `GET /api/activities`
- **Purpose**: Search catalog activities filtered by `cityId`, `search`, `category`.
- **Auth**: Public / Authenticated
- **Response**: `200 OK` (Array of Activity objects)

### 6.2 `GET /api/trips/{tripId}/stops/{stopId}/activities`
- **Purpose**: List activities assigned to a specific stop ordered by `activityOrder`.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 6.3 `POST /api/trips/{tripId}/stops/{stopId}/activities`
- **Purpose**: Assign an activity to a trip stop.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "activityId": 501,
    "scheduledDate": "2026-10-11",
    "startTime": "10:00:00",
    "notes": "Booked online",
    "customCost": 45.00
  }
  ```
- **Response**: `201 Created`

### 6.4 `PUT /api/trips/{tripId}/stops/{stopId}/activities/{tripActivityId}`
- **Purpose**: Edit scheduled activity time, date, notes, or custom cost.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 6.5 `PUT /api/trips/{tripId}/stops/{stopId}/activities/reorder`
- **Purpose**: Reorder assigned activities within a stop.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "orderedTripActivityIds": [3, 1, 2]
  }
  ```
- **Response**: `200 OK`

### 6.6 `DELETE /api/trips/{tripId}/stops/{stopId}/activities/{tripActivityId}`
- **Purpose**: Remove assigned activity from a trip stop.
- **Auth**: Bearer JWT Token
- **Response**: `204 No Content`

---

## 7. Budget Endpoints (`/api/trips/{tripId}/budget`)

### 7.1 `GET /api/trips/{tripId}/budget`
- **Purpose**: Retrieve financial breakdown (budget, total activity cost, remaining, %, category breakdown, overbudget flag).
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`
  ```json
  {
    "tripId": 101,
    "budget": 1500.00,
    "totalActivityCost": 450.00,
    "remainingBudget": 1050.00,
    "budgetUsedPercentage": 30.0,
    "budgetExceeded": false,
    "currency": "USD",
    "categoryBreakdown": [
      {
        "category": "ADVENTURE",
        "cost": 250.00,
        "activityCount": 2
      },
      {
        "category": "CULTURE",
        "cost": 200.00,
        "activityCount": 3
      }
    ]
  }
  ```

### 7.2 `PUT /api/trips/{tripId}/budget`
- **Purpose**: Set or update trip budget.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "budget": 2000.00
  }
  ```
- **Response**: `200 OK` (returns updated `BudgetSummaryResponse`)

---

## 8. Sharing & Public Itinerary Endpoints

### 8.1 `PUT /api/trips/{tripId}/sharing`
- **Purpose**: Enable or disable public sharing for a trip.
- **Auth**: Bearer JWT Token
- **Request Body**:
  ```json
  {
    "isPublic": true
  }
  ```
- **Response**: `200 OK`
  ```json
  {
    "tripId": 101,
    "isPublic": true,
    "shareToken": "4dc3785e-9c8d-4e02-a9f5-ec2fa049302a",
    "publicUrl": "/shared/4dc3785e-9c8d-4e02-a9f5-ec2fa049302a"
  }
  ```

### 8.2 `GET /api/trips/{tripId}/sharing`
- **Purpose**: Retrieve sharing status & public link for trip owner.
- **Auth**: Bearer JWT Token
- **Response**: `200 OK`

### 8.3 `GET /api/public/trips/{shareToken}`
- **Purpose**: Retrieve read-only trip itinerary payload for public view page.
- **Auth**: Public
- **Response**: `200 OK`
  ```json
  {
    "tripId": 101,
    "shareToken": "4dc3785e-9c8d-4e02-a9f5-ec2fa049302a",
    "name": "European Tour 2026",
    "description": "Multi-city tour across France and Italy.",
    "startDate": "2026-10-10",
    "endDate": "2026-10-20",
    "coverPhoto": "https://example.com/paris.jpg",
    "creatorName": "Jane Doe",
    "budget": 1500.00,
    "stops": [...]
  }
  ```

### 8.4 `POST /api/public/trips/{shareToken}/copy`
- **Purpose**: Clone a public shared itinerary into the logged-in user's account.
- **Auth**: Bearer JWT Token
- **Response**: `201 Created` returning cloned `TripResponse`.
