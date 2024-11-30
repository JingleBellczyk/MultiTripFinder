import logging
import os

from amadeus import Client, ResponseError
from datetime import datetime, timedelta
from typing import List, Tuple
import asyncio
from models import RouteRequest

amadeus_client_id = os.environ.get("AMADEUS_CLIENT_ID")
amadeus_client_secret = os.environ.get("AMADEUS_CLIENT_SECRET")


def calculate_date_window(current_date: datetime, days_left: int) -> Tuple[str, datetime]:
    """
    Рассчитывает date_window и корректирует current_date для базовой даты.
    :param current_date: Текущая дата начала диапазона.
    :param days_left: Количество оставшихся дней для обработки.
    :return: (date_window, updated_current_date)
    """
    if days_left >= 7:
        date_window = "I3D"  # ±3 дня
        updated_date = current_date + timedelta(days=3)  # Центральная дата
    elif 4 <= days_left < 7:
        date_window = "I2D"  # ±2 дня
        updated_date = current_date + timedelta(days=2)  # Центральная дата
    elif 1 < days_left <= 3:
        date_window = f"P{days_left - 1}D"  # Только вперед
        updated_date = current_date  # Для короткого периода базовая дата остается начальной
    elif days_left == 1:
        date_window = "P1D"
        updated_date = current_date
    else:
        raise ValueError("Invalid number of days left")

    return date_window, updated_date


def filter_flights_by_date(flights, start_date: str, max_days: int) -> List[dict]:
    """
    Фильтрует рейсы, исключая те, у которых departure позже start_date + max_days.
    :param flights: Список всех найденных рейсов.
    :param start_date: Начальная дата в формате YYYY-MM-DD.
    :param max_days: Максимальное количество дней с начала.
    :return: Отфильтрованный список рейсов.
    """
    start_date = datetime.strptime(start_date, "%Y-%m-%d")
    end_date = start_date + timedelta(days=max_days)

    # Фильтрация рейсов
    filtered_flights = [
        flight for flight in flights
        if datetime.strptime(flight["itineraries"][0]["segments"][0]["departure"]["at"][:10], "%Y-%m-%d") <= end_date
    ]
    return filtered_flights


def extract_flight_details(flight: dict) -> dict:
    """
    Извлекает и форматирует нужные данные из одного ответа API.
    :param flight: Один рейс из ответа Amadeus API.
    :return: Словарь с отфильтрованной информацией.
    """
    try:
        # Основные данные о рейсе
        price = f"{flight['price']['total']} {flight['price']['currency']}"
        duration = flight["itineraries"][0]["duration"]
        segments = flight["itineraries"][0]["segments"]

        # Данные о сегментах
        segment_details = []
        for segment in segments:
            segment_info = {
                "airline_and_flight": f"{segment['carrierCode']} {segment['number']}",
                "departure_time": segment["departure"]["at"],
                "departure_city": segment["departure"]["iataCode"],
                "arrival_time": segment["arrival"]["at"],
                "arrival_city": segment["arrival"]["iataCode"],
                "aircraft": segment["aircraft"]["code"]
            }
            segment_details.append(segment_info)

        # Возврат отфильтрованных данных
        return {
            "price": price,
            "duration": duration,
            "segments": segment_details,
            "validatingAirline": flight["validatingAirlineCodes"][0] if flight["validatingAirlineCodes"] else None
        }
    except KeyError as e:
        logging.info(f"Ошибка извлечения данных: отсутствует ключ {e}")
        return {}


async def fetch_amadeus_data(body: dict):
    # Инициализация Amadeus SDK
    amadeus = Client(
        client_id=amadeus_client_id,
        client_secret=amadeus_client_secret
    )

    try:
        response = amadeus.shopping.flight_offers_search.post(body)
        await asyncio.sleep(0.1)  # Задержка для предотвращения блокировки
        return response.data
    except ResponseError as error:
        logging.info(f"Error fetching flights: {error}")
        return []


# Основная функция обработки маршрута
async def process_flight_data(request: RouteRequest):
    global filtered_results
    tasks = []

    # Создание пар городов
    if request.cities_to_visit != []:
        city_pairs = [(request.start_city, city) for city in request.cities_to_visit]
        city_pairs += [(city_a, city_b) for i, city_a in enumerate(request.cities_to_visit) for city_b in
                       request.cities_to_visit if city_a != city_b]
        city_pairs += [(city, request.end_city) for city in request.cities_to_visit]
    else:
        city_pairs = [(request.start_city, request.end_city)]

    logging.info("City pairs:")
    for city_pair in city_pairs:
        logging.info(city_pair[0].name, " - ", city_pair[1].name)

    # Генерация base_date и date_window
    current_date = datetime.strptime(request.start_date, "%Y-%m-%d")
    days_left = request.max_days
    date_ranges = []

    while days_left > 0:
        date_window, updated_date = calculate_date_window(current_date, days_left)
        base_date = updated_date.strftime("%Y-%m-%d")
        date_ranges.append((base_date, date_window))
        current_date += timedelta(days=7)
        days_left -= 7

    # Создание задач для всех пар городов и диапазонов дат
    for origin, destination in city_pairs:
        for base_date, date_window in date_ranges:
            body = {
                "currencyCode": "EUR",
                "originDestinations": [
                    {
                        "id": "1",
                        "originLocationCode": origin.code,
                        "destinationLocationCode": destination.code,
                        "departureDateTimeRange": {
                            "date": base_date,
                            "dateWindow": date_window
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
            tasks.append(fetch_amadeus_data(body))

    # Выполнение запросов параллельно
    responses = await asyncio.gather(*tasks)

    # Фильтрация и сохранение результатов
    filtered_results = []
    for response in responses:
        if response:
            for flight in response:
                flight_details = extract_flight_details(flight)
                if flight_details:
                    filtered_results.append(flight_details)
