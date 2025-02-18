package com.tap.unict.iotleaf.models;

import java.util.List;

public class SubMenu {
    public static List<PathUrl> getViewsSubMenu(){
        return List.of(
            new PathUrl("devices","Devices","/devices"),
            new PathUrl("plants","Plants", "/plants"),
            new PathUrl("plantTypes","Plant types","/plantTypes"),
            new PathUrl("sensorTypes","Sensors", "/sensorTypes")
        );
    }
}
