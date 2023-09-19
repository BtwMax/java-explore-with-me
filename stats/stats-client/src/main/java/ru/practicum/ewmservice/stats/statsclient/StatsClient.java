package ru.practicum.ewmservice.stats.statsclient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewmservice.stats.statsdto.GetStatDto;
import ru.practicum.ewmservice.stats.statsdto.InnerStatsDto;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsClient extends BaseClient {

    @Autowired
    public StatsClient(@Value("http://localhost:9090") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }


    public ResponseEntity<Object> addStats(InnerStatsDto innerStatsDto) {
        return post("/hit", innerStatsDto);
    }

    public ResponseEntity<Object> getStats(String start, String end, List<String> uris, boolean unique) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("start", start);
        parameters.put("end", end);
        if (uris != null && uris.size() != 0) {
            parameters.put("uris", uris);
        }
        parameters.put("unique", unique);

        if (parameters.containsKey("uris")) {
            return get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);

        } else {
            return get("/stats?start={start}&end={end}&unique={unique}", parameters);
        }
    }

    public long getStat(GetStatDto getStatDto) {
        Map<String, Object> parameters = Map.of(
                "start", getStatDto.getStart().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "end", getStatDto.getEnd().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "uris", getStatDto.getUris(),
                "unique", getStatDto.isUnique()
        );
        return getHits("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
    }
}
