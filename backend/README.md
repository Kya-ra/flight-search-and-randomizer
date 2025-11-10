# Backend

Spring Boot backend for the Budget Flight Finder application.

# Model
Relationships Summary

User → UserPreference: 1–1

User → SearchHistory: 1–many

SearchHistory → CachedFlightResult: 1–many

Booking → User: many–1

Destination:

Note: 
- UserPreference store max budget, prefered climate ...
- SearchHistory (maybe the last 5 or 10 searches ?)
- CachedFlightResult store recently flights, so that avoid to call API multiple times. These flights are stored to the SearchHistory
