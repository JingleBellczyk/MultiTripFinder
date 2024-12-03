import logging
import os

from amadeus import Client, ResponseError
from datetime import datetime, timedelta
from typing import List, Tuple
import asyncio

from dto import AlgorithmRequest, PlaceInSearchRequest, TransportMode, TransportModeLeg

amadeus_client_id = os.environ.get("AMADEUS_CLIENT_ID")
amadeus_client_secret = os.environ.get("AMADEUS_CLIENT_SECRET")

def extract_flight_details(flight: dict, end_datetime: datetime, origin: PlaceInSearchRequest, destination: PlaceInSearchRequest, passenger_count: int) -> dict:
    """
    - Pobieramy tylko potrzebne dane z odpowiedzi Amadeus API
    - Usuwamy połączenia, które kończą się po max_trip_duration 
    """
    try:
        duration = int(timedelta(hours=int(flight["itineraries"][0]["duration"][2:4]), minutes=int(flight["itineraries"][0]["duration"][5:7])).total_seconds() / 60)
        segments = flight["itineraries"][0]["segments"]

        # Wyciąganie szczegółów segmentów
        segment_details = []
        for segment in segments:
            segment_info = {
                "departure_time": datetime.strptime(segment["departure"]["at"], "%Y-%m-%dT%H:%M:%S"),
                "departure_city_code": segment["departure"]["iataCode"],
                "arrival_time": datetime.strptime(segment["arrival"]["at"], "%Y-%m-%dT%H:%M:%S"),
                "arrival_city_code": segment["arrival"]["iataCode"],
            }
            segment_details.append(segment_info)

        if segment_details[-1].arrival_time > end_datetime:
            return {}
        if flight["numberOfBookableSeats"] < passenger_count:
            return {}

        # Возврат отфильтрованных данных
        return {
            "origin_city": origin,
            "destination_city": destination,
            "departure_time": segment_details[0]["departure_time"],
            "arrival_time": segment_details[0]["arrival_time"][-1]["arrival_time"],
            "price": flight['price']['total'],
            "duration": duration,
            "segments": segment_details,
            "transport_modes": [TransportModeLeg(transport_mode=TransportMode.PLANE, duration=duration)] #list of transport modes + their durations
        }
    except KeyError as e:
        logging.info(f"Error extracting data: no such key {e}")
        return {}


async def fetch_amadeus_data(body: dict):
    # Инициализация Amadeus SDK
    amadeus = Client(
        client_id=amadeus_client_id,
        client_secret=amadeus_client_secret
    )

    try:
        response = amadeus.shopping.flight_offers_search.post(body)
        await asyncio.sleep(0.05)
        return response.data
    except ResponseError as error:
        logging.info(f"Error fetching flights: {error}")
        return []


async def process_flight_data(request: AlgorithmRequest, city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    tasks = []
    
    # Generacja maks daty końcowej
    start_date = request.trip_start_date
    max_duration = request.max_trip_duration
    end_datetime = start_date.strftime("%Y-%m-%d") + timedelta(days=max_duration, hours=23, minutes=59).strftime("T%H:%M:%S")

    # Generacja dat bazowych
    base_dates = []
    for i in range(0, max_duration, 7):
        base_date = (start_date + timedelta(days=i)).strftime("%Y-%m-%d")
        base_dates.append(base_date)

    # Создание задач для всех пар городов и диапазонов дат
    for origin, destination in city_pairs:
        if origin.city_code and destination.city_code:
            for base_date in base_dates:
                body = {
                    "currencyCode": "EUR",
                    "originDestinations": [
                        {
                            "id": "1",
                            "originLocationCode": origin.city_code,
                            "destinationLocationCode": destination.city_code,
                            "departureDateTimeRange": {
                                "date": base_date,
                                "dateWindow": "I3D"
                            }
                        }
                    ],
                    "travelers": [
                        {
                            "id": 1,
                            "travelerType": "ADULT"
                        }
                    ],
                    "sources": [
                        "GDS"
                    ],
                    "searchCriteria": {
                        "maxFlightOffers": 15,
                        "oneFlightOfferPerDay": True
                    }
                }
                tasks.append(fetch_amadeus_data(body), origin, destination)

    # Asynchroniczne wykoanie zapytań
    responses = await asyncio.gather(*tasks)

    # Filtracja wyników
    filtered_results = []
    for response, origin, destination in responses:
        if response:
            for flight in response:
                flight_details = extract_flight_details(flight, end_datetime, origin, destination, request.passenger_count)
                if flight_details:
                    filtered_results.append(flight_details)
