# GlobeTrotter — Product Requirements Document (PRD)

> **Document Status**: Draft / Final Documentation Specification  
> **Source of Truth**: Official Odoo Hackathon GlobeTrotter Problem Statement  

---

## 1. Product Name, Vision & Mission

### Product Name
**GlobeTrotter** — Empowering Personalized Travel Planning

### Product Vision `[Explicitly Required by PDF]`
The overarching vision for GlobeTrotter is to become a personalized, intelligent, and collaborative platform that transforms the way individuals plan and experience travel. The platform aims to empower users to dream, design, and organize trips with ease by offering an end-to-end travel planning tool that combines flexibility and interactivity.

It envisions a world where users can explore global destinations, visualize their journeys through structured itineraries, make cost-effective decisions, and share their travel plans within a community—making travel planning as exciting as the trip itself.

### Product Mission `[Explicitly Required by PDF]`
The mission for the hackathon team is to build a user-centric, responsive application that simplifies the complexity of planning multi-city travel. The platform should provide travelers with intuitive tools to:
- **Add and manage travel stops and durations**
- **Explore cities and activities of interest**
- **Estimate trip budgets automatically**
- **Visualize timelines and plans**
- **Share trip plans with others**

This involves constructing a solution that is both functional and insightful, powered by a well-designed relational database and a smooth frontend experience. The team must focus on enabling users to organize personalized trips efficiently, stay within budget, and enjoy full visibility of their journey.

---

## 2. Problem Statement `[Explicitly Required by PDF]`

Design and develop a complete travel planning application where users can:
1. **Create customized multi-city itineraries**
2. **Assign travel dates, activities, and budgets**
3. **Discover activities and destinations through search**
4. **Receive cost breakdowns and visual calendars**
5. **Share their plans publicly or with friends**

The application must demonstrate proper use of relational databases to store and retrieve complex travel data such as user-specific itineraries, stops, activities, and estimated expenses. The system should also support dynamic user interfaces that adapt to each user's trip flow across desktop or mobile platforms.

---

## 3. Target Users

- **Independent & Multi-City Travelers**: Users planning travel across single or multiple global destinations who need structured schedules and cost visibility.
- **Budget-Conscious Vacationers**: Travelers needing accurate expense forecasting, daily budget tracking, and over-budget warnings.
- **Social / Community Travelers**: Individuals who want to share their travel itineraries with friends, family, or the community, or clone shared itineraries for their own trips.
- **Platform Administrators `[Optional in PDF]`**: Administrators tracking platform adoption, popular destinations, and user metrics.

---

## 4. Core User Journey

1. **Account Access**: User signs up / logs in to their personalized account.
2. **Dashboard Overview**: User views upcoming trips, budget highlights, and recommended destinations.
3. **Trip Initiation**: User clicks "Plan New Trip" to specify trip name, start/end dates, description, and optional cover photo.
4. **Destination & Activity Discovery**: User searches cities (filtering by country/region, viewing cost index and popularity) and selects activities (filtering by type, cost, duration).
5. **Itinerary Construction**: User adds multi-city stops, assigns travel dates, orders/reorders cities, and assigns activities to specific dates/times.
6. **Visualization & Budgeting**: User views the day-wise itinerary in list or interactive calendar/timeline view and reviews the automatic expense breakdown (transport, stay, activities, meals) with over-budget indicators.
7. **Sharing & Cloning**: User generates a public shareable URL. Friends view read-only itinerary or click "Copy Trip" to clone it into their own account.

---

## 5. Detailed Feature & Screen Requirements `[Explicitly Required by PDF]`

The application includes 13 major feature screens as specified in the problem statement:

### 1. Login / Signup Screen `[Explicitly Required by PDF]`
- **Description**: Entry point of the app allowing users to create or access their account.
- **Purpose**: Authenticate users to manage personal travel plans securely.
- **Key Functionality / Components**:
  - Email & password fields
  - Login button
  - Signup link / view toggle
  - "Forgot Password" link
  - Basic form validation (email format, password length, missing fields)

