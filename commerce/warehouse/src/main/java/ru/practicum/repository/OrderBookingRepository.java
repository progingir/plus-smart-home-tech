package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.model.OrderBooking;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBooking, UUID> {
}
