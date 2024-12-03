import logging
import networkx as nx
from typing import Dict, Any, List, Optional, Tuple
from dto import PlaceInSearchRequest, TransportMode, CriteriaMode, Connection
from datetime import datetime, timedelta

# Функция для добавления узлов (городов) в граф
def add_city_to_graph(graph: nx.DiGraph, city: PlaceInSearchRequest):
    graph.add_node(city)


# Функция для добавления маршрута (рёбер) между городами в граф
def add_transfer_to_graph(graph: nx.DiGraph, edge: Connection):

    graph.add_edge(
        edge.origin_city,
        edge.destination_city,
        transport_type=edge.transport_type,
        cost=edge.price,
        duration=edge.duration,
        depart_time=edge.depart_time,
        arrival_time=edge.arrival_time,
        segments=edge.segments
    )


# Функция для поиска оптимального маршрута от узла S до узла E, проходящего через все узлы ровно один раз
def find_optimal_route(graph: nx.DiGraph, start: PlaceInSearchRequest, end: PlaceInSearchRequest, start_date: datetime) -> Tuple[List[str], float]:
    best_route = None
    best_cost = float('inf')

    def dfs(current_city, visited, current_cost, current_route, current_time):
        nonlocal best_route, best_cost

        logging.info(f"Bieżące miasto: {current_city}, Bieżący koszt: {current_cost}, Bieżąca trasa: {current_route}")

        if len(visited) == len(graph.nodes) and current_city == end.name:
            if current_cost < best_cost:
                best_cost = current_cost
                best_route = list(current_route)
            logging.info(f"Znaleziona cała trasa: {current_route} z kosztem: {current_cost}")
            return

        for edge in graph.edges(current_city, data=True):
            next_city = edge[1]
            if next_city not in visited:
                next_edge = edge[2]
                depart_time_dt = datetime.strptime(next_edge['depart_time'], "%Y-%m-%dT%H:%M:%S")

                # Проверка на соответствие ограничениям пребывания
                if is_valid_stay(current_route, next_edge['depart_time'], current_time):
                    new_cost = current_cost + next_edge['cost']
                    new_route = list(current_route)
                    new_route.append(next_city)

                    # Рекурсивно посещаем следующий город
                    dfs(next_city, visited.union({next_city}), new_cost, new_route, depart_time_dt)

    dfs(start.name, {start.name}, 0, [start.name], start_date)
    return best_route, best_cost


# Функция для проверки, соответствует ли время отправления ограничениям на пребывание в городе
def is_valid_stay(graph: nx.DiGraph, current_route: List[str], depart_time: str, current_time: datetime) -> bool:
    try:
        depart_time_dt = datetime.strptime(depart_time, "%Y-%m-%dT%H:%M:%S")
    except ValueError:
        logging.info(f"Błąd przekształcenia depart_time: {depart_time}")
        return False

    if len(current_route) == 1:
        # Проверяем первый город: мы должны выехать не позднее, чем через 24 часа от указанной даты с автоматически добавленным временем 00:00
        latest_departure = start_date + timedelta(hours=24)
        valid = depart_time_dt <= latest_departure
        logging.info(
            f"Sprawdzenie pierwszego miasta. depart_time: {depart_time_dt}, Dopuszczalny depart_time: {latest_departure}, Wynik: {valid}")
        return valid

    last_city = current_route[-1]
    stay_hours_min = graph.nodes[last_city]['stay_hours_min']
    stay_hours_max = graph.nodes[last_city]['stay_hours_max']

    # Получаем время прибытия в город
    previous_edge = graph.get_edge_data(current_route[-2], last_city)

    if not previous_edge:
        logging.info(f"Nie ma przedniej krawędzi między {current_route[-2]} a {last_city}")
        return False

    arrival_time = datetime.strptime(previous_edge['arrival_time'], "%Y-%m-%dT%H:%M:%S")
    logging.info(f"Znaleziono arrival_time dla {last_city}: {arrival_time}")

    # Проверяем, что время отправления находится в допустимом диапазоне пребывания
    earliest_departure = arrival_time + timedelta(hours=stay_hours_min)
    latest_departure = arrival_time + timedelta(hours=stay_hours_max)
    valid = earliest_departure <= depart_time_dt <= latest_departure
    logging.info(
        f"Sprawdzenie czasu pobytu w mieście {last_city}. Czas przyjazdu: {arrival_time}, Czas odjazdu: {depart_time_dt}, Dopuszczalny zakres: ({earliest_departure}, {latest_departure}), Wynik: {valid}")

    # Исправление: если текущее время отправления не удовлетворяет минимальному времени пребывания, оно должно соответствовать минимуму
    if depart_time_dt < earliest_departure:
        logging.info(f"Godzina odbycia jest zbyt wczesna. Dostosujemy się do możliwego minimum: {earliest_departure}")
        return False

    return valid

def process_connections_data(start_city: PlaceInSearchRequest, end_city: PlaceInSearchRequest,
                             cities_to_visit: List[PlaceInSearchRequest], passenger_count: int,
                             preferred_transport: Optional[TransportMode], optimization_criteria: CriteriaMode,
                             otp_data: List[Dict[str, Any]], amadeus_data: List[Dict[str, Any]]): 
    graph = nx.DiGraph()
    
    add_city_to_graph(start_city)
    for city in cities_to_visit:
        add_city_to_graph(city)
    add_city_to_graph(end_city)

    for connection in amadeus_data:
        origin = connection['origin_city']
        destination = connection['destination_city']
        transport_type = connection['transport_type']
        depart_time = connection['departure_time']
        arrival_time = connection['arrival_time']
        fair = connection['price']
        duration = connection['duration']
        segments = connection['segments']

        add_transfer_to_graph(origin, destination, transport_type, depart_time, arrival_time, fair, duration, segments)