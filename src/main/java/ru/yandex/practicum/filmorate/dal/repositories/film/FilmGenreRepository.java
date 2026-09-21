package ru.yandex.practicum.filmorate.dal.repositories.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Repository
public class FilmGenreRepository {

    private static final String ADD_GENRE_QUERY =
            "INSERT INTO film_genres(film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_FILM_GENRES_QUERY =
            "DELETE FROM film_genres WHERE film_id = ?";

    private static final String FIND_FILM_GENRES_QUERY =
            "SELECT g.* " +
                    "FROM genres g " +
                    "JOIN film_genres fg ON g.id = fg.genre_id " +
                    "WHERE fg.film_id = ? " +
                    "ORDER BY g.id";

    private static final String FIND_GENRES_BY_FILM_IDS = "SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name " +
            "FROM genres g " +
            "JOIN film_genres fg ON fg.genre_id = g.id " +
            "WHERE fg.film_id IN (%s)";

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreMapper;

    public FilmGenreRepository(
            JdbcTemplate jdbc,
            RowMapper<Genre> genreMapper
    ) {
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
        return new LinkedHashSet<>(
                jdbc.query(FIND_FILM_GENRES_QUERY, genreMapper, filmId)
        );
    }

    public Map<Long, Set<Genre>> getMapGenresFromFilmIds(List<Long> filmIds) {
        String placeholders = IntStream.range(0, filmIds.size())
                .mapToObj(i -> "?")
                .collect(Collectors.joining(", "));

        String finalSql = FIND_GENRES_BY_FILM_IDS.formatted(placeholders);
        return jdbc.query(finalSql,
                (rs) -> {
                    Map<Long, Set<Genre>> map = new HashMap<>();
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Genre genre = new Genre();
                        genre.setId(rs.getLong("genre_id"));
                        genre.setName(rs.getString("genre_name"));
                        map.computeIfAbsent(filmId, k -> new HashSet<>()).add(genre);
                    }
                    return map;
                },
                (Object[]) filmIds.toArray(new Long[0])
        );
    }
}