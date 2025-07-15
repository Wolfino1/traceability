package com.plazoleta.trazabilidad.domain.models;

import java.time.LocalDateTime;

public class TraceabilityModel {

    private String id;
    private Long restaurantId;
    private Long orderId;
    private Long clientId;
    private String clientEmail;
    private LocalDateTime date;
    private OrderStatus previousState;
    private OrderStatus newState;
    private Long employeeId;
    private String employeeEmail;

    public TraceabilityModel(Long restaurantId, Long orderId, Long clientId, String clientEmail, LocalDateTime date,
                             OrderStatus previousState, OrderStatus newState, Long employeeId, String employeeEmail) {

        this.restaurantId =restaurantId;
        this.orderId = orderId;
        this.clientId = clientId;
        this.clientEmail = clientEmail;
        this.date = date;
        this.previousState = previousState;
        this.newState = newState;
        this.employeeId = employeeId;
        this.employeeEmail = employeeEmail;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public OrderStatus getPreviousState() {
        return previousState;
    }

    public void setPreviousState(OrderStatus previousState) {
        this.previousState = previousState;
    }

    public OrderStatus getNewState() {
        return newState;
    }

    public void setNewState(OrderStatus newState) {
        this.newState = newState;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
}
