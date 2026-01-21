package com.hmsService.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Room {

    public enum Status { VACANT, OCCUPIED, MAINTENANCE }

    public enum Type {
        SINGLE, DOUBLE, TWIN, SUITE, DELUXE, FAMILY
    }

    private Integer roomNo;
    private Status status;
    private LocalDate availabilityDate;
    private Type type;
    private BigDecimal price;

    public Room() {
        this.status = Status.VACANT;
    }

    public Room(Integer roomNo, Type type, BigDecimal price, Status status, LocalDate availabilityDate) {
        this.roomNo = roomNo;
        this.type = type;
        this.price = price;
        this.status = status;
        this.availabilityDate = availabilityDate;
    }

    public Integer getRoomNo() {
        return roomNo;
    }

    public void setRoomNo(Integer roomNo) {
        this.roomNo = roomNo;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDate getAvailabilityDate() {
        return availabilityDate;
    }

    public void setAvailabilityDate(LocalDate availabilityDate) {
        this.availabilityDate = availabilityDate;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
