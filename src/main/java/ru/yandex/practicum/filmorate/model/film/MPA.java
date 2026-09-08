package ru.yandex.practicum.filmorate.model.film;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MPA {

    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final int id;
    private final String name;

    @JsonCreator
    public static MPA fromJson(JsonNode node) {
        int id = node.get("id").asInt();

        for (MPA mpa : values()) {
            if (mpa.id == id) {
                return mpa;
            }
        }

        throw new IllegalArgumentException("Неизвестный MPA id: " + id);
    }
}