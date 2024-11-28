from fastapi import FastAPI
from contextlib import asynccontextmanager

from models import RouteRequest
from otp_utils import start_otp, stop_otp
from amadeus_utils import process_flight_data
from graph_utils import add_city_to_graph, add_route_to_graph, find_optimal_route

app = FastAPI()
otp_ready = False

@asynccontextmanager
async def lifespan(app: FastAPI):
    otp_ready = await start_otp()
    yield
    stop_otp()

@app.post("/process_route")
async def process_route(request: RouteRequest):
    if not otp_ready:
        return {"status": "OTP is not ready", "results": []}
    filtered_results = await process_flight_data(request)
    return {"status": "Request processed", "results": filtered_results}

# Команда для запуска сервера:
# uvicorn main:app --reload
