package ru.practicum.stats.statsdto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
public class OutHitsDto {
    private String app;
    private String uri;
    private long hits;

    public OutHitsDto(String app, String uri, long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}
