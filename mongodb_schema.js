// Schéma pour les items (version simplifiée)
db.createCollection('items', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id_item", "nom", "type"],
            properties: {
                id_item: { bsonType: "int" },
                nom: { bsonType: "string" },
                type: { bsonType: "string" },
                description: { bsonType: "string" },
                prix: { bsonType: "int" },
                effet: { bsonType: "object" },
                proprietes: { bsonType: "object" },
                stackable: { bsonType: "bool" },
                quantite_max: { bsonType: "int" }
            }
        }
    }
});

// Schéma pour les inventaires
db.createCollection('inventaires', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id_joueur", "items"],
            properties: {
                id_joueur: { bsonType: "int" },
                items: {
                    bsonType: "array",
                    items: {
                        bsonType: "object",
                        required: ["id_item", "quantite"],
                        properties: {
                            id_item: { bsonType: "int" },
                            quantite: { bsonType: "int" },
                            position: { bsonType: "int" }
                        }
                    }
                }
            }
        }
    }
});

// Schéma pour les coffres
db.createCollection('coffres', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id_joueur", "items"],
            properties: {
                id_joueur: { bsonType: "int" },
                items: {
                    bsonType: "array",
                    items: {
                        bsonType: "object",
                        required: ["id_item", "quantite"],
                        properties: {
                            id_item: { bsonType: "int" },
                            quantite: { bsonType: "int" },
                            position: { bsonType: "int" }
                        }
                    }
                }
            }
        }
    }
});

// Schéma pour les équipements
db.createCollection('equipements', {
    validator: {
        $jsonSchema: {
            bsonType: "object",
            required: ["id_personnage", "equipements"],
            properties: {
                id_personnage: { bsonType: "int" },
                equipements: {
                    bsonType: "object",
                    properties: {
                        arme: { bsonType: "int" },
                        armure: { bsonType: "int" },
                        casque: { bsonType: "int" },
                        bottes: { bsonType: "int" },
                        gants: { bsonType: "int" },
                        accessoire1: { bsonType: "int" },
                        accessoire2: { bsonType: "int" }
                    }
                }
            }
        }
    }
});

// Index essentiels
db.items.createIndex({ "id_item": 1 }, { unique: true });
db.inventaires.createIndex({ "id_joueur": 1 });
db.coffres.createIndex({ "id_joueur": 1 });
db.equipements.createIndex({ "id_personnage": 1 });

