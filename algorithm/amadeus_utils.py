import logging
import os
from amadeus import Client, ResponseError
from datetime import datetime, timedelta
from typing import List, Tuple, Any
import asyncio
import pytz
from timezonefinder import TimezoneFinder

from dto import AlgorithmRequest, PlaceInSearchRequest, TransportMode, TransportModeLeg, Connection

amadeus_client_id = os.environ.get("AMADEUS_CLIENT_ID")
amadeus_client_secret = os.environ.get("AMADEUS_CLIENT_SECRET")

gmt_plus_one = pytz.timezone("Europe/Berlin")  # GMT+1 timezone
timezone_finder = TimezoneFinder()


def convert_to_gmt_plus_one(local_time: datetime, city: PlaceInSearchRequest) -> datetime:
    """Convert local time to GMT+1 based on the city's coordinates."""
    try:
        local_tz_name = timezone_finder.timezone_at(lat=float(city.station_coordinates.lat),
                                                    lng=float(city.station_coordinates.lon))
        local_tz = pytz.timezone(local_tz_name)
        local_time = local_tz.localize(local_time)
        return local_time.astimezone(gmt_plus_one)
    except Exception as e:
        logging.error(f"Error converting time for city {city.city}: {e}")
        return local_time


def extract_flight_details(flight: dict, end_datetime: datetime, origin: PlaceInSearchRequest,
                           destination: PlaceInSearchRequest, passenger_count: int) -> Connection:
    try:
        duration = int(timedelta(hours=int(flight["itineraries"][0]["duration"][2:4]),
                                 minutes=int(flight["itineraries"][0]["duration"][5:7])).total_seconds() / 60)
        segments = flight["itineraries"][0]["segments"]

        segment_details = []
        for segment in segments:
            departure_time = datetime.strptime(segment["departure"]["at"], "%Y-%m-%dT%H:%M:%S")
            arrival_time = datetime.strptime(segment["arrival"]["at"], "%Y-%m-%dT%H:%M:%S")
            segment_info = {
                "departure_time": departure_time,
                "departure_city_code": segment["departure"]["iataCode"],
                "arrival_time": arrival_time,
                "arrival_city_code": segment["arrival"]["iataCode"],
            }
            segment_details.append(segment_info)

        if segment_details[-1]["arrival_time"] > end_datetime:
            return {}
        if flight["numberOfBookableSeats"] < passenger_count:
            return {}

        return Connection(
            origin_city=origin,
            destination_city=destination,
            departure_time=convert_to_gmt_plus_one(segment_details[0]["departure_time"], origin),
            arrival_time=convert_to_gmt_plus_one(segment_details[-1]["arrival_time"], destination),
            price=flight['price']['total'],
            duration=duration,
            segments=segment_details,
            transport_type=[TransportModeLeg(transport_mode=TransportMode.PLANE, duration=duration)]
        )
    except KeyError as e:
        logging.info(f"Error extracting data: no such key {e}")
        return {}


async def fetch_amadeus_data(body: dict, trip_start_date: str, origin: PlaceInSearchRequest,
                             destination: PlaceInSearchRequest, passenger_count: int) -> \
        tuple[Any, str, PlaceInSearchRequest, PlaceInSearchRequest, int] | list[Any]:
    amadeus = Client(
        client_id=amadeus_client_id,
        client_secret=amadeus_client_secret
    )

    try:
        response = amadeus.shopping.flight_offers_search.post(body)
        await asyncio.sleep(0.05)
        return response.data, trip_start_date, origin, destination, passenger_count
    except ResponseError as error:
        logging.info(f"Error fetching flights: {error}")
        return []


async def process_start_city_pairs(request: AlgorithmRequest,
                                   start_city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    tasks = []
    for origin, destination in start_city_pairs:
        if origin.city_code and destination.city_code:
            body = {
                "currencyCode": "EUR",
                "originDestinations": [
                    {
                        "id": "1",
                        "originLocationCode": origin.city_code,
                        "destinationLocationCode": destination.city_code,
                        "departureDateTimeRange": {
                            "date": request.trip_start_date
                        }
                    }
                ],
                "travelers": [
                    {"id": 1, "travelerType": "ADULT"}
                ],
                "sources": ["GDS"],
                "searchCriteria": {
                    "maxFlightOffers": 15,
                    "oneFlightOfferPerDay": True
                }
            }
            tasks.append(
                fetch_amadeus_data(body, request.trip_start_date, origin, destination, request.passenger_count))
    return await asyncio.gather(*tasks)


async def process_visit_city_pairs(request: AlgorithmRequest,
                                   visit_city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    tasks = []
    base_dates = [datetime.strptime(request.trip_start_date, "%Y-%m-%d") + timedelta(days=i) for i in range(request.max_trip_duration)]
    for origin, destination in visit_city_pairs:
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
                                "date": base_date.strftime("%Y-%m-%d"),
                                "dateWindow": "I3D"
                            }
                        }
                    ],
                    "travelers": [
                        {"id": 1, "travelerType": "ADULT"}
                    ],
                    "sources": ["GDS"],
                    "searchCriteria": {
                        "maxFlightOffers": 15,
                        "oneFlightOfferPerDay": True
                    }
                }
                tasks.append(
                    fetch_amadeus_data(body, request.trip_start_date, origin, destination, request.passenger_count))
    return await asyncio.gather(*tasks)


async def process_end_city_pairs(request: AlgorithmRequest,
                                 end_city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    tasks = []
    total_min_stay_hours = sum(
        place.stay_hours_min for place in request.places_to_visit) + request.start_place.stay_hours_min
    base_date = datetime.strptime(request.trip_start_date, "%Y-%m-%d") + timedelta(days=total_min_stay_hours // 24)
    for origin, destination in end_city_pairs:
        if origin.city_code and destination.city_code:
            body = {
                "currencyCode": "EUR",
                "originDestinations": [
                    {
                        "id": "1",
                        "originLocationCode": origin.city_code,
                        "destinationLocationCode": destination.city_code,
                        "departureDateTimeRange": {
                            "date": base_date.strftime("%Y-%m-%d"),
                            "dateWindow": "I3D"
                        }
                    }
                ],
                "travelers": [
                    {"id": 1, "travelerType": "ADULT"}
                ],
                "sources": ["GDS"],
                "searchCriteria": {
                    "maxFlightOffers": 15,
                    "oneFlightOfferPerDay": True
                }
            }
            tasks.append(
                fetch_amadeus_data(body, request.trip_start_date, origin, destination, request.passenger_count))
    return await asyncio.gather(*tasks)


async def process_flight_data(request: AlgorithmRequest, start_city_pairs, visit_city_pairs, end_city_pairs):
    start_results = await process_start_city_pairs(request, start_city_pairs)
    visit_results = await process_visit_city_pairs(request, visit_city_pairs)
    end_results = await process_end_city_pairs(request, end_city_pairs)

    results = start_results + visit_results + end_results
    # apply extract_flight_details to all results
    final_results = []
    for result in results:
        flight, end_datetime, origin, destination, passenger_count = result
        if result:
            final_results.append(extract_flight_details(flight, end_datetime, origin, destination, passenger_count))

    return final_results
