package ru.practicum.statsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.practicum.stats.statsdto.OutHitsDto;
import ru.practicum.statsservice.model.Stats;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@RepositoryRestResource
public interface StatsRepository extends JpaRepository<Stats, Long> {

    @Query("SELECT new ru.practicum.stats.statsdto.OutHitsDto(s.app, s.uri, COUNT(distinct s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip)")
    List<OutHitsDto> findAllUniqueId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.statsdto.OutHitsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip)")
    List<OutHitsDto> findAllNotUniqueId(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.statsdto.OutHitsDto(s.app, s.uri, COUNT(distinct s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri")
    Optional<OutHitsDto> findByUriAndUniqueId(String uri, LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.stats.statsdto.OutHitsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stats AS s " +
            "WHERE s.uri = ?1 AND s.timestamp BETWEEN ?2 AND ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip)")
    Optional<OutHitsDto> findByUriAndNotUniqueId(String uri, LocalDateTime start, LocalDateTime end);

}
