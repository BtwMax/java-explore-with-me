package ru.practicum.ewmservice.events.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.events.model.Location;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    Optional<Location> findByLatAndLon(float lat, float lon);
}
