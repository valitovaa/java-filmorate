package ru.yandex.practicum.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.List;

@Repository
public class FilmGenreRepository {

    private static final String ADD_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String FIND_FILM_GENRES_QUERY = "SELECT g.* " + "FROM genres g " + "JOIN film_genres fg ON g.id = fg.genre_id " + "WHERE fg.film_id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreMapper;

    public FilmGenreRepository(JdbcTemplate jdbc, RowMapper<Genre> genreMapper) {
        this.jdbc = jdbc;
        this.genreMapper = genreMapper;
    }

    public void addGenre(long filmId, long genreId) {
        jdbc.update(ADD_GENRE_QUERY, filmId, genreId);
    }

    public void deleteGenres(long filmId) {
        jdbc.update(DELETE_FILM_GENRES_QUERY, filmId);
    }

    public List<Genre> findGenresByFilmId(long filmId) {
        return jdbc.query(FIND_FILM_GENRES_QUERY, genreMapper, filmId);
    }
}