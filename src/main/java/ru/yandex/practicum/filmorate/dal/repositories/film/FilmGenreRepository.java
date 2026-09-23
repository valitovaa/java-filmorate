package ru.yandex.practicum.filmorate.dal.repositories.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.*;

@Slf4j
@Repository
public class FilmGenreRepository {

    private static final String ADD_GENRE_QUERY = "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_FILM_GENRES_QUERY = "DELETE FROM film_genres WHERE film_id = ?";

    private static final String FIND_FILM_GENRES_QUERY = "SELECT g.* " + "FROM genres g " + "JOIN film_genres fg ON g.id = fg.genre_id " + "WHERE fg.film_id = ? " + "ORDER BY g.id";

    private static final String INSERT_QUERY = """
            INSERT INTO film_genres (film_id, genre_id)
            VALUES (?, ?)
            """;

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

    public Set<Genre> findGenresByFilmId(long filmId) {
        return new LinkedHashSet<>(jdbc.query(FIND_FILM_GENRES_QUERY, genreMapper, filmId));
    }

    public void addGenres(Long filmId, Set<Genre> genres) {
        jdbc.batchUpdate(INSERT_QUERY, genres, genres.size(), (ps, genre) -> {
            ps.setLong(1, filmId);
            ps.setLong(2, genre.getId());
        });
    }
}