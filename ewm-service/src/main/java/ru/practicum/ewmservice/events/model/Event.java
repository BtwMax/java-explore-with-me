package ru.practicum.ewmservice.events.model;

import lombok.*;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.ewmservice.category.model.Category;
import ru.practicum.ewmservice.events.model.enums.State;
import ru.practicum.ewmservice.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column
    private LocalDateTime createdOn;

    @Column
    private String description;

    @Column
    private LocalDateTime eventDate;

    @ManyToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User initiator;

    @ManyToOne
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    private Boolean paid;

    @Column
    private Long participantLimit;

    @Column
    private LocalDateTime publishedOn;

    @Column
    private Boolean requestModeration;

    @Enumerated(EnumType.STRING)
    private State state;

    @Column
    private String title;

    @Formula("(SELECT COUNT(pr.id) FROM participation_requests AS pr " +
            "WHERE pr.event_id = id AND pr.status = 'CONFIRMED')")
    private int confirmedRequests;

    @Transient
    private long views;

}
