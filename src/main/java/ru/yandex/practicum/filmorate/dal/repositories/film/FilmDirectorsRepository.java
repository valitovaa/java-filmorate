package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.film.DirectorRowMapper;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.film.Director;

import java.util.List;

@Repository
public class FilmDirectorsRepository extends BaseRepository<Director> {
    private static final String INSERT_QUERY =
            "INSERT INTO film_directors(film_id, director_id) VALUES (?, ?)";
    private static final String DELETE_BY_FILM_QUERY =
            "DELETE FROM film_directors WHERE film_id = ?";
    private static final String FIND_DIRECTORS_BY_FILM_QUERY =
            "SELECT d.* FROM directors d " +
                    "JOIN film_directors fd ON d.id = fd.director_id " +
                    "WHERE fd.film_id = ?";

    public FilmDirectorsRepository(JdbcTemplate jdbc, DirectorRowMapper rowMapper) {
        super(jdbc, rowMapper);
    }

    public void addDirectorsToFilm(Long filmId, List<Director> directors) {
        for (Director director : directors) {
            jdbc.update(INSERT_QUERY, filmId, director.getId());
        }
    }

    public void deleteDirectorsByFilm(Long filmId) {
        delete(DELETE_BY_FILM_QUERY, filmId);
    }

    public List<Director> getDirectorsByFilm(Long filmId) {
        return findMany(FIND_DIRECTORS_BY_FILM_QUERY, filmId);
    }
}
