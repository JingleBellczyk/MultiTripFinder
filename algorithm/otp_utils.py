import subprocess

OTP_URL = f"http://localhost:8080/otp/routers/default/plan"

# Функция для запуска OpenTripPlanner
def start_otp():
    global otp_process, otp_ready
    command = ["java", "-Xmx10G", "-jar", "D:/PWR/sem7/ZPI/otp-2.6.0-shaded.jar", "--load", "D:/PWR/sem7/ZPI/otp"]
    otp_process = subprocess.Popen(command, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, text=True, encoding="utf-8")

    # Чтение вывода процесса для определения готовности
    for line in iter(otp_process.stdout.readline, ""):
        print(line.strip())  # Печать строк для логирования
        if "Grizzly server running." in line:
            return True
            break

# Функция для остановки OpenTripPlanner
def stop_otp():
    global otp_process
    if otp_process:
        otp_process.terminate()
        otp_process.wait()
        print("OTP process terminated")