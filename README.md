# IotLeaf

### 📝 Descrizione

> IoTLeaf è una piattaforma avanzata per la gestione distribuita di dispositivi IoT, progettata per il monitoraggio e il controllo automatico delle condizioni ambientali delle piante. Questo sistema sfrutta una pipeline di streaming ad-hoc per raccogliere e analizzare in tempo reale i dati provenienti da sensori IoT (come temperatura, umidità del suolo, luminosità) e per attuare azioni automatizzate come l'irrigazione, la regolazione dell'illuminazione e il controllo della temperatura.

In un contesto di crescente diffusione dell'Internet delle Cose (IoT), IoTLeaf si distingue come una soluzione SaaS (Software as a Service), che permette agli utenti di accedere facilmente alla piattaforma attraverso un'interfaccia web. Non è necessario gestire hardware o infrastruttura, poiché IoTLeaf è completamente basato su cloud. Gli utenti possono monitorare e automatizzare le condizioni delle loro piante in modo intuitivo, scalabile e senza doversi preoccupare della gestione della tecnologia sottostante.

Questa piattaforma è progettata per supportare dispositivi IoT scalabili e ad alta frequenza di aggiornamenti, sfruttando la potenza di Kafka per l'elaborazione dei dati in tempo reale, Prometheus e Grafna per il monitoraggio e la visualizzazione delle metriche. IoTLeaf rappresenta una soluzione moderna ed efficiente per chi desidera una gestione ottimizzata e intelligente di dispositivi IoT in ambienti complessi, come quelli agricoli o domestici, con l'obiettivo di migliorare la produttività e il benessere delle piante.

### 🛠️ Architettura del Sistema
![Architettura del Sistema](architecture.png)

Il sistema è basato su un'architettura a microservizi e utilizza le seguenti tecnologie:

