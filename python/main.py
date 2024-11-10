import json
from flask import Flask, request, jsonify
import math
from datetime import datetime

app = Flask(__name__)
DATA_FILE = "magnetic_field_data.txt"

def save_data_to_file(data):
    # Добавляем дату и время к данным
    data["timestamp"] = datetime.now().isoformat()  # Используем ISO формат для даты и времени
    
    # Записываем данные в текстовый файл в формате JSON
    with open(DATA_FILE, "a") as file:
        file.write(json.dumps(data) + "\n")

@app.route('/magnetic-field', methods=['POST'])
def get_magnetic_field():
    data = request.json  # Получаем JSON из POST-запроса
    
    if 'x' not in data or 'y' not in data or 'z' not in data:
        return jsonify({"error": "Invalid data"}), 400

    x = data['x']
    y = data['y']
    z = data['z']

    # Вычисляем вектор магнитного поля
    vector = math.sqrt(x * x + y * y + z * z)
    response_data = {"x": x, "y": y, "z": z, "vector": vector}

    # Сохраняем данные с магнитным полем, дополнив запись временем
    save_data_to_file(response_data)
    return jsonify({"vector": vector})

@app.route('/location', methods=['POST'])
def get_location_field():
    data = request.json  # Получаем JSON из POST-запроса
    
    if 'longitude' not in data or 'latitude' not in data:
        return jsonify({"error": "Invalid data"}), 400

    longitude = data['longitude']
    latitude = data['latitude']

    # Формируем данные для записи и сохраняем в файл с временем
    response_data = {"longitude": longitude, "latitude": latitude}
    save_data_to_file(response_data)
    return jsonify(response_data), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
