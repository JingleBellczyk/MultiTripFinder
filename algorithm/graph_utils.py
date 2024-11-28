import networkx as nx
from typing import Dict, Any, List
from models import City
from datetime import datetime, timedelta

# Создаем направленный граф для хранения информации о маршрутах
route_graph = nx.DiGraph()
criteria = None
preferred_transport = None
start_date_global = None

def set_optimal_criteria(new_criteria: str, new_preferred_transport: str, start_date: str):
    global criteria, preferred_transport, start_date_global
    criteria = new_criteria
    preferred_transport = new_preferred_transport
    start_date_global = datetime.strptime(start_date, "%Y-%m-%dT%H:%M:%S")

# Функция для добавления узлов (городов) в граф
def add_city_to_graph(city: City):
    route_graph.add_node(
        city.name,
        country=city.country,
        stay_hours_min=city.stay_hours_min,
        stay_hours_max=city.stay_hours_max
    )

# Функция для добавления маршрута (рёбер) между городами в граф
def add_transfer_to_graph(origin: City, destination: City, transport_type: str, depart_time: str, arrival_time: str, fair: float, duration: float, segments: Dict[str, Any]):
    if preferred_transport and transport_type != preferred_transport:
        transport_cost = 15
    else:
        transport_cost = 0
    if criteria == "cost":
        cost = 0.7 * fair + 0.3 * transport_cost
    else:
        cost = 0.7 * duration + 0.3 * transport_cost

    route_graph.add_edge(
        origin.name,
        destination.name,
        transport_type=transport_type,
        depart_time=depart_time,
        arrival_time=arrival_time,
        fair=fair,
        duration=duration,
        segments=segments,
        cost=cost
    )

# Функция для поиска оптимального маршрута от узла S до узла E, проходящего через все узлы ровно один раз
def find_optimal_route(start: City, end: City):
    best_route = None
    best_cost = float('inf')

    def dfs(current_city, visited, current_cost, current_route, current_time):
        nonlocal best_route, best_cost

        print(f"Bieżące miasto: {current_city}, Bieżący koszt: {current_cost}, Bieżąca trasa: {current_route}")

        if len(visited) == len(route_graph.nodes) and current_city == end.name:
            if current_cost < best_cost:
                best_cost = current_cost
                best_route = list(current_route)
            print(f"Znaleziona cała trasa: {current_route} z kosztem: {current_cost}")
            return

        for edge in route_graph.edges(current_city, data=True):
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

    dfs(start.name, {start.name}, 0, [start.name], start_date_global)
    return best_route, best_cost

# Функция для проверки, соответствует ли время отправления ограничениям на пребывание в городе
def is_valid_stay(current_route: List[str], depart_time: str, current_time: datetime) -> bool:
    try:
        depart_time_dt = datetime.strptime(depart_time, "%Y-%m-%dT%H:%M:%S")
    except ValueError:
        print(f"Błąd przekształcenia depart_time: {depart_time}")
        return False

    if len(current_route) == 1:
        # Проверяем первый город: мы должны выехать не позднее, чем через 24 часа от указанной даты с автоматически добавленным временем 00:00
        latest_departure = start_date_global + timedelta(hours=24)
        valid = depart_time_dt <= latest_departure
        print(f"Sprawdzenie pierwszego miasta. depart_time: {depart_time_dt}, Dopuszczalny depart_time: {latest_departure}, Wynik: {valid}")
        return valid

    last_city = current_route[-1]
    stay_hours_min = route_graph.nodes[last_city]['stay_hours_min']
    stay_hours_max = route_graph.nodes[last_city]['stay_hours_max']

    # Получаем время прибытия в город
    previous_edge = route_graph.get_edge_data(current_route[-2], last_city)
    
    if not previous_edge:
        print(f"Nie ma przedniej krawędzi między {current_route[-2]} a {last_city}")
        return False

    arrival_time = datetime.strptime(previous_edge['arrival_time'], "%Y-%m-%dT%H:%M:%S")
    print(f"Znaleziono arrival_time dla {last_city}: {arrival_time}")

    # Проверяем, что время отправления находится в допустимом диапазоне пребывания
    earliest_departure = arrival_time + timedelta(hours=stay_hours_min)
    latest_departure = arrival_time + timedelta(hours=stay_hours_max)
    valid = earliest_departure <= depart_time_dt <= latest_departure
    print(f"Sprawdzenie czasu pobytu w mieście {last_city}. Czas przyjazdu: {arrival_time}, Czas odjazdu: {depart_time_dt}, Dopuszczalny zakres: ({earliest_departure}, {latest_departure}), Wynik: {valid}")

    # Исправление: если текущее время отправления не удовлетворяет минимальному времени пребывания, оно должно соответствовать минимуму
    if depart_time_dt < earliest_departure:
        print(f"Godzina odbycia jest zbyt wczesna. Dostosujemy się do możliwego minimum: {earliest_departure}")
        return False

    return valid

