package com.example.vendor;

public class MyOrderData {

    String itemFinalPrice, itemGms, itemImage, itemName, itemQuantity, itemUnit, itemNameInMarathi;

    public MyOrderData(String itemFinalPrice, String itemGms, String itemImage, String itemName, String itemQuantity, String itemUnit, String itemNameInMarathi) {
        this.itemFinalPrice = itemFinalPrice;
        this.itemGms = itemGms;
        this.itemImage = itemImage;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemUnit = itemUnit;
        this.itemNameInMarathi =itemNameInMarathi;
    }

    public MyOrderData(){}

    public String getItemNameInMarathi() {
        return itemNameInMarathi;
    }

    public void setItemNameInMarathi(String itemNameInMarathi) {
        this.itemNameInMarathi = itemNameInMarathi;
    }

    public String getItemFinalPrice() {
        return itemFinalPrice;
    }

    public void setItemFinalPrice(String itemFinalPrice) {
        this.itemFinalPrice = itemFinalPrice;
    }

    public String getItemGms() {
        return itemGms;
    }

    public void setItemGms(String itemGms) {
        this.itemGms = itemGms;
    }

    public String getItemImage() {
        return itemImage;
    }

    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getItemUnit() {
        return itemUnit;
    }

    public void setItemUnit(String itemUnit) {
        this.itemUnit = itemUnit;
    }
}
