import asyncio
import logging
import sys
import time
from contextlib import asynccontextmanager
from datetime import datetime

from fastapi import FastAPI
from amadeus_utils import process_flight_data
from city_pairs_creator import extract_city_pairs
from dto import AlgorithmRequest
from graph_utils import process_connections_data
from otp_utils import start_otp, stop_otp, find_routes_otp_async
import uvicorn

otp_process = None
logging.basicConfig(level=logging.INFO)


@asynccontextmanager
async def lifespan(app: FastAPI):
    try:
        #await asyncio.to_thread(start_otp)
        yield
    except Exception as e:
        logging.error(f"OTP initialization failed: {e}")
        sys.exit(1)
    #finally:
        #stop_otp()


app = FastAPI(lifespan=lifespan)


@app.post("/process_route")
async def process_route(request: AlgorithmRequest):
    start_city_pairs, visit_city_pairs, end_city_pairs = extract_city_pairs(request)
    city_pairs = start_city_pairs + visit_city_pairs + end_city_pairs
    start_time = time.monotonic()
    #otp_results = await find_routes_otp_async(request, city_pairs)
    duration = time.monotonic() - start_time
    logging.info(f"async find_routes_otp executed in {duration:.2f} seconds")
    
    amadeus_results = await process_flight_data(request, start_city_pairs, visit_city_pairs, end_city_pairs)
    logging.info(f"Amadeus results: {amadeus_results}")
    #string of date to datetime with time 00:00:00
    start_date_datetime = datetime.combine(datetime.strptime(request.trip_start_date, "%Y-%m-%d"), datetime.min.time())

    result = process_connections_data(request.start_place, request.end_place, request.places_to_visit, request.passenger_count, request.preferred_transport, request.optimization_criteria, start_date_datetime, [], amadeus_results)
    logging.info(result)
    return {"status": "Request processed", "results": result}


if __name__ == "__main__":
    uvicorn.run(app, host='0.0.0.0', port=8000)
