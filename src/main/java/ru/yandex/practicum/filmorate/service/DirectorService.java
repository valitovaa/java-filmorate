package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ValidationException;
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
        if (director.getName() == null) {
            throw new ValidationException("Имя должно быть указано");
        }

        return directorRepository.create(director);
    }

    public List<Director> getAllDirectors() {
        return directorRepository.getAllDirectors();
    }

    public Director getDirectorById(Long id) {
        if (id == null) {
            throw new ValidationException("Id должен быть указан");
        }

        return directorRepository.getDirectorById(id).orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
    }

    public Director update(Director newDirector) {
        if (newDirector.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        if (newDirector.getName() == null || newDirector.getName().isBlank()) {
            throw new ValidationException("Имя должно быть указано");
        }

        return directorRepository.update(newDirector);
    }

    public void delete(Long id) {
        directorRepository.delete(id);
    }
}