### 2. Dashboard / Home Screen `[Explicitly Required by PDF]`
- **Description**: Central hub showing upcoming trips, popular cities, and quick actions.
- **Purpose**: Allows users to navigate to their trips and explore travel inspiration.
- **Key Functionality / Components**:
  - Personalized welcome message
  - List of recent / upcoming trips
  - Prominent "Plan New Trip" button
  - Recommended destinations gallery
  - Budget highlights summary widget

### 3. Create Trip Screen `[Explicitly Required by PDF]`
- **Description**: Form to initiate a new trip by providing core trip parameters.
- **Purpose**: Begins the process of creating a personalized travel plan.
- **Key Functionality / Components**:
  - Trip name field
  - Start & end date pickers
  - Trip description text area
  - Cover photo upload / URL input `(optional)`
  - Save / Create Trip submission button

### 4. My Trips (Trip List) Screen `[Explicitly Required by PDF]`
- **Description**: List view of all trips created by the user with summary metadata.
- **Purpose**: Easily access, manage, and filter existing or upcoming trips.
- **Key Functionality / Components**:
  - Grid / list of trip cards showing: name, date range, destination count, thumbnail
  - Action buttons per card: Edit, View, Delete trip

### 5. Itinerary Builder Screen `[Explicitly Required by PDF]`
- **Description**: Interactive interface to add cities, dates, and activities for each stop.
- **Purpose**: Construct the full day-wise trip plan in an interactive format.
- **Key Functionality / Components**:
  - "Add Stop" button to append new destinations
  - City selector & travel date assigner per stop
  - Activity assignment per stop / day
  - City stop reordering mechanism (drag-and-drop or sequence reordering)

### 6. Itinerary View Screen `[Explicitly Required by PDF]`
- **Description**: Visual representation of the completed trip itinerary.
- **Purpose**: Review the full plan in a structured format (timeline or grouped by cities).
- **Key Functionality / Components**:
  - Day-wise layout showing schedule flow
  - City headers dividing destination legs
  - Activity blocks displaying time, duration, and estimated cost
  - View mode toggle (Calendar view vs List view)

### 7. City Search `[Explicitly Required by PDF]`
- **Description**: Search interface to find and add cities to a trip, with meta details like country, cost index, and popularity.
- **Purpose**: Discover and include relevant cities in the itinerary.
- **Key Functionality / Components**:
  - Search bar with live filtering
  - List / grid of cities displaying metadata (Country, Region, Cost Index, Popularity)
  - "Add to Trip" direct action button
  - Filter controls by country or region

### 8. Activity Search `[Explicitly Required by PDF]`
- **Description**: Browse and select things to do in each stop, categorized by interest or cost.
- **Purpose**: Enrich trips with experiences like sightseeing, food tours, or adventure activities.
- **Key Functionality / Components**:
  - Activity filters (by type/category, cost, duration)
  - Add / remove activity toggle buttons
  - Quick view modal / card displaying activity description and images

### 9. Trip Budget & Cost Breakdown Screen `[Explicitly Required by PDF]`
- **Description**: Summarized financial view showing estimated total cost and breakdowns.
- **Purpose**: Helps travelers stay informed and within budget constraints.
- **Key Functionality / Components**:
  - Cost breakdown by categories: Transport, Stay, Activities, Meals
  - Pie / Bar visual charts summarizing expense distribution
  - Calculated average cost per day
  - Automated visual alerts for over-budget days or total limit excess

### 10. Trip Calendar / Timeline Screen `[Explicitly Required by PDF]`
- **Description**: Calendar-based or vertical timeline view of the full itinerary.
- **Purpose**: Helps users visualize the journey and daily plan flow.
- **Key Functionality / Components**:
  - Interactive calendar component
  - Expandable day views detailing scheduled activities
  - Drag-to-reorder activities across times/days
  - Quick editing options for schedule adjustments

