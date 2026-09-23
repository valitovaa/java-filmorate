package ru.yandex.practicum.filmorate.model.feed;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "eventId")
public class Event {
    //    "timestamp": 123344556,
//            "userId": 123,
//            "eventType": "LIKE", // одно из значениий LIKE, REVIEW или FRIEND
//            "operation": "REMOVE", // одно из значениий REMOVE, ADD, UPDATE
//            "eventId": 1234, //primary key
//            "entityId": 1234   // идентификатор сущности, с которой произошло событие
    Long timestamp;
    Long userId;
    EventType eventType;
    Operation operation;
    Long eventId;
    Long entityId;

}