- [Docker](https://docs.docker.com/): La containerizzazione con Docker assicura l'isolamento dei vari servizi, facilitando il deployment e la gestione delle dipendenze. Grazie a Docker Compose, l'intero sistema può essere avviato con un solo comando, riducendo la complessità operativa. Il supporto per il monitoraggio e il logging (tramite strumenti come Prometheus e Grafana) migliora il processo di debugging e gestione delle risorse.
- [Mosquitto](https://hub.docker.com/_/eclipse-mosquitto)(MQTT Broker): Mosquitto, un broker MQTT leggero e scalabile, viene utilizzato per la comunicazione tra i dispositivi IoT e il sistema backend. Il protocollo MQTT è particolarmente adatto per dispositivi a bassa potenza e larghe reti di sensori, poiché permette una comunicazione efficiente e a bassa latenza, con un utilizzo minimo delle risorse.
- [mqtt2KafkaBridge](https://hub.docker.com/r/marmaechler/mqtt2kafkabridge): Questo componente svolge un ruolo cruciale nell'interfacciare MQTT con Kafka, combinando la leggerezza del protocollo MQTT con la scalabilità e la resilienza di Kafka per l'elaborazione dei dati in streaming. Il bridge permette di convertire i messaggi MQTT in eventi Kafka, che possono poi essere processati, analizzati e archiviati in modo distribuito e sicuro.
- [Zookeeper](https://hub.docker.com/r/bitnami/zookeeper): Essenziale per il coordinamento e la gestione di Kafka, Zookeeper garantisce la sincronizzazione tra i broker, gestisce i metadati dei topic e permette l'alta disponibilità del sistema. La sua presenza è fondamentale per il funzionamento stabile di Kafka in ambienti distribuiti, assicurando che i dati vengano gestiti correttamente anche in scenari di grande carico.
- [Kafka](https://hub.docker.com/r/bitnami/kafka): La scelta di Kafka come sistema di messaggistica distribuita consente di gestire grandi flussi di dati in tempo reale con un'elevata capacità di throughput e una bassa latenza. Kafka è ideale per raccogliere e gestire i dati provenienti dai dispositivi IoT, garantendo che i dati siano sempre disponibili e facilmente accessibili per elaborazioni future.
- [Kafka UI](https://hub.docker.com/r/provectuslabs/kafka-ui): Un'interfaccia grafica intuitiva per la gestione dei topic Kafka. Questa dashboard facilita il monitoraggio delle attività, la gestione dei topic e la visualizzazione dei messaggi, rendendo più semplice la gestione operativa dei flussi di dati.
- [Spring Boot](https://spring.io/projects/spring-boot): Framework utilizzato per sviluppare i microservizi backend. La sua modularità e scalabilità permettono una rapida integrazione con Kafka e altri sistemi, offrendo un’architettura robusta per l’elaborazione dei dati. Spring Boot rende anche facile la creazione di API per il controllo e la gestione dei dispositivi IoT.
- [Prometheus](https://hub.docker.com/r/prom/prometheus): Strumento di monitoraggio delle performance che raccoglie e analizza le metriche in tempo reale. Prometheus permette di monitorare la salute e le prestazioni dei vari componenti del sistema (dispositivi IoT, Kafka, microservizi) e inviare allarmi in caso di anomalie.
- [Grafana](https://hub.docker.com/r/grafana/grafana): Utilizzato per la visualizzazione delle metriche raccolte da Prometheus. Le dashboard interattive di Grafana permettono agli utenti di monitorare in tempo reale il funzionamento del sistema, analizzando i dati relativi ai dispositivi IoT, alla gestione dei topic Kafka e alle performance generali.
- [MySQL](https://hub.docker.com/_/mysql): Database relazionale utilizzato per archiviare dati persistenti, come le configurazioni dei dispositivi, le registrazioni delle piante e altre informazioni storiche necessarie per la gestione e la personalizzazione del sistema.

## ✨ Funzionamento della piattaforma
IoTLeaf raccoglie e elabora i dati in tempo reale grazie alla combinazione di tecnologie moderne come Kafka, Mosquitto e Spring Boot. I dispositivi IoT inviano periodicamente dati relativi alle condizioni ambientali al sistema tramite il protocollo MQTT, che vengono poi trasformati in eventi Kafka. Questi eventi vengono elaborati da microservizi sviluppati con Spring Boot, che attivano azioni automatizzate (come l'irrigazione delle piante) in base ai parametri rilevati.

Tutti i dati vengono monitorati in tempo reale tramite Prometheus e visualizzati in dashboard interattive su Grafana, consentendo agli utenti di visualizzare facilmente lo stato del sistema, le performance e il benessere delle piante.

## 📦 Installazione e Configurazione
IoTLeaf è progettato per essere facilmente deployato tramite Docker. Gli utenti possono clonare il repository, configurare le variabili necessarie nel file .env e avviare i servizi con un semplice comando. L'intero sistema è pronto all'uso in pochi minuti.

Clona il repository e avvia i servizi con:
```sh
git clone https://github.com/Oliwoo/Iotleaf-tap.git
cd IotLeaf-tap
docker-compose up --build -d
```

Verifica i servizi:
```sh
docker ps
```

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

## Testing

Per testare il sistema, utilizza l'emulatore basato su Electron:
```sh
cd emulator
npm install
npm start
```

## 🔒 Sicurezza e Scalabilità
Essendo una piattaforma cloud-based e SaaS, IoTLeaf è progettata per essere scalabile orizzontalmente, permettendo l'aggiunta di nuovi dispositivi e sensori senza impatti significativi sulle performance. Inoltre, la gestione sicura delle comunicazioni tra i dispositivi e il backend è garantita da MQTT, Kafka e altre misure di sicurezza integrate.

### 🗃️ Link rapidi
- [Kafka UI](http://localhost:8130)
- [Prometheus](http://localhost:9090)
- [IoTLeaf UI](http://localhost:8080)
- [Grafana Dashboard](http://localhost:8131)

## ⚙️ Configurazione del file `.env`

Un file `.env` è fornito per configurare parametri essenziali. Modificalo in base alle tue necessità.

Ora sei pronto per eseguire e testare IotLeaf! 🚀
