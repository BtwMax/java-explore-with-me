package ru.practicum.ewmservice.stats.statsdto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@Getter
@Setter
@NoArgsConstructor
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
