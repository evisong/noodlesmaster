package me.evis.mobile.noodle.product;

public class Product {
    /**
     * Amazon Item.ASIN
     */
    private String productId;
    /**
     * Amazon Item.ItemAttributes.Title
     */
    private String name;
    /**
     * Amazon Item.ItemAttributes.Brand/Item.ItemAttributes.Author
     */
    private String brand;
    /**
     * Amazon Item.ItemAttributes.Manufacturer
     */
    private String manufacturer;
    /**
     * Amazon EditorialReviews.EditorialReview.Content
     */
    private String description;
    /**
     * Amazon Item.MediumImage.URL
     */
    private String imageUrl;
    /**
     * Amazon Item.DetailPageURL
     */
    private String buyUrl;
    
    public String getProductId() {
        return productId;
    }
    public void setProductId(String productId) {
        this.productId = productId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public String getManufacturer() {
        return manufacturer;
    }
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getBuyUrl() {
        return buyUrl;
    }
    public void setBuyUrl(String buyUrl) {
        this.buyUrl = buyUrl;
    }
}
