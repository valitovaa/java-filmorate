package ru.yandex.practicum.filmorate.storage.film.genre;

import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreStorage {

    List<Genre> findAll();

    Optional<Genre> findById(Long id);

    Set<Long> findExistingIds(Set<Long> ids);
}