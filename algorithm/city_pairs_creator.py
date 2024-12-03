import logging

from dto import AlgorithmRequest


def extract_city_pairs(request: AlgorithmRequest):
    visit_city_pairs = []
    end_city_pairs = []
    if request.places_to_visit:
        start_city_pairs = [(request.start_place, city) for city in request.places_to_visit]
        visit_city_pairs = [(city_a, city_b) for i, city_a in enumerate(request.places_to_visit) for city_b in
                            request.places_to_visit if city_a != city_b]
        end_city_pairs = [(city, request.end_place) for city in request.places_to_visit]
    else:
        start_city_pairs = [(request.start_place, request.end_place)]
    return start_city_pairs, visit_city_pairs, end_city_pairs
