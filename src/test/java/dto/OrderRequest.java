package dto;

public class OrderRequest {
    private String orderType;
    private String productId;
    private int quantity;

    public OrderRequest(String orderType, String productId, int quantity) {
        this.orderType = orderType;
        this.productId = productId;
        this.quantity = quantity;
    }

    public String getOrderType() {
        return orderType;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}