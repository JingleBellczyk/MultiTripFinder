import asyncio
import logging
import subprocess
from datetime import datetime, timedelta, date
from typing import List, Tuple

import aiohttp
from dto import AlgorithmRequest, PlaceInSearchRequest, Coordinates, TransportMode, CriteriaMode
from pydantic import BaseModel, Field
from typing import List, Optional

otp_graphql_endpoint = "http://localhost:8801/otp/gtfs/v1"
headers = {
    "Content-Type": "application/json"
}


def start_otp():
    global otp_process
    try:
        command = ["java", "-Xmx6G", "-jar", "./otp/otp-2.6.0-shaded.jar", "--load", "./otp/graph", "--port", "8801"]
        otp_process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True,
                                       encoding="utf-8")

        for line in iter(otp_process.stdout.readline, ""):
            logging.info(line.strip())
            if "Grizzly server running." in line:
                logging.info("OTP service started successfully.")
                return

    except Exception as e:
        logging.info(f"Failed to start OTP service: {e}")
        raise e


def stop_otp():
    global otp_process
    if otp_process:
        otp_process.terminate()
        otp_process.wait()
        logging.info("OTP process terminated")


async def fetch_otp_data(session, body):
    async with session.post(otp_graphql_endpoint, headers=headers, json=body) as response:
        response.raise_for_status()
        return await response.json()


async def process_pair(session, request: AlgorithmRequest, startPlace, endPlace):
    filtered_itineraries = []
    for i in range(request.max_trip_duration):
        date = request.trip_start_date + timedelta(days=i)
        request_body = create_otp_request_body(
            startPlace.station_coordinates.railway_station_coordinates,
            endPlace.station_coordinates.railway_station_coordinates,
            date,
            TransportMode.TRAIN,
        )
        try:
            response_json = await fetch_otp_data(session, request_body)
            plan = OTPResponse(**response_json["data"]).plan
            if request.optimization_criteria == CriteriaMode.DURATION:
                filtered_itineraries.extend(sorted(plan.itineraries, key=lambda x: x.duration)[:5])
            else:
                # Handle other optimization criteria as needed
                pass
        except Exception as e:
            print(f"Error fetching OTP data: {e}")
    return filtered_itineraries


async def find_routes_otp_async(request: AlgorithmRequest,
                                city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    final_itineraries = []
    tasks = []

    async with aiohttp.ClientSession() as session:
        for pair in city_pairs:
            startPlace, endPlace = pair
            tasks.append(
                process_pair(session, request, startPlace, endPlace)
            )
        results = await asyncio.gather(*tasks)

    # Flatten the results
    for itineraries in results:
        final_itineraries.extend(itineraries)

    return final_itineraries


def create_otp_request_body(fromCoordinates: Coordinates, toCoordinates: Coordinates, date_: date,
                            transportMode: TransportMode):
    transport = "RAIL" if transportMode == TransportMode.TRAIN else "BUS"
    request = f"""
        {{
          plan(
            from: {{lat: {fromCoordinates.lat}, lon: {fromCoordinates.lon}}}
            to: {{lat: {toCoordinates.lat}, lon: {toCoordinates.lon}}}
            date: "{map_date(date_).strftime("%Y-%m-%d")}"
            time: "00:00"
            searchWindow: "86400"
            numItineraries: 100
            transportModes: [{{mode: {transport}}}]
            minTransferTime: 1800
          ) {{
            itineraries {{
              start
              end
              duration
              numberOfTransfers
              legs {{
                from {{
                  name
                  lat
                  lon
                  departure {{
                    scheduledTime
                  }}
                }}
                to {{
                  name
                  lat
                  lon
                  arrival {{
                    scheduledTime
                  }}
                }}
                route {{
                  longName
                  shortName
                }}
                distance
                mode
              }}
            }}
          }}
        }}
    """
    return {"query": request}


def map_date(date_: date):
    schedule_start = datetime(2024, 11, 18)
    schedule_end = datetime(2024, 11, 24)
    target_day_of_week = date_.weekday()

    corresponding_date = schedule_start + timedelta(days=target_day_of_week)

    if schedule_start <= corresponding_date <= schedule_end:
        return corresponding_date
    else:
        raise ValueError("Target date's day of the week is not within the schedule range.")


class TimeInfo(BaseModel):
    scheduledTime: str


class LocationInfo(BaseModel):
    name: str
    lat: float
    lon: float
    departure: Optional[TimeInfo] = None
    arrival: Optional[TimeInfo] = None


class RouteInfo(BaseModel):
    longName: Optional[str]
    shortName: Optional[str]


class Leg(BaseModel):
    from_: LocationInfo = Field(alias="from")
    to: LocationInfo
    distance: float
    route: Optional[RouteInfo] = None
    mode: str

    class Config:
        allow_population_by_field_name = True


class Itinerary(BaseModel):
    start: str
    end: str
    duration: int
    numberOfTransfers: int
    legs: List[Leg]


class Plan(BaseModel):
    itineraries: List[Itinerary]


class OTPResponse(BaseModel):
    plan: Plan
