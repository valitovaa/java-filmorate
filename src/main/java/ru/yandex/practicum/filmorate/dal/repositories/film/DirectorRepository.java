package ru.yandex.practicum.filmorate.dal.repositories.film;

import jakarta.validation.ValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;
import java.util.Optional;

@Repository
public class    DirectorRepository extends BaseRepository<Director> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";

    public DirectorRepository(JdbcTemplate jdbc, RowMapper<Director> rowMapper) {
        super(jdbc, rowMapper);
    }

    public Director create(Director director) {

        long id = insert(INSERT_QUERY, director.getName());
        return findOne(FIND_BY_ID_QUERY, id)
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден после сохранения"));
    }

    public Director update(Director director) {
        if (director.getId() == null) {
            throw new ValidationException("Id должен быть указан");
        }

        update(UPDATE_QUERY, director.getName(), director.getId());
        return findOne(FIND_BY_ID_QUERY, director.getId())
                .orElseThrow(() -> new NotFoundException("Режиссёр не найден"));
    }

    public List<Director> getAllDirectors() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Director> getDirectorById(Long id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public void delete(Long id) {
        if (!delete(DELETE_QUERY, id)) {
            throw new NotFoundException("Режиссёр не найден");
        }
    }
}
