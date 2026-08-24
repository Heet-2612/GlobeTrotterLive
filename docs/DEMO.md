# GlobeTrotter — Live Hackathon Demo Script

> **Document Status**: Presentation Guide  
> **Source of Truth**: GlobeTrotter PRD & Problem Statement  

---

## 1. Persona & Narrative Setup

- **User Story**:  
  *"Meet Sarah, a traveler planning a 7-day multi-city trip across Europe (Paris and Rome). She needs to discover destinations, pick activities, keep track of daily dates and times, stay strictly within her $2,500 budget, and share her travel itinerary with her travel partner."*

- **Demo Goal**:  
  Demonstrate a complete, unbroken end-to-end trip planning user journey in **under 4 minutes**, highlighting GlobeTrotter's multi-city builder, automated budget calculation, timeline visualization, and public sharing.

---

## 2. Step-by-Step Demo Flow

| Step | Action by Presenter | Feature Demonstrated | What Judges Should See |
| :--- | :--- | :--- | :--- |
| **1. Access** | Open app, enter credentials, click **Login**. | **Login / Signup Screen** | Clean authentication screen; smooth transition to dashboard upon validation. |
| **2. Dashboard** | Highlight welcome banner, recent trips, and budget widgets. Click **"Plan New Trip"**. | **Dashboard / Home Screen** | Personalized hub showing trip cards, recommended destinations, and clear primary CTA. |
| **3. Create Trip** | Input *"Euro Odyssey 2026"*, dates `Oct 10 - Oct 17`, description. Click **Save**. | **Create Trip Screen** | Intuitive trip metadata form; instant redirection to the builder interface. |
| **4. City Search** | Search for *"Paris"* and *"Rome"*. Review country, cost index (4.2), and popularity (95). Click **"Add to Trip"**. | **City Search** | Dynamic city discovery with live filters, cost index badges, and region tags. |
| **5. Build Stop Legs** | Assign Paris (`Oct 10-14`) and Rome (`Oct 14-17`). Reorder stops if needed. | **Itinerary Builder Screen** | Interactive stop sequence list with dates and drag/reorder capability. |
| **6. Activity Search** | Search activities in Paris (*Eiffel Tower Tour*, *Louvre Visit*) and Rome (*Colosseum Tour*). Filter by type (Sightseeing) and cost. Click **Add Activity**. | **Activity Search** | Categorized activity list with duration, cost estimates, and add/remove buttons. |
| **7. View Itinerary** | Open **Itinerary View**. Show day-wise schedule blocks with assigned times and city headers. | **Itinerary View Screen** | Structured day-by-day plan displaying time, cost, and city leg transitions. |
| **8. Check Budget** | Open **Budget Screen**. Highlight total cost ($2,150), transport/stay/activity breakdown pie chart, daily average ($307/day), and safety alert checks. | **Trip Budget & Cost Breakdown** | Automated financial summary chart, daily averages, and over-budget indicator logic. |
| **9. Visual Timeline** | Toggle to **Timeline / Calendar View**. Expand a day view and demonstrate drag-to-reorder activity time. | **Trip Calendar / Timeline** | Interactive calendar/timeline view showing daily activity blocks and reordering. |
| **10. Share & Clone** | Click **"Share Trip"** to generate public URL. Open link in incognito/new window. Click **"Copy Trip"**. | **Shared / Public View Screen** | Read-only public page with summary details and instant trip cloning into a new account. |

---

## 3. What to Emphasize for Judges

1. **Relational Database Design**: Highlight how multi-city stops, ordered activities, dates, and budget breakdown categories dynamically link together in PostgreSQL.
2. **Automated Budget Intelligence**: Emphasize how activity costs automatically compute daily averages and update category breakdowns without manual math.
3. **End-to-End User Experience**: Show that the user can go from zero to a fully planned, shareable multi-city trip in minutes.
4. **Community Sharing ("Copy Trip")**: Demonstrate how shared itineraries act as reusable templates for other travelers.

---

## 4. Features NOT to Demonstrate (If Incomplete)

- Do **NOT** navigate to the Admin Dashboard if seed analytics data is incomplete.
- Do **NOT** attempt image uploads if relying on fallback image URLs.
- Do **NOT** spend time editing user profile settings during the core pitch.
