package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.util.List;

@Service
public class MpaService {

    public List<MpaResponse> findAll() {
        return List.of(
                new MpaResponse(1, "G"),
                new MpaResponse(2, "PG"),
                new MpaResponse(3, "PG-13"),
                new MpaResponse(4, "R"),
                new MpaResponse(5, "NC-17")
        );
    }

    public MpaResponse findById(int id) {
        for (MPA mpa : MPA.values()) {
            if (mpa.getId() == id) {
                return new MpaResponse(
                        mpa.getId(),
                        mpa.getName()
                );
            }
        }

        throw new NotFoundException("Рейтинг с id = " + id + " не найден");
    }
}