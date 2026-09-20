package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repositories.film.DirectorRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;

@Service
public class DirectorService {
    private final DirectorRepository directorRepository;

    public DirectorService(DirectorRepository directorRepository) {
        this.directorRepository = directorRepository;
    }

    public Director create(Director director) {
        return directorRepository.create(director);
    }

    public List<Director> getAllDirectors() {
        return directorRepository.getAllDirectors();
    }

    public Director getDirectorById(Long id) {
        return directorRepository.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
    }

    public Director update(Director director) {
        return directorRepository.update(director);
    }

    public void delete(Long id) {
        directorRepository.delete(id);
    }
}
