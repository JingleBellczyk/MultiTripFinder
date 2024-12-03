import asyncio
import logging
import sys
import time
from contextlib import asynccontextmanager
from fastapi import FastAPI
from amadeus_utils import process_flight_data
from city_pairs_creator import extract_city_pairs
from dto import AlgorithmRequest
from otp_utils import start_otp, stop_otp, find_routes_otp_async
import uvicorn

otp_process = None
logging.basicConfig(level=logging.INFO)


@asynccontextmanager
async def lifespan(app: FastAPI):
    try:
        await asyncio.to_thread(start_otp)
        yield
    except Exception as e:
        logging.error(f"OTP initialization failed: {e}")
        sys.exit(1)
    finally:
        stop_otp()


app = FastAPI(lifespan=lifespan)


@app.post("/process_route")
async def process_route(request: AlgorithmRequest):
    start_city_pairs, visit_city_pairs, end_city_pairs = extract_city_pairs(request)
    start_time = time.monotonic()
    otp_results = await find_routes_otp_async(request, start_city_pairs, visit_city_pairs, end_city_pairs)
    duration = time.monotonic() - start_time
    logging.info(f"async find_routes_otp executed in {duration:.2f} seconds")
    
    #amadeus_results = await process_flight_data(request, city_pairs)
    
    return {"status": "Request processed", "results": "pass"}


if __name__ == "__main__":
    uvicorn.run(app, host='0.0.0.0', port=8000)
