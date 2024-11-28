#Algorythm for finding the best route from city START to city END through the cities in the list CITIES (the order of the cities can be changed)
#Algorythm gets start date, how many days the trip can maximum take, the list of cities (city, country) and how many days user wants to spend in each city.
#Start date: we get date and assume time is 00:00, the latest departure time can be 24 hours later
#In each city we can spend tge specified number of days + 24 hours maximum
#The algorythm also gets preferable transport types (bus, train, plane) (optional) and optimization criteria (time or price)

#The algorythm uses Nomimatim API to get the coordinates of the biggest transport node in the city and its coordinates.
#The algorythm gets flight connections from the Amadeus API and above-ground paths from the OTP API.
#The algorytm has to find 10 best routes and return them in the form of list of cities + used connections with transport type and departure and arrival times.

#The algorytm will be implemented in Java (Spring Boot), but now its the first version in python.

#Implementation:

#Input data:
start = "Warsaw, Poland"
end = "Wroclaw, Poland"
cities = ["Berlin, Germany", "Venice, Italy", "Paris, France", "Oslo, Norway"]
days = [2, 1, 3, 2]
start_date = "2025-03-01"
max_days = 10
transport = None
optimization = "cost"

#Get the coordinates of the city
import requests
import json

def get_coordinates(city):
    query = f"{city} railway station"
    url = f"https://nominatim.openstreetmap.org/search?q={query}&format=json"
    response = requests.get(url)
    data = response.json()
    if len(data) == 0:
        return None
    return (data[0]['lat'], data[0]['lon'])

#Plan all possible permutations of cities
from itertools import permutations

def get_all_permutations(start, cities, end):
    #start zawsze na poczatku, end zawsze na koncu
    pairs = []
    for perm in permutations(cities):
        pairs.append([start] + list(perm) + [end])
    return pairs

#Get the flight connections
def get_flights(start, end, date):
    url = "https://test.api.amadeus.com/v2/shopping/flight-offers"
    headers = {
        'Authorization' : 'Bearer apikey',
        'Content-Type': 'application/json'
    }
    data = {
        "originLocationCode": start,
        "destinationLocationCode": end,
        "departureDate": date,
        "adults": 1,
        "max": 3,
        "currencyCode": "EUR"
    }
    response = requests.post(url, headers=headers, json=data)
    #get only important data
    flights = []
    for offer in response.json()['data']:
        flights.append({
            "departure": offer['itineraries'][0]['segments'][0]['departure']['at'],
            "arrival": offer['itineraries'][0]['segments'][-1]['arrival']['at'],
            "price": offer['price']['total']
        })
    return flights

#Get the above-ground connections from OTP (running on localhost:8080)
def get_connections(start, end, date):
    url = f"http://localhost:8080/otp/routers/default/plan?fromPlace={start}&toPlace={end}&date={date}&time=00:00:00&searchWindow=86400&mode=TRANSIT,WALK&maxWalkDistance=3000&numItineraries=50"
    response = requests.get(url)
    connections = []
    for it in response.json()['plan']['itineraries']:
        connections.append({
            "departure": it['startTime'],
            "arrival": it['endTime'],
            "duration": it['duration'],
            "cost": it['fare']['regular']['cents'],
            "transfers": len(it['legs']) - 1
        })
    return connections

#Get the routes
def get_routes(start, end, cities, days, start_date, max_days, transport, optimization):
    #get all permutations
    pairs = get_all_permutations(start, cities, end)
    #get the coordinates of the cities
    coords = []
    for city in cities:
        coords.append(get_coordinates(city))
    #get the best routes
    routes = []
    for pair in pairs:
        route = {
            "cities": pair,
            "connections": []
        }
        #get the flights
        flights = get_flights(pair[0], pair[1], start_date)
        for flight in flights:
            route['connections'].append({
                "from": pair[0],
                "to": pair[1],
                "type": "flight",
                "departure": flight['departure'],
                "arrival": flight['arrival'],
                "price": flight['price']
            })
        #get the above-ground connections
        for i in range(len(pair) - 1):
            connections = get_connections(pair[i], pair[i + 1], start_date)
            for connection in connections:
                route['connections'].append({
                    "from": pair[i],
                    "to": pair[i + 1],
                    "type": "above-ground",
                    "departure": connection['departure'],
                    "arrival": connection['arrival'],
                    "duration": connection['duration'],
                    "cost": connection['cost'],
                    "transfers": connection['transfers']
                })
        routes.append(route)
    return routes

routes = get_routes(start, end, cities, days, start_date, max_days, transport, optimization)

#Sort the routes
def sort_routes(routes, optimization):
    if optimization == "cost":
        return sorted(routes, key=lambda x: sum([c['price'] for c in x['connections']]))
    else:
        return sorted(routes, key=lambda x: sum([c['duration'] for c in x['connections']]))

sorted_routes = sort_routes(routes, optimization)

#Return the best routes
def return_best_routes(routes, n):
    return routes[:n]

best_routes = return_best_routes(sorted_routes, 10)