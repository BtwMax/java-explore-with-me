package ru.practicum.ewmservice.comments.model;

import lombok.*;
import ru.practicum.ewmservice.events.model.Event;
import ru.practicum.ewmservice.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "events_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author")
    private User author;

    @Column
    private String text;

    @Column
    private LocalDateTime created;
}
