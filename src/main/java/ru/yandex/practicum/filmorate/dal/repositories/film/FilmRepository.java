package ru.yandex.practicum.filmorate.dal.repositories.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.*;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {

    @Autowired
    private FilmGenreRepository filmGenreRepository;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa) " + "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " + "duration = ?, mpa = ? WHERE id = ?";

    private static final String FIND_POPULAR_QUERY = "SELECT f.* " + "FROM films f " + "LEFT JOIN film_likes fl ON f.id = fl.film_id " + "GROUP BY f.id " + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";

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

    private static final String FIND_MOST_POPULARS_FILM_TO_GENRE_ID_AND_YEAR_QUERY = "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, f.mpa, COUNT(fl.user_id) AS likes, fg.genre_id, g.name AS genre_name " +
            "FROM films f " +
            "INNER JOIN film_genres fg ON f.id = fg.film_id " +
            "INNER JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "WHERE fg.genre_id = ? AND f.release_date BETWEEN %s " +
            "GROUP BY f.id, f.name, f.description, f.duration, f.mpa, fg.genre_id, g.name " +
            "ORDER BY likes DESC " +
            "LIMIT ?";


    private static final String FIND_MOST_POPULARS_FILM_TO_YEAR_QUERY = "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, f.mpa, COUNT(fl.user_id) AS likes, fg.genre_id, g.name AS genre_name " +
            "FROM films f " +
            "INNER JOIN film_genres fg ON f.id = fg.film_id " +
            "INNER JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "WHERE f.release_date BETWEEN %s " +
            "GROUP BY f.id, f.name, f.description, f.duration, f.mpa, fg.genre_id, g.name " +
            "ORDER BY likes DESC " +
            "LIMIT ?";

    private static final String FIND_MOST_POPULARS_FILM_TO_GENRE_ID_QUERY = "SELECT f.id, f.name, f.description, f.release_date, " +
            "f.duration, f.mpa, COUNT(fl.user_id) AS likes, fg.genre_id, g.name AS genre_name " +
            "FROM films f " +
            "INNER JOIN film_genres fg ON f.id = fg.film_id " +
            "INNER JOIN genres g ON fg.genre_id = g.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "WHERE fg.genre_id = ? " +
            "GROUP BY f.id, f.name, f.description, f.duration, f.mpa, fg.genre_id, g.name " +
            "ORDER BY likes DESC " +
            "LIMIT ?";

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

        return findById(id).orElseThrow(() -> new NotFoundException("Film was not saved"));
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


    public List<Film> findFilmsByIds(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return List.of();
        }

        String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));

        return findMany(FIND_BY_IDS_QUERY.formatted(placeholders), filmIds.toArray());
    }

    public List<Film> findMostPopularsFilm(int count, Long genreId, Long year) {

        List<Film> films = new ArrayList<>();
        String addDateToSqlQuery = "DATE '" + year + "-01-01' AND DATE '" + year + "-12-31'";

        if (genreId != null && year != null) {
            String finalSql = FIND_MOST_POPULARS_FILM_TO_GENRE_ID_AND_YEAR_QUERY.formatted(addDateToSqlQuery);
            films = findMany(finalSql, genreId, count);
        }
        if (genreId != null && year == null) {
            films = findMany(FIND_MOST_POPULARS_FILM_TO_GENRE_ID_QUERY, genreId, count);

        }
        if (genreId == null && year != null) {
            String finalSql = FIND_MOST_POPULARS_FILM_TO_YEAR_QUERY.formatted(addDateToSqlQuery);
            films = findMany(finalSql, count);
        }
        if (genreId == null && year == null) {
            films = findPopularFilms(count);
        }

        return fillGenreInFilms(films);
    }

    private List<Film> fillGenreInFilms(List<Film> films) {
        log.warn("FilmRepository: films={}", films);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        log.warn("FilmRepository: filmIds={}", filmIds);
        Map<Long, Set<Genre>> filmGenreMap = filmGenreRepository.getMapGenresFromFilmIds(filmIds);
        for (Film f : films) {
            if (filmGenreMap.containsKey(f.getId())) {
                f.setGenres(filmGenreMap.get(f.getId()));
            } else f.setGenres(Set.of(new Genre()));
        }

        return films;
    }
}