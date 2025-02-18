package com.tap.unict.iotleaf.models;

import java.util.List;

public class ActivePage {
    private String id;
    private String name;
    private String subPage;
    private List<PathUrl> navigator;
    
    public ActivePage(String id, String name, String subPage, List<PathUrl> navigator) {
        this.id = id;
        this.name = name;
        this.subPage = subPage;
        this.navigator = navigator;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSubPage() {
        return subPage;
    }
    public void setSubPage(String subPage) {
        this.subPage = subPage;
    }
    public List<PathUrl> getNavigator() {
        return navigator;
    }
    public void setNavigator(List<PathUrl> navigator) {
        this.navigator = navigator;
    }    
}
