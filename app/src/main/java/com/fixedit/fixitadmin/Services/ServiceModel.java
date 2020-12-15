package com.fixedit.fixitadmin.Services;

import com.fixedit.fixitadmin.Models.User;

public class ServiceModel {
    String id, name, description;
    boolean active, deleted;
    int serviceBasePrice, peakPrice;
    int commercialServicePrice, commercialServicePeakPrice;
    String imageUrl;
    boolean offeringCommercialService, offeringResidentialService;
    int position;

    public ServiceModel() {
    }

    public ServiceModel(ServiceModel serviceModel) {
        this.id = serviceModel.id;
        this.name = serviceModel.name;
        this.description = serviceModel.description;
        this.active = serviceModel.active;
        this.deleted = serviceModel.deleted;
        this.serviceBasePrice = serviceModel.serviceBasePrice;
        this.peakPrice = serviceModel.peakPrice;
        this.commercialServicePrice = serviceModel.commercialServicePrice;
        this.commercialServicePeakPrice = serviceModel.commercialServicePeakPrice;
        this.imageUrl = serviceModel.imageUrl;
        this.offeringCommercialService = serviceModel.offeringCommercialService;
        this.offeringResidentialService = serviceModel.offeringResidentialService;
    }


    public ServiceModel(String id, int position,String name, String description, boolean active, boolean deleted,
                        int serviceBasePrice, int peakPrice, int commercialServicePrice,
                        int commercialServicePeakPrice, String imageUrl, boolean offeringResidentialService, boolean offeringCommercialService) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.active = active;
        this.deleted = deleted;
        this.serviceBasePrice = serviceBasePrice;
        this.peakPrice = peakPrice;
        this.commercialServicePrice = commercialServicePrice;
        this.commercialServicePeakPrice = commercialServicePeakPrice;
        this.imageUrl = imageUrl;
        this.offeringCommercialService = offeringCommercialService;
        this.position = position;
        this.offeringResidentialService = offeringResidentialService;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public boolean isOfferingCommercialService() {
        return offeringCommercialService;
    }

    public void setOfferingCommercialService(boolean offeringCommercialService) {
        this.offeringCommercialService = offeringCommercialService;
    }

    public boolean isOfferingResidentialService() {
        return offeringResidentialService;
    }

    public void setOfferingResidentialService(boolean offeringResidentialService) {
        this.offeringResidentialService = offeringResidentialService;
    }

    public int getCommercialServicePrice() {
        return commercialServicePrice;
    }

    public void setCommercialServicePrice(int commercialServicePrice) {
        this.commercialServicePrice = commercialServicePrice;
    }

    public int getCommercialServicePeakPrice() {
        return commercialServicePeakPrice;
    }

    public void setCommercialServicePeakPrice(int commercialServicePeakPrice) {
        this.commercialServicePeakPrice = commercialServicePeakPrice;
    }

    public int getPeakPrice() {
        return peakPrice;
    }

    public void setPeakPrice(int peakPrice) {
        this.peakPrice = peakPrice;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getServiceBasePrice() {
        return serviceBasePrice;
    }

    public void setServiceBasePrice(int serviceBasePrice) {
        this.serviceBasePrice = serviceBasePrice;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
