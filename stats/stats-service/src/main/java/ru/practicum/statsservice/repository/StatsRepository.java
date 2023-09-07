package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.statsservice.model.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT s.app, s.uri, COUNT(s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<OutHitsDto> getAllNotUniqueIpHits(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.app, s.uri, COUNT(distinct s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<OutHitsDto> getAllUniqueIpHits(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.app, s.uri, COUNT(s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    Optional<OutHitsDto> getByUriAndNotUniqueIpHits(String uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT s.app, s.uri, COUNT(distinct s.ip) AS hits " +
            "FROM Stats AS s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    Optional<OutHitsDto> getByUriAndUniqueIpHits(String uri, LocalDateTime start, LocalDateTime end);
}