### 11. Shared/Public Itinerary View Screen `[Explicitly Required by PDF]`
- **Description**: Public web page displaying a shareable version of an itinerary.
- **Purpose**: Allows others to view, get inspired, or copy the trip structure.
- **Key Functionality / Components**:
  - Unique public URL generator / link display
  - Read-only itinerary summary view
  - Prominent "Copy Trip" button (clones trip into logged-in user's account)
  - Social media sharing shortcuts

### 12. User Profile / Settings Screen `[Explicitly Required by PDF]`
- **Description**: User settings page to update profile information and preferences.
- **Purpose**: Enables users to control their personal data, preferences, and privacy.
- **Key Functionality / Components**:
  - Editable user profile fields (Name, Profile Photo URL/upload, Email)
  - Language preference selector
  - Account deletion option ("Delete Account")
  - Saved destinations list / bookmarks

### 13. Admin / Analytics Dashboard `[Optional in PDF]`
- **Description**: Admin-only interface to track user trends, trip data, and platform usage.
- **Purpose**: Helps in monitoring app adoption, popular cities, and user behavior.
- **Key Functionality / Components**:
  - Data tables and visual charts of total trips created
  - Top visited cities & top selected activities analytics
  - User engagement and platform usage statistics
  - User management tools (view/deactivate user accounts)

---

## 6. Scope Categorization & Distinction

To ensure clarity during implementation, features are tagged as follows:

| Feature / Requirement | Category | Source |
| :--- | :--- | :--- |
| User Authentication (Signup/Login/Validation) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Dashboard with Recent Trips & "Plan New Trip" | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Create Trip (Name, Dates, Description) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| My Trips List View & Trip Management | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Multi-City Itinerary Builder (Stops & Cities) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| City Search & Metadata (Cost Index, Popularity) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Activity Search & Filters (Type, Cost, Duration) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Assign Activities to Stops/Days with Costs | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Itinerary View (Day-wise / Timeline) | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Trip Budget Breakdown & Daily Averages | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Public Shared Link & Read-only View | **P0 (MVP)** | `[Explicitly Required by PDF]` |
| Copy Shared Trip to User Account | **P1 (High)** | `[Explicitly Required by PDF]` |
| Visual Pie/Bar Charts for Budget Breakdown | **P1 (High)** | `[Explicitly Required by PDF]` |
| Overbudget Daily Visual Alerts | **P1 (High)** | `[Explicitly Required by PDF]` |
| Calendar View Toggle & Drag-to-Reorder | **P1 (High)** | `[Explicitly Required by PDF]` |
| Profile & Settings (Preferences, Saved List) | **P1 (High)** | `[Explicitly Required by PDF]` |
| Cover Photo Upload for Trips | **P2 (Optional)** | `[Explicitly Required by PDF]` |
| Admin / Analytics Dashboard | **P2 (Optional)** | `[Optional in PDF]` |
| Spring Boot + React + PostgreSQL Stack | Technical | `[Technical Recommendation]` |
| JWT Stateless Token Authentication | Technical | `[Technical Recommendation]` |

---

## 7. MVP Scope (Minimum Viable Product)

The MVP for the hackathon MUST deliver a single, complete end-to-end user flow:
1. User logs in.
2. User lands on Dashboard and clicks "Plan New Trip".
3. User creates a trip (e.g., "European Tour", Oct 10–Oct 20).
4. User searches and adds 2–3 cities (e.g., Paris, Rome).
5. User searches and assigns activities with costs to each stop.
6. User reviews the structured day-wise Itinerary View.
7. User checks the Budget Screen showing cost breakdown and average daily expense.
8. User generates a Public Link, opens it in read-only mode, and clones it using "Copy Trip".

---

## 8. Success Criteria

- **Completeness**: All 12 mandatory screens from the PDF exist in the application and function smoothly without breaking.
- **Relational Integrity**: Multi-city stops, activities, schedules, and costs are reliably stored and queried from PostgreSQL tables.
- **Usability & Aesthetics**: Responsive, clean UI with dynamic budget calculations and calendar/timeline visualizations.
- **Demo Readiness**: A presenter can complete the full travel planning user journey live in under 5 minutes without manual database or API intervention.
