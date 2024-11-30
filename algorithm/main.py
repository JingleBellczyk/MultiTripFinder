import asyncio
import logging
import sys
from contextlib import asynccontextmanager
from fastapi import FastAPI
from amadeus_utils import process_flight_data
from models import RouteRequest
from otp_utils import start_otp, stop_otp
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
async def process_route(request: RouteRequest):
    filtered_results = await process_flight_data(request)
    return {"status": "Request processed", "results": filtered_results}


if __name__ == "__main__":
    uvicorn.run(app, host='0.0.0.0', port=8000)