# Пример использования (комментирование для интеграции с main.py)
if __name__ == "__main__":
    # Пример добавления городов и маршрутов
    city1 = City(name="Wroclaw", country="Poland", coordinates=(51.107885, 17.038538), stay_hours_min=24, stay_hours_max=48)
    city2 = City(name="Berlin", country="Germany", coordinates=(52.520008, 13.404954), stay_hours_min=24, stay_hours_max=72)
    city3 = City(name="Prague", country="Czech Republic", coordinates=(50.075538, 14.437800), stay_hours_min=24, stay_hours_max=48)
    city4 = City(name="Vienna", country="Austria", coordinates=(48.208176, 16.373819), stay_hours_min=24, stay_hours_max=72)

    add_city_to_graph(city1)
    add_city_to_graph(city2)
    add_city_to_graph(city3)
    add_city_to_graph(city4)

    set_optimal_criteria("cost", None, "2024-12-15T00:00:00")

    # Добавляем соединения для каждой пары городов с разными временными интервалами
    connections = [
        (city1, city2, 'bus', '2024-12-15T08:00:00', '2024-12-15T13:00:00', 100, 5, {}),
        (city1, city2, 'bus', '2024-12-15T10:00:00', '2024-12-15T15:00:00', 120, 5, {}),
        (city1, city2, 'bus', '2024-12-15T12:00:00', '2024-12-15T17:00:00', 90, 5, {}),
        (city1, city3, 'bus', '2024-12-15T15:00:00', '2024-12-15T22:00:00', 130, 7, {}),
        (city1, city3, 'bus', '2024-12-15T18:00:00', '2024-12-16T01:00:00', 140, 7, {}),
        (city2, city3, 'bus', '2024-12-16T18:00:00', '2024-12-16T22:00:00', 80, 4, {}),
        (city2, city3, 'bus', '2024-12-16T20:00:00', '2024-12-17T00:00:00', 85, 4, {}),
        (city2, city3, 'bus', '2024-12-17T09:00:00', '2024-12-17T13:00:00', 75, 4, {}),
        (city3, city2, 'bus', '2024-12-17T10:00:00', '2024-12-17T14:00:00', 95, 4, {}),
        (city3, city2, 'bus', '2024-12-17T12:00:00', '2024-12-17T16:00:00', 100, 4, {}),
        (city3, city4, 'bus', '2024-12-18T10:00:00', '2024-12-18T14:00:00', 90, 4, {}),
        (city3, city4, 'bus', '2024-12-18T12:00:00', '2024-12-18T16:00:00', 85, 4, {}),
        (city3, city4, 'bus', '2024-12-18T14:00:00', '2024-12-18T18:00:00', 95, 4, {}),
        (city2, city4, 'bus', '2024-12-17T17:00:00', '2024-12-17T23:00:00', 130, 7, {}),
        (city2, city4, 'bus', '2024-12-18T05:00:00', '2024-12-18T11:00:00', 140, 7, {}),
        (city2, city4, 'bus', '2024-12-18T07:00:00', '2024-12-18T13:00:00', 135, 7, {})
    ]

    # Добавляем все соединения в граф
    for conn in connections:
        add_transfer_to_graph(*conn)

    # Поиск оптимального маршрута
    best_route, best_cost = find_optimal_route(city1, city4)
    print(f"Best route: {best_route} with cost: {best_cost}")
