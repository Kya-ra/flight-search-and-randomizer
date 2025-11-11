# Backend

Spring Boot backend for the Budget Flight Finder application.

## Setup

### 1. Environment Variables


Create a `.env` file in the `backend/` directory (already gitignored):

```bash
cp .env.example .env
```

Edit `.env` and add your SerpAPI key:
```
SERPAPI_API_KEY=your_actual_key_here
```

Get your key from: https://serpapi.com/manage-api-key

### 2. Running the Application

**Option A: With .env file (using spring-boot-dotenv)**
```bash
./mvnw spring-boot:run
```

**Option B: With environment variable directly**
```bash
# Windows (PowerShell)
$env:SERPAPI_API_KEY="your_key_here"; ./mvnw spring-boot:run

# Linux/Mac
SERPAPI_API_KEY=your_key_here ./mvnw spring-boot:run
```


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
