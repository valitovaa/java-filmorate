package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.List;
import java.util.Optional;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " + "duration = ?, mpa = ? WHERE id = ?";

    private static final String FIND_POPULAR_QUERY = "SELECT f.* " + "FROM films f " + "LEFT JOIN film_likes fl ON f.id = fl.film_id " + "GROUP BY f.id " + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";

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
        long id = insert(INSERT_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa() != null ? film.getMpa().name() : null);

        return findById(id)
                .orElseThrow(() -> new NotFoundException("Film was not saved"));
    }

    public Film update(Film film) {
        update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(), film.getMpa() != null ? film.getMpa().name() : null, film.getId());

        return film;
    }

    public List<Film> findPopularFilms(int count) {
        return findMany(FIND_POPULAR_QUERY, count);
    }

    public List<Film> findCommonFilmsByUsers(long userId, long friendId) {
        return findMany(COMMON_FILMS_BY_USERS_QUERY, userId, friendId);
    }
}