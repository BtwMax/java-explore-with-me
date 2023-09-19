package ru.practicum.ewmservice.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmservice.request.model.ParticipationRequest;
import ru.practicum.ewmservice.user.model.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {

    List<ParticipationRequest> findAllByRequester(User user);

    ParticipationRequest findByRequesterIdAndEventId(long userId, long eventId);

    @Query("SELECT COUNT(pr.id) FROM ParticipationRequest AS pr " +
            "WHERE pr.event.id IN :eventId " +
            "AND pr.status = 'CONFIRMED'")
    Long findConfirmedRequests(long eventId);

    List<ParticipationRequest> findAllByEventId(long eventId);

    List<ParticipationRequest> findAllByIdIn(List<Long> ids);
}
