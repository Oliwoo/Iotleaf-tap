# IotLeaf

### 📝 Descrizione

> IotLeaf è un sistema avanzato per la gestione distribuita di dispositivi smart, progettato per monitorare e controllare automaticamente le condizioni delle piante. Attraverso una pipeline di streaming ad-hoc, il sistema raccoglie dati sui parametri ambientali (umidità del suolo, illuminazione, temperatura) e attiva azioni automatizzate come irrigazione, regolazione della luce e controllo della temperatura.

### 🛠️ Architettura del Sistema
![Architettura del Sistema](architecture.png)

Il sistema è basato su un'architettura a microservizi e utilizza le seguenti tecnologie:

- [Docker](https://docs.docker.com/): Docker garantisce isolamento, scalabilità e facilità di deployment. Permette di eseguire tutti i servizi in ambienti containerizzati, evitando problemi di compatibilità e semplificando la gestione delle dipendenze. Con Docker Compose, l'intero sistema può essere avviato con un solo comando, mentre il supporto per il monitoraggio e logging (Prometheus, Grafana) rende debugging e manutenzione più efficienti. Inoltre, facilita scalabilità orizzontale, aggiornamenti rapidi e portabilità tra diversi ambienti.
- [Mosquitto](https://hub.docker.com/_/eclipse-mosquitto): L'uso di Mosquitto si basa sul fatto che MQTT è il protocollo più diffuso nei dispositivi smart grazie alla sua leggerezza e basso consumo di risorse. Mosquitto è un broker MQTT efficiente e scalabile, perfetto per gestire la comunicazione tra dispositivi IoT e il backend con latenza minima. Inoltre, supporta connessioni affidabili e sicure, è facile da configurare e si integra perfettamente con il resto dell’architettura, consentendo la trasmissione di dati in tempo reale con un overhead minimo.
- [mqtt2KafkaBridge](https://hub.docker.com/r/marmaechler/mqtt2kafkabridge): L'uso di mqtt2kafkabridge nasce dall'esigenza di intefacciare MQTT con Kafka, combinando la leggerezza di MQTT per i dispositivi IoT con la scalabilità di Kafka per l'elaborazione dei dati in streaming. Questo bridge permette di convertire i messaggi MQTT in eventi Kafka, garantendo affidabilità, persistenza e gestione distribuita dei dati, essenziale per analisi in tempo reale e automazioni avanzate. 
- [Zookeeper](https://hub.docker.com/r/bitnami/zookeeper): Zookeeper è essenziale per la gestione e il coordinamento di Kafka, garantendo alta disponibilità, sincronizzazione tra i broker e gestione dei metadati dei topic. Senza Zookeeper, Kafka non potrebbe funzionare in modo affidabile in un ambiente distribuito, rendendolo un componente fondamentale per la scalabilità e stabilità del sistema.
- [Kafka](https://hub.docker.com/r/bitnami/kafka): Kafka gioca un ruolo chiave nel gestire e processare i flussi di dati generati dai dispositivi IoT. Kafka viene utilizzato come piattaforma di streaming per la gestione dei dati in tempo reale, utile per garantire l’affidabilità e la scalabilità dei dati raccolti, per l'elaborazione e l'automazione dei processi
- [Kafka UI](https://hub.docker.com/r/provectuslabs/kafka-ui): Kafka-UI è un'interfaccia grafica che semplifica la gestione e monitoraggio dei cluster Kafka. È utile per visualizzare e interagire con i topic e i messaggi all'interno di Kafka, senza dover utilizzare la riga di comando o script complessi.
- [Spring Boot](https://spring.io/projects/spring-boot): Spring Boot è utilizzato come consumer Kafka e per data processing per la sua facilità d'uso e rapidità nello sviluppo. Con Spring Kafka, integra facilmente Kafka per ricevere messaggi e processarli in tempo reale. La sua architettura a microservizi consente di scalare facilmente l'applicazione, mentre la modularità di Spring Boot facilita la gestione del sistema, rendendolo ideale per elaborare i dati provenienti dai dispositivi IoT in modo efficiente e scalabile. Inoltre permette di fornire una utile interfaccia web per la gestione dei dispositivi e arrichment dei dati attraverso database.
- [Prometheus](https://hub.docker.com/r/prom/prometheus): Prometheus viene utilizzato per il monitoraggio e la raccoglimento delle metriche in tempo reale. Esso raccoglie dati da vari componenti del sistema (come Spring Boot, Kafka, e Mosquitto), monitorando performance, utilizzo delle risorse e altri parametri operativi. Questi dati vengono poi analizzati per garantire il corretto funzionamento del sistema, facilitando il debug e la gestione delle risorse.
- [Grafana](https://hub.docker.com/r/grafana/grafana): Grafana viene utilizzato per la visualizzazione delle metriche raccolte da Prometheus. Fornisce dashboard interattive e facilmente configurabili per monitorare in tempo reale le performance e lo stato del sistema, come l'attività dei dispositivi IoT, l'uso delle risorse, e altre metriche cruciali. Con Grafana, è possibile avere una panoramica visiva chiara delle operazioni, rendendo più facile l'analisi, il debugging e l'ottimizzazione del sistema.
- [MySQL](https://hub.docker.com/_/mysql): MySQL viene utilizzato per archiviare informazioni persistenti come le impostazioni dei dispositivi, le registrazioni delle piante, e altre configurazioni del sistema. MySQL garantisce affidabilità e supporta query complesse, consentendo di gestire e recuperare facilmente i dati necessari per il funzionamento e la personalizzazione del sistema IoT.

## ✨ Funzionamento

### 🔧 Configurazione WebUI IoTLeaf
IoTLeaf possiede una comoda interfaccia per gestire `dispositivi`, `moduli`, `sensori`, `piante` e relative `categorie` sulla porta `8080`.
- [Devices](http://localhost:8080/devices): Gestione dei dispositivi registrati e dei rispettivi moduli ad esso connessi.
- [Plants](http://localhost:8080/plants): Gestione delle piante e delle relative configurazioni di base.
- [Plant Types](http://localhost:8080/plantTypes): Gestione delle categorie di piante.
- [Sensors](http://localhost:8080/sensorTypes): Gestione dei tipi di sensori supportati e dei relativi formati.

### 🪪 Registrazione dispositivo al sistema

Il dispositivo smart, mediante protocollo di comunicazione `MQTT`, invia una richiesta di registrazione al topic `registration`:
```json
{
    "request_id": "xxxxxxxxxxxx",
    "data": {
        "irrigation": false,
        "model": "Device model",
        "slots": 1,
        "network": "Default",
        "ipAddress": "192.168.x.x",
        "macAddress": "xx:xx:xx:xx:xx:xx",
        "firmware": "v0.1",
        "uptime": 3600
    },
    "timestamp": xxxxxxxx
}
```
> Il `timestamp` non è obbligatorio nella richiesta.

Il dispositivo deve quindi iscriversi al topic `registration/<request_id>/response` per ricevere una risposta contenente il `device_id` e altre informazioni:
```json
{
    "data": {
        "id": 25,
        "name": "New Device",
        "irrigation": true,
        "lastPing": "xxxx-xx-xx xx:xx:xx",
        "model": "Device model",
        "slots": 6,
        "network": "Default",
        "ipAddress": "192.168.x.x",
        "macAddress": "XX:XX:XX:XX:XX:XX",
        "firmware": "v0.1",
        "uptime": 3600,
        "status": false,
        "uptimeStr": "0 s"
    },
    "success": true,
    "error": null,
    "timestamp": 1739985270713
}
```

## 👷 Installazione

Clona il repository e avvia i servizi con:
```sh
git clone https://github.com/Oliwoo/Iotleaf-tap.git
cd IotLeaf-tap
docker-compose up -d
```

Verifica i servizi:
```sh
docker ps
```

## Testing

Per testare il sistema, utilizza l'emulatore basato su Electron:
```sh
cd emulator
npm install
npm start
```

### 🗃️ Link rapidi
- [Kafka UI](http://localhost:8130)
- [Prometheus](http://localhost:9090)
- [IoTLeaf UI](http://localhost:8080)
- [Grafana Dashboard](http://localhost:8131)

## ⚙️ Configurazione del file `.env`

Un file `.env` è fornito per configurare parametri essenziali. Modificalo in base alle tue necessità.

Ora sei pronto per eseguire e testare IotLeaf! 🚀
