import json
from flask import Flask, request, jsonify
from datetime import datetime
import math

app = Flask(__name__)
DATA_FILE = "magnetic_field_data.txt"

def save_data_to_file(data):
    # Добавляем метку времени
    data["timestamp"] = datetime.now().isoformat()  # ISO формат даты и времени
    
    # Записываем данные в файл в формате JSON
    with open(DATA_FILE, "a") as file:
        file.write(json.dumps(data) + "\n")

@app.route('/magnetic-field', methods=['POST'])
def receive_data():
    data = request.json  # Получаем JSON из POST-запроса

    # Проверяем наличие необходимых данных в запросе
    if 'location' not in data or 'magneticField' not in data:
        return jsonify({"error": "Invalid data"}), 400

    # Извлекаем данные местоположения
    location = data['location']
    if 'longitude' not in location or 'latitude' not in location:
        return jsonify({"error": "Invalid location data"}), 400

    longitude = location['longitude']
    latitude = location['latitude']

    # Извлекаем данные магнитного поля
    magnetic_field = data['magneticField']
    if 'x' not in magnetic_field or 'y' not in magnetic_field or 'z' not in magnetic_field:
        return jsonify({"error": "Invalid magnetic field data"}), 400

    x = magnetic_field['x']
    y = magnetic_field['y']
    z = magnetic_field['z']

    # Вычисляем вектор магнитного поля
    vector = math.sqrt(x * x + y * y + z * z)

    # Формируем данные для записи и отправки обратно
    response_data = {
        "location": {
            "longitude": longitude,
            "latitude": latitude
        },
        "magneticField": {
            "x": x,
            "y": y,
            "z": z,
            "vector": vector
        },
        "timestamp": datetime.now().isoformat()
    }

    # Сохраняем данные в файл
    save_data_to_file(response_data)

    # Возвращаем ответ с вычисленным вектором
    return jsonify(response_data), 200

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
