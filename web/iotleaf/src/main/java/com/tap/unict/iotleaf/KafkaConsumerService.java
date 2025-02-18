package com.tap.unict.iotleaf;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tap.unict.iotleaf.models.*;
import com.tap.unict.iotleaf.models.Device.Device;
import com.tap.unict.iotleaf.models.Modules.*;
import com.tap.unict.iotleaf.models.Modules.Module;
import com.tap.unict.iotleaf.models.Plant.*;
import com.tap.unict.iotleaf.mqtt.MqttMsgManager;
import com.tap.unict.iotleaf.repositories.*;

@Service
public class KafkaConsumerService {

    private final DeviceRepository deviceRepository;
    private final ModuleRepository moduleRepository;
    private final SensorTypeRepository sensorTypeRepository;
    private final PlantBaseConfigurationRepository plantBaseConfigurationRepository;
    private final ModuleConfigurationRepository moduleConfigurationRepository;
    private final MeterRegistry meterRegistry;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Double> sensorValues = new ConcurrentHashMap<>();

    @Autowired
    public KafkaConsumerService(DeviceRepository deviceRepository, ModuleRepository moduleRepository,
        SensorTypeRepository sensorTypeRepository, ModuleConfigurationRepository moduleConfigurationRepository,
        PlantBaseConfigurationRepository plantBaseConfigurationRepository, MeterRegistry meterRegistry) {
        this.deviceRepository = deviceRepository;
        this.moduleRepository = moduleRepository;
        this.sensorTypeRepository = sensorTypeRepository;
        this.moduleConfigurationRepository = moduleConfigurationRepository;
        this.plantBaseConfigurationRepository = plantBaseConfigurationRepository;
        this.meterRegistry = meterRegistry;
    }

    @KafkaListener(topics = "report", groupId = "iotleaf")
    @Transactional
    public void consume(String message) {
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            Long deviceId = jsonNode.get("device_id").asLong();
            Long slot = jsonNode.get("slot").asLong();

            Device device = deviceRepository.findById(deviceId)
            .orElseThrow(() -> new RuntimeException("Device not found"));

            if(device != null && slot != null && device.getSlots() >= slot && moduleRepository.existsById(new ModuleId(device, slot))) {
                Module module = moduleRepository.findById(new ModuleId(device, slot))
                .orElseThrow(() -> new RuntimeException("Module not found"));

                List<ModuleConfiguration> configurations = new ArrayList<>();

                sensorTypeRepository.findAll().stream().forEach(sensorType -> {
                    ModuleConfiguration c = getOrCreateModuleConfiguration(module.getDevice(), module.getSlot(), sensorType, module.getPlant());
                    if(c.getMin() != 0 && c.getMax() != 0){configurations.add(c);}
                });

                List<SensorReport> sensorList = objectMapper.convertValue(
                    jsonNode.get("sensors"), objectMapper.getTypeFactory().constructCollectionType(List.class, SensorReport.class)
                );

                for (SensorReport sensor : sensorList) {
                    configurations.stream()
                    .filter(s -> s.getSensor().getName().equalsIgnoreCase(sensor.getName()))
                    .findFirst()
                    .ifPresent(configuration -> {
                        String metricKey = deviceId + "_" + slot + "_" + sensor.getName();
                        double sensorValue = module.getStatus()?sensor.getValue():Double.NaN;
                        sensorValues.put(metricKey, sensorValue);

                        checkModuleSensorAction(sensorValue,configuration);
                        registerOrUpdateGauge(deviceId, slot, sensor.getName(), configuration);
                    });
                }
            }else{
                System.out.println("Device "+deviceId+" or slot "+slot+" issue detected.");
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private ModuleConfiguration getOrCreateModuleConfiguration(Device d, Long slot, SensorType s, Plant p) {
        ModuleConfigurationId mci = new ModuleConfigurationId(d, slot, s);
        PlantBaseConfigurationId pbci = new PlantBaseConfigurationId(p, s);
        if (moduleConfigurationRepository.existsById(mci)) {
            return moduleConfigurationRepository.getReferenceById(mci);
        }
        if (plantBaseConfigurationRepository.existsById(pbci)) {
            PlantBaseConfiguration pbc = plantBaseConfigurationRepository.getReferenceById(pbci);
            return new ModuleConfiguration(d, slot, s, false, pbc.getMin(), pbc.getMax());
        }
        return new ModuleConfiguration(d, slot, s, false, 0, 0);
    }

    private void registerOrUpdateGauge(Long deviceId, Long slot, String sensorName, ModuleConfiguration configuration) {
        String metricKey = deviceId + "_" + slot + "_" + sensorName;
        
        if(meterRegistry.find("sensor_value").tags(
            "device_id", String.valueOf(deviceId),
            "slot", String.valueOf(slot),
            "sensor", sensorName
        ).gauge() == null){
            Gauge.builder("sensor_value", sensorValues, metrics -> sensorValues.getOrDefault(metricKey, Double.NaN))
            .tags("device_id", String.valueOf(deviceId), 
                "slot", String.valueOf(slot), 
                "sensor", sensorName, 
                "min", String.valueOf(configuration.getMin()), 
                "max", String.valueOf(configuration.getMax()), 
                "format", configuration.getSensor().getFormat())
            .register(meterRegistry);
        }
    }

    private void checkModuleSensorAction(double value, ModuleConfiguration configuration){
        SensorAlert alert = new SensorAlert(configuration.getDevice().getId(), configuration.getSlot(),configuration.getSensor().getName(), value);
        if(value < configuration.getMin()){
            alert.setSecureValue(configuration.getMax());
        }
        if(value > configuration.getMax()){
            alert.setSecureValue(configuration.getMax());
        }
        MqttMsgManager.getInstance().publishAlert(alert.toString());
    }
}