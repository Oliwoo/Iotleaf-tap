package com.tap.unict.iotleaf.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.stereotype.Service;

@Service
public class MqttMsgManager {
    private static MqttMsgManager instance;

    private MqttClient client;

    private MqttMsgManager() {
        try {
            client = new MqttClient("tcp://mqtt:1883", "iotleaf-server", null);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            client.connect(options);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public static synchronized MqttMsgManager getInstance() {
        if(instance == null) {
            instance = new MqttMsgManager();     
        }
        return instance;
    }

    public boolean publishMessage(String topic,String message){
        try {
            if(client.isConnected()) {
                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(1);
                client.publish(topic, mqttMessage);
                return true;
            } else {
                return false;
            }
        }catch(MqttException e){
            return false;
        }
    }
    public void publishAlert(String message) {
        this.publishMessage("alert",message);
    }
}
