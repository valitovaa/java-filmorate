package ru.yandex.practicum.filmorate.dal.repositories.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.feed.Event;

import java.util.List;

@Repository
public class EventRepository extends BaseRepository<Event> {

    private static final String FIND_BY_USER_ID_QUERY = """
            SELECT *
            FROM feed_events
            WHERE user_id = ?
            ORDER BY timestamp ASC
            """;

    private static final String INSERT_QUERY = """
            INSERT INTO feed_events (
                timestamp,
                user_id,
                event_type,
                operation,
                entity_id
            )
            VALUES (?, ?, ?, ?, ?)
            """;


    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public void addEvent(Event event) {
        long id = insert(
                INSERT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );

        event.setEventId(id);
    }

    public List<Event> findByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

}