const mqtt = require('mqtt');

// Configura il broker Mosquitto
const brokerUrl = 'mqtt://127.0.0.1'; // Cambia 'localhost' con l'indirizzo del tuo container Docker
const port = 1883; // Porta standard MQTT

// Crea il client MQTT
const client = mqtt.connect(`${brokerUrl}:${port}`);

// Lista delle piante da simulare (valori fissi)
const plants = [];

for(let i=0; i<16; i++){
  plants.push({device_id: 1, slot: i, sensors:[]});
}

// Funzione per generare piccole variazioni casuali
function addRandomVariation(value, variation = 5) {
  const randomOffset = (Math.random() * variation * 2 - variation).toFixed(2);
  return parseFloat((parseFloat(value) + parseFloat(randomOffset)).toFixed(2));
}

// Simula l'invio di messaggi MQTT
function publishPlantData() {
  plants.forEach((device) => {
    const message = {
      device_id: device.device_id,
      slot: device.slot,
      sensors:[
        {name:"temperature",value: addRandomVariation(50)},
        {name:"humidity", value: addRandomVariation(50)},
        {name:"luminosity", value: addRandomVariation(50)}
      ],
      timestamp: new Date().toISOString(),
    };

    // Converte l'oggetto in una stringa JSON
    const payload = JSON.stringify(message);

    // Pubblica il messaggio sul topic "report"
    const topic = 'report';
    client.publish(topic, payload, { qos: 1 }, (err) => {
      if (err) {
        console.error(`Errore durante l'invio al topic ${topic}:`, err);
      } else {
        console.log(`Messaggio inviato a ${topic}:`, payload);
      }
    });
  });
}

// Evento di connessione
client.on('connect', () => {
  console.log('Connesso al broker MQTT');

  // Invia messaggi ogni 5 secondi
  setInterval(publishPlantData, 5000);
});

// Evento di errore
client.on('error', (err) => {
  console.error('Errore nella connessione al broker MQTT:', err);
});
