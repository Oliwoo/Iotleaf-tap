package com.tap.unict.iotleaf.models.Plant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Plant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int germogliationTime;
    private String imgPath = "/images/no_image.png";
    private boolean outdoor = false;
    @ManyToOne
    @JoinColumn(name="type")
    private PlantType type;
    @Column(columnDefinition = "TEXT")
    private String plantInfo;
    @Column(columnDefinition = "TEXT")
    private String allevationInfo;
    
    public Plant() {

    }
    public Plant(Long id, String name, int germogliationTime, String imgPath, boolean outdoor, PlantType type,
            String plantInfo, String allevationInfo) {
        this.id = id;
        this.name = name;
        this.germogliationTime = germogliationTime;
        this.imgPath = imgPath!=null?imgPath:this.imgPath;
        this.outdoor = outdoor;
        this.type = type;
        this.plantInfo = plantInfo;
        this.allevationInfo = allevationInfo;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getGermogliationTime() {
        return germogliationTime;
    }
    public void setGermogliationTime(int germogliationTime) {
        this.germogliationTime = germogliationTime;
    }
    public String getImgPath() {
        return imgPath;
    }
    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
    public boolean isOutdoor() {
        return outdoor;
    }
    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }
    public PlantType getType() {
        return type;
    }
    public void setType(PlantType type) {
        this.type = type;
    }
    public String getPlantInfo() {
        return plantInfo;
    }
    public void setPlantInfo(String plantInfo) {
        this.plantInfo = plantInfo;
    }
    public String getAllevationInfo() {
        return allevationInfo;
    }
    public void setAllevationInfo(String allevationInfo) {
        this.allevationInfo = allevationInfo;
    }   
}
