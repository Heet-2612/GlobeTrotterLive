# GlobeTrotter — Technical Architecture

> **Document Status**: Architectural Specification  
> **Source of Truth**: GlobeTrotter PRD & Problem Statement  

---

## 1. System Overview & Strategy

GlobeTrotter is designed as a clean, single-page application (SPA) frontend communicating with a stateless RESTful backend API powered by a relational database. This architecture guarantees rapid development, straightforward deployment, and seamless user experiences required for a hackathon demonstration.

```
+-----------------------------------------------------------------------+
|                           React SPA Frontend                           |
|  (Dashboard, Itinerary Builder, Timeline/Calendar, Budget, Share UI) |
+-----------------------------------------------------------------------+
                                    |
                                    | REST API / JSON over HTTP
                                    v
+-----------------------------------------------------------------------+
|                       Spring Boot Backend Service                      |
|  +------------------+  +-------------------+  +--------------------+  |
|  | Controllers      |  | Services          |  | Security (JWT)     |  |
|  +------------------+  +-------------------+  +--------------------+  |
|  | DTO Mappers      |  | Business Logic    |  | Repositories (JPA) |  |
|  +------------------+  +-------------------+  +--------------------+  |
+-----------------------------------------------------------------------+
                                    |
                                    | SQL / JDBC
                                    v
+-----------------------------------------------------------------------+
|                          PostgreSQL Database                          |
|    (users, trips, cities, trip_stops, activities, trip_activities)    |
+-----------------------------------------------------------------------+
```

---

## 2. Architectural Distinction

To maintain compliance with the project guidelines, technical aspects are categorized below:

| Component / Layer | Category | Explanation |
| :--- | :--- | :--- |
| **Relational Database** | `[Explicitly Required by PDF]` | Mandatory use of relational database to store trips, stops, activities, budgets. |
| **Multi-City & Trip Data Model** | `[Explicitly Required by PDF]` | Data domain must represent users, trips, stops, cities, activities, schedules. |
| **Responsive Web Platform** | `[Explicitly Required by PDF]` | Rich, dynamic user interfaces adaptable across desktop or mobile. |
| **Public Sharing Link & Read-Only View**| `[Explicitly Required by PDF]` | Public access URL mechanism exposing read-only trip data. |
| **Frontend Framework (React)** | `[Technical Recommendation]` | Modern component-based SPA framework for fast interactive rendering. |
| **Backend Framework (Spring Boot)** | `[Technical Recommendation]` | Robust Java REST backend with automated JPA/Hibernate ORM mapping. |
| **Database Engine (PostgreSQL)** | `[Technical Recommendation]` | ACID-compliant relational database engine supporting foreign key constraints. |
| **Authentication (JWT Token)** | `[Technical Recommendation]` | Stateless authentication token mechanism stored in client headers. |

---

## 3. Layered Component Architecture

### 3.1 Frontend Architecture (React)
The frontend is structured into modular components:

```text
src/
├── assets/          # Static images, icons, and global styles
├── components/      # Reusable UI components
│   ├── common/      # Navbar, Footer, Modal, LoadingSpinner, Alerts
│   ├── dashboard/   # RecentTripsCard, RecommendedCities, BudgetHighlightWidget
│   ├── itinerary/   # DayView, StopHeader, ActivityBlock, TimelineView, DragContainer
│   ├── budget/      # CategoryBreakdown, ExpenseChart, OverbudgetAlert
│   └── search/      # CityCard, ActivityCard, FilterBar
├── context/         # AuthContext, TripContext (global state management)
├── hooks/           # Custom React hooks (useAuth, useTrip, useBudget)
├── pages/           # Screen views mapped to routes
│   ├── LoginPage.jsx
│   ├── DashboardPage.jsx
│   ├── CreateTripPage.jsx
│   ├── MyTripsPage.jsx
│   ├── ItineraryBuilderPage.jsx
│   ├── ItineraryViewPage.jsx
│   ├── CitySearchPage.jsx
│   ├── ActivitySearchPage.jsx
│   ├── BudgetPage.jsx
│   ├── TimelinePage.jsx
│   ├── SharedItineraryPage.jsx
│   ├── ProfilePage.jsx
│   └── AdminDashboardPage.jsx
├── services/        # API service clients (authService, tripService, cityService)
└── utils/           # Date formatters, currency helpers, validation rules
```

