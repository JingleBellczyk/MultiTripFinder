from pydantic import BaseModel
from typing import List, Tuple, Optional
from enum import Enum

# Модель данных для города
class City(BaseModel):
    name: str
    country: str
    coordinates: Tuple[float, float]  # [latitude, longitude]
    code: Optional[str] = None  # Код аэропорта, если есть
    stay_hours_min: int  # Минимальное время пребывания в часах
    stay_hours_max: int  # Максимальное время пребывания в часах

# Перечисление для видов транспорта
class TransportMode(str, Enum):
    rail = "rail"
    bus = "bus"
    plane = "plane"
    none = "none"

# Модель данных для входного запроса
class RouteRequest(BaseModel):
    start_date: str
    start_city: City
    end_city: City
    cities_to_visit: List[City]
    criteria: str  # "time" или "cost"
    preferred_transport: TransportMode
    max_days: int
    passengers: int