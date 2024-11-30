import logging
import subprocess

OTP_URL = f"http://localhost:8080/otp/routers/default/plan"


# Функция для запуска OpenTripPlanner
def start_otp():
    global otp_process
    try:
        # Command to start OTP service
        command = ["java", "-Xmx6G", "-jar", "./otp/otp-2.6.0-shaded.jar", "--load", "./otp/graph"]
        otp_process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True,
                                       encoding="utf-8")

        # Read the output to determine if OTP is ready
        for line in iter(otp_process.stdout.readline, ""):
            logging.info(line.strip())  # Log the output of the OTP process
            if "Grizzly server running." in line:
                logging.info("OTP service started successfully.")
                return

    except Exception as e:
        logging.info(f"Failed to start OTP service: {e}")
        raise e  # Reraise the exception to stop FastAPI server startup


# Функция для остановки OpenTripPlanner
def stop_otp():
    global otp_process
    if otp_process:
        otp_process.terminate()
        otp_process.wait()
        logging.info("OTP process terminated")
