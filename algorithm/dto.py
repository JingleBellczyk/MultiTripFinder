from typing import List, Optional
from datetime import datetime, date
from enum import Enum
from pydantic import BaseModel


# Enums
class TransportMode(str, Enum):
    BUS = "BUS"
    TRAIN = "TRAIN"
    PLANE = "PLANE"


class CriteriaMode(str, Enum):
    PRICE = "PRICE"
    DURATION = "DURATION"


# Models
class TransportModeLeg(BaseModel):
    transport_mode: TransportMode
    duration: int


class Coordinates(BaseModel):
    lat: str
    lon: str


class StationCoordinates(BaseModel):
    railway_station_coordinates: Optional[Coordinates]
    bus_station_coordinates: Optional[Coordinates]


class PlaceInSearchRequest(BaseModel):
    country: str
    city: str
    station_coordinates: StationCoordinates
    city_code: Optional[str]
    stay_hours_min: int
    stay_hours_max: int


class AlgorithmRequest(BaseModel):
    start_place: PlaceInSearchRequest
    end_place: PlaceInSearchRequest
    places_to_visit: List[PlaceInSearchRequest]
    passenger_count: int
    trip_start_date: str
    max_trip_duration: int
    preferred_transport: Optional[TransportMode]
    optimization_criteria: CriteriaMode


class Connection(BaseModel):
    origin_city: PlaceInSearchRequest
    destination_city: PlaceInSearchRequest
    departure_time: datetime
    arrival_time: datetime
    price: float
    duration: int
    segments: List[dict]
    transport_type: List[TransportModeLeg]
