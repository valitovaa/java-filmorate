package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.MpaResponse;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.util.Arrays;
import java.util.List;

@Service
public class MpaService {

    public List<MpaResponse> findAll() {
        return Arrays.stream(MPA.values())
                .map(mpa -> new MpaResponse(mpa.getId(), mpa.getName()))
                .toList();
    }

    public MpaResponse findById(int id) {
        return Arrays.stream(MPA.values())
                .filter(mpa -> mpa.getId() == id)
                .findFirst()
                .map(mpa -> new MpaResponse(mpa.getId(), mpa.getName()))
                .orElseThrow(() ->
                        new NotFoundException(
                                "MPA с id = " + id + " не найден"
                        )
                );
    }
}