package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;

import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {

    private final FilmDirectorsRepository filmDirectorsRepository;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa = ? WHERE id = ?";

    private static final String FIND_POPULAR_QUERY = "SELECT f.* " +
            "FROM films f " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.user_id) DESC " +
            "LIMIT ?";

    private static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES = "SELECT f.*, " +
            "(SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.id) AS like_count " +
            "FROM films f " +
            "JOIN film_directors fd ON f.id = fd.film_id " +
            "WHERE fd.director_id = ? " +
            "ORDER BY like_count DESC, f.release_date DESC";

    private static final String FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR = "SELECT f.* " +
            "FROM films f " +
            "JOIN film_directors fd ON f.id = fd.film_id " +
            "WHERE fd.director_id = ? " +
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

    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper,
                          FilmDirectorsRepository filmDirectorsRepository) {
        super(jdbc, mapper);
        this.filmDirectorsRepository = filmDirectorsRepository;
    }

    private void loadDirectorsForFilms(List<Film> films) {
        if (films.isEmpty()) return;

        List<Long> filmIds = films.stream().map(Film::getId).toList();
        String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));

        Map<Long, List<Director>> directorsByFilmId = jdbc.query(
                "SELECT fd.film_id, d.id, d.name " +
                        "FROM film_directors fd " +
                        "JOIN directors d ON fd.director_id = d.id " +
                        "WHERE fd.film_id IN (" + placeholders + ")",
                rs -> {
                    Map<Long, List<Director>> map = new HashMap<>();
                    while (rs.next()) {
                        Long filmId = rs.getLong("film_id");
                        Director d = new Director();
                        d.setId(rs.getLong("id"));
                        d.setName(rs.getString("name"));
                        map.computeIfAbsent(filmId, k -> new ArrayList<>()).add(d);
                    }
                    return map;
                },
                filmIds.toArray()
        );

        films.forEach(f -> f.setDirectors(
                directorsByFilmId.getOrDefault(f.getId(), List.of())
        ));
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        loadDirectorsForFilms(films);
        return films;
    }

    public Optional<Film> findById(Long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(f -> {
            List<Director> directors = filmDirectorsRepository.getDirectorsByFilm(id);
            f.setDirectors(directors);
        });
        return film;
    }

    public Film addFilm(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().name() : null);

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            filmDirectorsRepository.addDirectorsToFilm(id, film.getDirectors());
        }

        return findById(id).orElseThrow(() -> new NotFoundException("Фильм не сохранён"));
    }

    public Film update(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa() != null ? film.getMpa().name() : null,
                film.getId());

        filmDirectorsRepository.deleteDirectorsByFilm(film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            filmDirectorsRepository.addDirectorsToFilm(film.getId(), film.getDirectors());
        }

        return findById(film.getId())
                .orElseThrow(() -> new NotFoundException("Фильм не найден"));
    }

    public List<Film> findPopularFilms(int count) {
        List<Film> films = findMany(FIND_POPULAR_QUERY, count);
        loadDirectorsForFilms(films);
        return films;
    }

    public List<Film> findFilmsByDirector(Long directorId, String sortBy) {
        if (sortBy.equals("likes")) {
            return findFilmsByLikes(directorId);
        } else if (sortBy.equals("year")) {
            return findFilmsByYear(directorId);
        }
        throw new IllegalArgumentException(
                "Неподдерживаемый тип сортировки: " + sortBy + ". Допустимые значения: 'likes', 'year'"
        );
    }

    private List<Film> findFilmsByLikes(Long directorId) {
        List<Film> films = findMany(FIND_FILMS_BY_DIRECTOR_SORT_BY_LIKES, directorId);
        loadDirectorsForFilms(films);
        return films;
    }

    private List<Film> findFilmsByYear(Long directorId) {
        List<Film> films = findMany(FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR, directorId);
        loadDirectorsForFilms(films);
        return films;
    }

    public List<Film> findCommonFilmsByUsers(long userId, long friendId) {
        List<Film> films = findMany(COMMON_FILMS_BY_USERS_QUERY, userId, friendId);
        loadDirectorsForFilms(films);
        return films;
    }

    public List<Film> findFilmsByIds(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));
        List<Film> films = findMany(FIND_BY_IDS_QUERY.formatted(placeholders), filmIds.toArray());
        loadDirectorsForFilms(films);
        return films;
    }

    public void deleteFilm(Long filmId) {
        if (filmId == null) {
            throw new IllegalArgumentException("Id должен быть указан");
        }
        boolean deleted = delete(DELETE_QUERY, filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм не найден");
        }
    }
}