### 3.2 Backend Architecture (Spring Boot)
The backend follows a standard 3-tier architecture:

```text
com.globetrotter/
├── GlobeTrotterApplication.java
├── config/          # SecurityConfig, CorsConfig, SwaggerConfig
├── controller/      # REST API Controllers (AuthController, TripController, etc.)
├── dto/             # Data Transfer Objects (Requests & Responses)
├── entity/          # JPA Entity definitions (User, Trip, TripStop, Activity, etc.)
├── exception/       # GlobalExceptionHandler, ResourceNotFoundException, UnauthorizedException
├── repository/      # Spring Data JPA Repositories
├── security/        # JwtTokenProvider, CustomUserDetailsService, JwtAuthenticationFilter
└── service/         # Business logic layer (TripService, BudgetService, SearchService)
```

---

## 4. Main System Modules

1. **Auth & User Module**: Handles user registration, credentials validation, password hashing (BCrypt), JWT token issuance, profile editing, and account deletion.
2. **Trip Management Module**: Handles creation, reading, updating, deleting (CRUD) of trips, user ownership validation, and date validation.
3. **City & Destination Module**: Provides search, region filtering, and destination meta-information retrieval.
4. **Activity Module**: Manages global/city activity catalogs, category filtering, duration, and activity-to-stop assignments.
5. **Itinerary Module**: Handles stop ordering/reordering, date mapping, day-wise activity grouping, and interactive timeline rendering.
6. **Budget & Cost Module**: Aggregates estimated costs from activities, stay, transport, and meals, calculates daily averages, and raises over-budget flags.
7. **Public Sharing Module**: Manages share token generation, public read-only route resolution, and trip cloning ("Copy Trip").
8. **Admin & Analytics Module `[Optional]`**: Provides aggregate statistics on users, trips, popular cities, and platform usage metrics.

---

## 5. Domain Concept Relationships

```mermaid
erDiagram
    USER ||--o{ TRIP : "owns"
    TRIP ||--o{ TRIP_STOP : "contains"
    CITY ||--o{ TRIP_STOP : "referenced by"
    TRIP_STOP ||--o{ TRIP_ACTIVITY : "includes"
    ACTIVITY ||--o{ TRIP_ACTIVITY : "assigned via"
    TRIP ||--o? TRIP_SHARE : "has share link"
    
    USER {
        Long id
        String email
        String password_hash
        String name
    }

    TRIP {
        Long id
        Long user_id
        String name
        Date start_date
        Date end_date
    }

    TRIP_STOP {
        Long id
        Long trip_id
        Long city_id
        Integer stop_order
    }

    TRIP_ACTIVITY {
        Long id
        Long trip_stop_id
        Long activity_id
        BigDecimal estimated_cost
        Time start_time
    }
```

---

## 6. End-to-End Data Flow

### Example Flow: Creating a Trip & Adding a City Stop
1. **Client Action**: User submits the "Plan New Trip" form on the React frontend.
2. **API Request**: Frontend sends `POST /api/trips` with JWT header containing trip name, dates, and description.
3. **Security Check**: `JwtAuthenticationFilter` validates token, extracts user identity, and attaches user to security context.
4. **Service Execution**: `TripService` validates date ranges, instantiates `Trip` entity, sets `user_id`, and saves to PostgreSQL.
5. **Client Update**: Controller returns `201 Created` with `TripDTO`. React updates state and navigates user to `ItineraryBuilderPage`.
6. **City Stop Addition**: User searches cities (`GET /api/cities?search=Paris`), selects Paris, and clicks "Add Stop". Frontend sends `POST /api/trips/{tripId}/stops`.
7. **Database Storage**: `TripStop` record is created linking `trip_id` and `city_id` with calculated `stop_order`.
8. **Budget Recalculation**: Backend automatically updates the trip's estimated costs and returns updated itinerary payload.
