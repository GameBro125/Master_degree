import json
from flask import Flask, request, jsonify
import math

app = Flask(__name__)
DATA_FILE = "magnetic_field_data.txt"

def save_data_to_file(data):
    # Записываем данные в текстовый файл
    with open(DATA_FILE, "a") as file:
        file.write(json.dumps(data) + "\n")  # Записываем данные в формате JSON

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

    save_data_to_file(response_data)
    return jsonify({"vector": vector})

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=8080)
