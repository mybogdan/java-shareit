package ru.practicum.shareit.booking.enums;

public enum BookingStatus {
    APPROVED("approved"),
    WAITING("waiting"),
    REJECTED("rejected"),
    CANCELED("canceled");

    private String name;

    BookingStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}