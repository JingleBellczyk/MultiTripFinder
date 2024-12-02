import logging
import subprocess
from datetime import datetime, timedelta
from typing import List, Tuple
import requests
from dto import AlgorithmRequest, PlaceInSearchRequest, Coordinates, TransportMode

otp_graphql_endpoint = "http://localhost:8801/otp/gtfs/v1"
headers = {
    "Content-Type": "application/json"
}


def start_otp():
    global otp_process
    try:
        command = ["java", "-Xmx6G", "-jar", "./otp/otp-2.6.0-shaded.jar", "--load", "./otp/graph", "--port", "8801"]
        otp_process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True,
                                       encoding="utf-8")

        for line in iter(otp_process.stdout.readline, ""):
            logging.info(line.strip())
            if "Grizzly server running." in line:
                logging.info("OTP service started successfully.")
                return

    except Exception as e:
        logging.info(f"Failed to start OTP service: {e}")
        raise e


def stop_otp():
    global otp_process
    if otp_process:
        otp_process.terminate()
        otp_process.wait()
        logging.info("OTP process terminated")


# 1. extract bus and railway coordinates
# 2. map date from request to date with same day of the week in existing timeschedule from gtfs
# 3. send request to otp
# 4. ...
#async def find_routes_otp(request: AlgorithmRequest, city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
#    request_body = create_otp_request_body(Coordinates(lat="51.097916", lon="17.0379512"), Coordinates(lat="50.0686663", lon="19.947802"), datetime.strptime("2024-12-02T00:00:00.000Z", "%Y-%m-%dT%H:%M:%S.%fZ"), TransportMode.TRAIN)
#    payload = {"query": request_body}
#    response = requests.post(otp_graphql_endpoint, headers=headers, json=payload)
#    logging.info(f"Response: {response.text}")
#    return None

async def find_routes_otp(request: AlgorithmRequest, city_pairs: List[Tuple[PlaceInSearchRequest, PlaceInSearchRequest]]):
    for pair in city_pairs:
        from


def create_otp_request_body(fromCoordinates: Coordinates, toCoordinates: Coordinates, date: datetime,
                            transportMode: TransportMode):
    transport = "RAIL" if transportMode == TransportMode.TRAIN else "BUS"
    return f"""
        {{
          plan(
            from: {{lat: {fromCoordinates.lat}, lon: {fromCoordinates.lon}}}
            to: {{lat: {toCoordinates.lat}, lon: {toCoordinates.lon}}}
            date: "{map_date(date).strftime("%Y-%m-%d")}"
            time: "00:00"
            searchWindow: "86400"
            numItineraries: 100
            transportModes: [{{mode: {transport}}}]
          ) {{
            itineraries {{
              start
              end
              numberOfTransfers
              legs {{
                from {{
                  name
                  departure {{
                    scheduledTime
                  }}
                }}
                to {{
                  name
                  arrival {{
                    scheduledTime
                  }}
                }}
                route {{
                  longName
                  shortName
                }}
                mode
              }}
            }}
          }}
        }}
    """


def map_date(date: datetime):
    schedule_start = datetime(2024, 11, 18)
    schedule_end = datetime(2024, 11, 24)
    target_day_of_week = date.weekday()

    # Find the corresponding date in the schedule with the same day of the week
    corresponding_date = schedule_start + timedelta(days=target_day_of_week)

    # Ensure the corresponding date is within the schedule range
    if schedule_start <= corresponding_date <= schedule_end:
        return corresponding_date
    else:
        raise ValueError("Target date's day of the week is not within the schedule range.")
