package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.film.DirectorRowMapper;
import ru.yandex.practicum.filmorate.exception.DatabaseException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository {
    private static final String FIND_ALL_QUERY = "SELECT * FROM directors";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO directors(name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE directors WHERE id = ?";

    private final JdbcTemplate jdbc;
    private final DirectorRowMapper rowMapper;

    public DirectorRepository(JdbcTemplate jdbc, DirectorRowMapper rowMapper) {
        this.jdbc = jdbc;
        this.rowMapper = rowMapper;
    }

    public Director create(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, director.getName());
            return ps; }, keyHolder);

        Long id = keyHolder.getKeyAs(Long.class);

        if (id != null) {
            director.setId(id);
            return director;
        } else {
            throw new DatabaseException("Не удалось сохранить данные");
        }
    }

    public List<Director> getAllDirectors() {
        return jdbc.query(FIND_ALL_QUERY, rowMapper);
    }

    public Optional<Director> getDirectorById(Long id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(FIND_BY_ID_QUERY, rowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public Director update(Director director) {
        int rowsAffected = jdbc.update(UPDATE_QUERY, director.getName(), director.getId());
        if (rowsAffected == 0) {
            throw new NotFoundException("Режиссёр не найден");
        }
        return director;
    }

    public void delete(Long id) {
        int rowsAffected = jdbc.update(DELETE_QUERY, id);
        if (rowsAffected == 0) {
            throw new NotFoundException("Режиссёр не найден");
        }
    }
}
