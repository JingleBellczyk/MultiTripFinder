import logging
import networkx as nx
from typing import Dict, Any, List, Optional, Tuple
from dto import PlaceInSearchRequest, TransportMode, CriteriaMode, Connection
from datetime import datetime, timedelta
import heapq  # Для хранения 5 лучших маршрутов


# Функция для добавления узлов (городов) в граф
def add_city_to_graph(graph: nx.DiGraph, city: PlaceInSearchRequest):
    graph.add_node(city)


# Функция для расчета веса для ребра графа
def calculate_edge_weight(connection: Connection, preferred_transport: Optional[TransportMode],
                          optimization_criteria: CriteriaMode) -> float:
    primary_weight = connection.duration if optimization_criteria == CriteriaMode.DURATION else connection.price

    transport_score_sum = 0
    total_duration = 0
    for leg in connection.transport_modes:
        total_duration += leg.duration
        if leg.transport_mode == preferred_transport:
            transport_score_sum += leg.duration

    transport_score = transport_score_sum / total_duration if total_duration else 0
    transport_weight_component = (1 - transport_score) * 0.4286 * primary_weight
    combined_weight = primary_weight + transport_weight_component
    return combined_weight


# Функция для добавления маршрута (рёбер) между городами в граф
def add_transfer_to_graph(graph: nx.DiGraph, connection: Connection, passenger_count: int, weight: float):
    graph.add_edge(
        connection.origin_city,
        connection.destination_city,
        transport_type=connection.transport_type,
        cost=connection.price * passenger_count,
        duration=connection.duration,
        depart_time=connection.departure_time,
        arrival_time=connection.arrival_time,
        segments=connection.segments,
        weight=weight  # Добавляем рассчитанный вес
    )


# Функция для поиска 5 лучших маршрутов
def find_optimal_routes(graph: nx.DiGraph, start: PlaceInSearchRequest, end: PlaceInSearchRequest,
                        start_date: datetime) -> List[Tuple[List[str], float]]:
    best_routes = []

    def dfs(current_city, visited, current_cost, current_route, current_time):
        nonlocal best_routes

        logging.info(f"Bieżące miasto: {current_city}, Bieżący koszt: {current_cost}, Bieżąca trasa: {current_route}")

        # Если маршрут завершён
        if len(visited) == len(graph.nodes) and current_city == end:
            heapq.heappush(best_routes, (-current_cost, list(current_route)))
            if len(best_routes) > 5:
                heapq.heappop(best_routes)  # Удаляем маршрут с наименьшей ценностью
            logging.info(f"Znaleziona cała trasa: {current_route} z kosztem: {current_cost}")
            return

        # Проверяем, что конечный город не посещается до последнего шага
        if current_city == end and len(visited) < len(graph.nodes):
            return

        # Обход соседей
        for edge in graph.edges(current_city, data=True):
            next_city = edge[1]
            if next_city not in visited:
                next_edge = edge[2]
                depart_time_dt = datetime.strptime(next_edge['depart_time'], "%Y-%m-%dT%H:%M:%S")

                # Проверка на соответствие ограничениям пребывания
                if is_valid_stay(graph, current_route, next_edge['depart_time'], current_time):
                    new_cost = current_cost + next_edge['weight']
                    new_route = list(current_route)
                    new_route.append(next_city)

                    # Рекурсивно посещаем следующий город
                    dfs(next_city, visited.union({next_city}), new_cost, new_route, depart_time_dt)

    dfs(start, {start}, 0, [start], start_date)

    # Конвертируем маршруты в понятный формат
    return [(route, -cost) for cost, route in sorted(best_routes, reverse=True)]


# Функция для проверки, соответствует ли время отправления ограничениям на пребывание в городе
def is_valid_stay(graph: nx.DiGraph, current_route: List[str], depart_time: str, current_time: datetime) -> bool:
    try:
        depart_time_dt = datetime.strptime(depart_time, "%Y-%m-%dT%H:%M:%S")
    except ValueError:
        logging.info(f"Błąd przekształcenia depart_time: {depart_time}")
        return False

    if len(current_route) == 1:
        latest_departure = current_time + timedelta(hours=24)
        valid = depart_time_dt <= latest_departure
        logging.info(
            f"Sprawdzenie pierwszego miasta. depart_time: {depart_time_dt}, Dopuszczalny depart_time: {latest_departure}, Wynik: {valid}")
        return valid

    last_city = current_route[-1]
    stay_hours_min = graph.nodes[last_city]['city'].stay_hours_min
    stay_hours_max = graph.nodes[last_city]['city'].stay_hours_max

    previous_edge = graph.get_edge_data(current_route[-2], last_city)
    if not previous_edge:
        logging.info(f"Nie ma przedniej krawędzi między {current_route[-2]} a {last_city}")
        return False

    arrival_time = datetime.strptime(previous_edge['arrival_time'], "%Y-%m-%dT%H:%M:%S")
    logging.info(f"Znaleziono arrival_time dla {last_city}: {arrival_time}")

    earliest_departure = arrival_time + timedelta(hours=stay_hours_min)
    latest_departure = arrival_time + timedelta(hours=stay_hours_max)
    valid = earliest_departure <= depart_time_dt <= latest_departure
    logging.info(
        f"Sprawdzenie czasu pobytu w mieście {last_city}. Czas przyjazdu: {arrival_time}, Czas odjazdu: {depart_time_dt}, Dopuszczalny zakres: ({earliest_departure}, {latest_departure}), Wynik: {valid}")

    if depart_time_dt < earliest_departure:
        logging.info(f"Godzina odbycia jest zbyt wczesna. Dostosujemy się do możliwego minimum: {earliest_departure}")
        return False

    return valid


def process_connections_data(start_city: PlaceInSearchRequest, end_city: PlaceInSearchRequest,
                             cities_to_visit: List[PlaceInSearchRequest], passenger_count: int,
                             preferred_transport: Optional[TransportMode], optimization_criteria: CriteriaMode, start_date: datetime,
                             otp_data: List[Connection], amadeus_data: List[Connection]):
    graph = nx.DiGraph()

    # Добавляем начальный, конечный города и города для посещения в граф
    add_city_to_graph(graph, start_city)
    for city in cities_to_visit:
        add_city_to_graph(graph, city)
    add_city_to_graph(graph, end_city)

    # Обрабатываем данные о соединениях и добавляем маршруты (рёбра) в граф
    for connection in amadeus_data + otp_data:
        weight = calculate_edge_weight(connection, preferred_transport, optimization_criteria)
        add_transfer_to_graph(graph, connection, passenger_count, weight)

    results = find_optimal_routes(graph, start_city, end_city, start_date)

    return results
