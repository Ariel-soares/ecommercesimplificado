package com.arielsoares.ecommercesimplificado.entities.enums;

public enum OrderStatus {
	
	OPEN(1),
	WAITING_PAYMENT(2),
    PAID(3),
    SHIPPED(4),
    COMPLETE(5),
    CANCELLED(6);

    private int code;

    private OrderStatus(int code){
        this.code = code;
    }

    public int getCode(){
        return code;
    }

    public static OrderStatus valueOf(int code){
        for (OrderStatus value : OrderStatus.values()){
            if (value.getCode() == code){
                return value;
            }
        }
        throw new IllegalArgumentException("Invalid status code");
    }

}
