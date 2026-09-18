package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT f.*, d.name AS director_name FROM films f LEFT JOIN directors d ON f.director_id = d.id WHERE f.id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa, director_id) " + "VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " + "duration = ?, mpa = ?, director_id = ? WHERE id = ?";

    private static final String FIND_POPULAR_QUERY = "SELECT f.* " + "FROM films f " + "LEFT JOIN film_likes fl ON f.id = fl.film_id " + "GROUP BY f.id " + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";

    private static final String FIND_FILMS_BY_LIKES = "SELECT f.*, " +
            "       d.name AS director_name, " +
            "       (SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.id) AS like_count " +
            "FROM films f " +
            "LEFT JOIN directors d ON f.director_id = d.id " +
            "WHERE f.director_id = ? " +
            "ORDER BY like_count DESC, f.release_date DESC";
    private static final String FIND_FILMS_BY_YEAR = "SELECT f.*, d.name AS director_name " +
            "FROM films f " +
            "LEFT JOIN directors d ON f.director_id = d.id " +
            "WHERE f.director_id = ? " +
            "ORDER BY f.release_date ASC";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM films WHERE id IN (%s)";

    private static final String COMMON_FILMS_BY_USERS_QUERY = """
            SELECT f.*
            FROM films f
                     JOIN film_likes fl1 ON f.id = fl1.film_id
                     JOIN film_likes fl2 ON f.id = fl2.film_id
            WHERE fl1.user_id = ?
              AND fl2.user_id = ?
            ORDER BY (
                         SELECT COUNT(*)
                         FROM film_likes fl
                         WHERE fl.film_id = f.id
                         ) DESC;
            """;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    public List<Film> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Film> findById(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId);
    }

    public Film addFilm(Film film) {
        Long directorId = null;
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorId = film.getDirectors().get(0).getId();
        }

        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa() != null ? film.getMpa().name() : null, directorId);

        return findById(id).orElseThrow(() -> new NotFoundException("Film was not saved"));
    }

    public Film update(Film film) {
        Long directorId = null;
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            directorId = film.getDirectors().get(0).getId();
        }

        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa() != null ? film.getMpa().name() : null, directorId, film.getId());

        return film;
    }

    public List<Film> findPopularFilms(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    public Collection<Film> findFilmsByDirector(Long directorId, String sortBy) {
        if (sortBy.equals("likes")) {
            return findFilmsByLikes(directorId);
        } else if (sortBy.equals("year")) {
            return findFilmsByYear(directorId);
        }

        return Collections.emptyList();
    }

    private Collection<Film> findFilmsByLikes(Long directorId) {
        return findMany(FIND_FILMS_BY_LIKES, directorId);
    }

    private Collection<Film> findFilmsByYear(Long directorId) {
        return findMany(FIND_FILMS_BY_YEAR, directorId);
    }

    public List<Film> findCommonFilmsByUsers(long userId, long friendId) {
        return findMany(COMMON_FILMS_BY_USERS_QUERY, userId, friendId);
    }


    public List<Film> findFilmsByIds(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));

        return findMany(FIND_BY_IDS_QUERY.formatted(placeholders), filmIds.toArray());
    }
}
