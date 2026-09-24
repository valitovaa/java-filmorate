package ru.yandex.practicum.filmorate.model.feed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "eventId")
public class Event {
    Long timestamp;
    Long userId;
    EventType eventType;
    Operation operation;
    Long eventId;
    Long entityId;

}
