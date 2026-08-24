UPDATE activities 
SET estimated_cost = estimated_cost * 100, currency = 'INR' 
WHERE currency = 'USD';

-- Assume trips.budget is implicitly USD based on previous application logic
UPDATE trips 
SET budget = budget * 100 
WHERE budget IS NOT NULL;

-- Assume trip_activities.custom_cost is implicitly USD
UPDATE trip_activities 
SET custom_cost = custom_cost * 100 
WHERE custom_cost IS NOT NULL;
