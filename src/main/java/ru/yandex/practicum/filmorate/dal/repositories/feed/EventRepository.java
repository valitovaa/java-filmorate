package ru.yandex.practicum.filmorate.dal.repositories.feed;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
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

    private static final String FIND_BY_ID_QUERY = """
            SELECT *
            FROM feed_events
            WHERE event_id = ?
            """;

    public EventRepository(JdbcTemplate jdbc, RowMapper<Event> mapper) {
        super(jdbc, mapper);
    }

    public Event addEvent(Event event) {
        long id = insert(
                INSERT_QUERY,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId()
        );

        return findOne(FIND_BY_ID_QUERY, id).orElseThrow(
                () -> new DatabaseException("Не удалось найти добавленное событие")
        );
    }

    public List<Event> findByUserId(Long userId) {
        return findMany(FIND_BY_USER_ID_QUERY, userId);
    }

}