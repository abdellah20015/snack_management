import csv
import random
from faker import Faker
import uuid

fake = Faker()

# Categories de snacks
CATEGORIES = ['Boissons', 'Snacks Sales', 'Snacks Sucres', 'Sandwiches', 'Desserts']

# Descriptions par categorie
DESCRIPTIONS = {
    'Boissons': ['Rafraichissant', 'Desalterant', 'Energisant', 'Petillant'],
    'Snacks Sales': ['Croustillant', 'Epice', 'Savoureux', 'Grille'],
    'Snacks Sucres': ['Fondant', 'Sucre', 'Delicieux', 'Gourmand'],
    'Sandwiches': ['Frais', 'Copieux', 'Fait maison', 'Gourmet'],
    'Desserts': ['Onctueux', 'Savoureux', 'Fait maison', 'Traditionnel']
}

def generate_product():
    category = random.choice(CATEGORIES)
    desc_base = random.choice(DESCRIPTIONS[category])

    return {
        'snackId': str(uuid.uuid4()),
        'name': fake.first_name() + "'s " + category.lower(),
        'description': f"{desc_base} {category.lower()}",
        'price': round(random.uniform(1.0, 15.0), 2),
        'category': category,
        'qrCodeUrl': f"/api/qr/{uuid.uuid4()}"
    }

def generate_csv(filename, num_products=1000):
    products = [generate_product() for _ in range(num_products)]

    with open(filename, 'w', newline='', encoding='utf-8') as f:
        writer = csv.DictWriter(f, fieldnames=products[0].keys())
        writer.writeheader()
        writer.writerows(products)

if __name__ == "__main__":
    generate_csv('test_products.csv', 1000)
    print("CSV file generated successfully!")
