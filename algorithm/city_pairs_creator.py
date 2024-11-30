import logging

from dto import AlgorithmRequest


def extract_city_pairs(request: AlgorithmRequest):
    if request.places_to_visit:
        city_pairs = [(request.start_place, city) for city in request.places_to_visit]
        city_pairs += [(city_a, city_b) for i, city_a in enumerate(request.places_to_visit) for city_b in
                       request.places_to_visit if city_a != city_b]
        city_pairs += [(city, request.end_place) for city in request.places_to_visit]
    else:
        city_pairs = [(request.start_place, request.end_place)]

    logging.info("City pairs:")
    for city_pair in city_pairs:
        logging.info(city_pair[0].city, " - ", city_pair[1].city)
    return city_pairs
