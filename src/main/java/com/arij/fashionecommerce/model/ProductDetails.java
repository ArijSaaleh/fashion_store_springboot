package com.arij.fashionecommerce.model;

import jakarta.persistence.Embeddable;

@Embeddable
public class ProductDetails {
    private String fabric;            // e.g., "100% Cotton"
    private String color;             // display color name
    private String careInstructions;  // optional
    private String sizeGuideUrl;      // link to size guide page/image

    // getters/setters
    public String getFabric() { return fabric; }
    public void setFabric(String fabric) { this.fabric = fabric; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCareInstructions() { return careInstructions; }
    public void setCareInstructions(String careInstructions) { this.careInstructions = careInstructions; }
    public String getSizeGuideUrl() { return sizeGuideUrl; }
    public void setSizeGuideUrl(String sizeGuideUrl) { this.sizeGuideUrl = sizeGuideUrl; }
}