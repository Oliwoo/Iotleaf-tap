const $ = require('jquery');
const mqtt = require("mqtt");
const client = mqtt.connect("mqtt://localhost:1883");

let reqid = null;
let device_id = null;

function publish(topic, message){
    $(".mqttLogger").append(`<p>📤 Inviato: ${message.toString()}</p>`);
    client.publish(topic,message);
}

client.on("connect", () => {
    console.log("✅ Connesso al broker MQTT");
});

function random(min,max){
    return Math.floor(Math.random() * (max - min + 1) + min);
}

function showModules(slots){
    for(i=0; i<slots; i++){
        $(".modules").append("<form class='module' id='module"+i+"'>\
            <h1>Slot "+i+"</h1>\
            <input type='hidden' name='id' value='"+i+"'>\
            <label for='temperature'>Temperature</label>\
            <input type='number' class='sensor' name='temperature' min='0' value='"+random(20,30)+"'>\
            <label for='humidity'>Humidity</label>\
            <input type='number' class='sensor' name='humidity' min='0' max='100' value='"+random(0,100)+"'>\
            <label for='luminosity'>Luminosity</label>\
            <input type='number' class='sensor' name='luminosity' min='0' max='100' value='"+random(0,100)+"'>\
            <button type='submit'>Invia report</button>\
            <script>setInterval(function(){\
                $(document).find('#module"+i+" button').submit();\
                $(document).find('.module input.sensor').each(function(e){$(this).val(random(0,100))});\
            },5000);</script>\
        </form>");
    }
}

$(document).on("submit","form.module",function(e){
    e.preventDefault();
    const id = $(this).find("input[name=id]").val();
    const temperature = $(this).find("input[name=temperature]").val();
    const humidity = $(this).find("input[name=humidity]").val();
    const luminosity = $(this).find("input[name=luminosity]").val();

    const data = JSON.stringify({
        device_id: device_id,
        slot: id,
        sensors: [
            {name:"temperature", value:parseFloat(temperature)},
            {name:"humidity", value:parseFloat(humidity)},
            {name:"luminosity", value:parseFloat(luminosity)},
        ]
    })

    publish("report", data);
})

function validateMessageStructure(message) {
    return message.hasOwnProperty('data') &&
    message.hasOwnProperty('success') &&
    message.hasOwnProperty('error');
}

function validateRegistrationRequest(req) {
    const data = req.data;
    return data.hasOwnProperty('id');
}

client.on("message", (topic, message) => {
    try {
        $(".mqttLogger").append(`<p>📩 Risposta: ${message.toString()}</p>`);
        const req = JSON.parse(message);
        if(validateMessageStructure(req)){
            switch(topic){
                case "registration/"+reqid+"/response":   
                    if(!validateRegistrationRequest(req) || !req.success){
                        console.log("Device registration failed!",req);
                    }else{
                        device_id = req.data.id;
                        showModules(req.data.slots);
                        $("#register").addClass("hidden");
                        $("#dataEmulation").removeClass("hidden");
                        $("#device_id").text(device_id);
                        console.log("Device Succeffull registered..");
                        client.subscribe("iotleaf/"+device_id+"/report");
                        client.subscribe("iotleaf/"+device_id+"/command");
                    }
                    break;
                case "iotleaf/"+device_id+"/command":
                    console.log("[COMMAND]: ",req);
                    break;
            }
        }else{
            console.log("Invalid message structure");
        }
    }catch(e){
        console.log("Error on elaborate the message", e);
    }    
});

document.getElementById("device-form").addEventListener("submit", (event) => {
    event.preventDefault();

    const autoGenerate = document.getElementById("auto-generate").checked;
    const irrigation = autoGenerate ? Math.random() > 0.5 : document.getElementById("irrigation").value === "true";
    const model = autoGenerate ? `IotLeaf Smart Garden${Math.floor(Math.random() * 1000)}` : document.getElementById("model").value;
    const slots = autoGenerate ? Math.floor(Math.random() * 8) : document.getElementById("network").value;
    const network = autoGenerate ? `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}` : document.getElementById("ipAddress").value;
    const ipAddress = autoGenerate ? `192.168.${Math.floor(Math.random() * 255)}.${Math.floor(Math.random() * 255)}` : document.getElementById("ipAddress").value;
    //const macAddress =  autoGenerate ? `XX:XX:XX:${Math.floor(Math.random() * 100)}:${Math.floor(Math.random() * 100)}:${Math.floor(Math.random() * 100)}` : document.getElementById("macAddress").value;
    const macAddress = "XX:XX:XX:XX:XX:XX";
    const firmware = autoGenerate ? `v${Math.random().toFixed(2)}` : document.getElementById("firmware").value;
    const uptime = 0;

    const deviceData = {
        irrigation: irrigation,
        model: model,
        slots: slots,
        network: network,
        ipAddress: ipAddress,
        macAddress: macAddress,
        firmware: firmware,
        uptime: uptime
    };

    const req = {
        request_id: 1,
        data: deviceData,
        timestamp: 1234567890
    };
    reqid = req.request_id;

    console.log("📤 Inviando dati:", req);
    console.log("Attesa risposta da:","registration/"+req.request_id+"/response");
    client.subscribe("registration/"+req.request_id+"/response");
    publish("registration", JSON.stringify(req));
});
