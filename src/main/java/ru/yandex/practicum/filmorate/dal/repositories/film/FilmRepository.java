package ru.yandex.practicum.filmorate.dal.repositories.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {

    private final FilmDirectorsRepository filmDirectorsRepository;

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa) " +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, mpa = ? WHERE id = ?";

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

    private static final String FIND_MOST_POPULAR_QUERY = """
            SELECT f.id, f.name, f.description, f.release_date,
                   f.duration, f.mpa, COUNT(DISTINCT fl.user_id) AS likes
            FROM films f
            LEFT JOIN film_genres fg ON f.id = fg.film_id
            LEFT JOIN film_likes fl ON f.id = fl.film_id
            WHERE (? IS NULL OR fg.genre_id = ?)
              AND (? IS NULL OR f.release_date BETWEEN ? AND ?)
            GROUP BY f.id, f.name, f.description, f.release_date, f.duration, f.mpa
            ORDER BY likes DESC
            LIMIT ?
            """;

    private static final String SEARCH_FILMS_QUERY = """
            SELECT f.*,
                   COUNT(fl.user_id) AS likes_count
            FROM films f
                     LEFT JOIN film_likes fl ON f.id = fl.film_id
                     LEFT JOIN film_directors fd ON f.id = fd.film_id
                     LEFT JOIN directors dir ON fd.director_id = dir.id
            WHERE (? AND LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')))
               OR (? AND LOWER(dir.name) LIKE LOWER(CONCAT('%', ?, '%')))
            GROUP BY f.id
            ORDER BY likes_count DESC;
            """;

    private static final String FIND_GENRES_BY_FILM_IDS_QUERY = """
            SELECT fg.film_id, g.id AS genre_id, g.name AS genre_name
            FROM film_genres fg
            JOIN genres g ON g.id = fg.genre_id
            WHERE fg.film_id IN (%s)
            ORDER BY g.id
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

        if (directorsByFilmId == null) {
            return;
        }

        films.forEach(f -> f.setDirectors(
                directorsByFilmId.getOrDefault(f.getId(), List.of())
        ));
    }

    public List<Film> findAll() {
        List<Film> films = findMany(FIND_ALL_QUERY);
        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    public Optional<Film> findById(Long id) {
        Optional<Film> film = findOne(FIND_BY_ID_QUERY, id);
        film.ifPresent(f -> {
            List<Director> directors = filmDirectorsRepository.getDirectorsByFilm(id);
            f.setDirectors(directors);
            fillGenres(List.of(f));
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
        fillGenres(films);
        return films;
    }

    private List<Film> findFilmsByYear(Long directorId) {
        List<Film> films = findMany(FIND_FILMS_BY_DIRECTOR_SORT_BY_YEAR, directorId);
        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    public List<Film> findCommonFilmsByUsers(long userId, long friendId) {
        List<Film> films = findMany(COMMON_FILMS_BY_USERS_QUERY, userId, friendId);
        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    public List<Film> findFilmsByIds(List<Long> filmIds) {
        if (filmIds.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(", ", Collections.nCopies(filmIds.size(), "?"));
        List<Film> films = findMany(FIND_BY_IDS_QUERY.formatted(placeholders), filmIds.toArray());
        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    public List<Film> findPopularFilms(Long count, Long genreId, Long year) {
        LocalDate from = year != null ? LocalDate.of(year.intValue(), 1, 1) : null;
        LocalDate to = year != null ? LocalDate.of(year.intValue(), 12, 31) : null;
        int limit = count != null ? count.intValue() : Integer.MAX_VALUE;

        List<Film> films = findMany(FIND_MOST_POPULAR_QUERY,
                genreId, genreId,
                year, from, to,
                limit);

        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    public void deleteFilm(Long filmId) {
        boolean deleted = delete(DELETE_QUERY, filmId);
        if (!deleted) {
            throw new NotFoundException("Фильм не найден");
        }
    }

    public List<Film> searchFilms(String query, boolean byTitle, boolean byDirector) {
        List<Film> films = findMany(SEARCH_FILMS_QUERY, byTitle, query, byDirector, query);
        loadDirectorsForFilms(films);
        fillGenres(films);
        return films;
    }

    private void fillGenres(List<Film> films) {
        if (films == null || films.isEmpty()) {
            return;
        }

        Map<Long, Film> filmsById = new LinkedHashMap<>();
        for (Film film : films) {
            film.setGenres(new LinkedHashSet<>());
            filmsById.put(film.getId(), film);
        }

        String placeholders = String.join(",", Collections.nCopies(films.size(), "?"));
        String sql = FIND_GENRES_BY_FILM_IDS_QUERY.formatted(placeholders);
        Object[] args = films.stream().map(Film::getId).toArray();

        jdbc.query(sql, (rs, rowNum) -> {
            long filmId = rs.getLong("film_id");
            Genre genre = new Genre(rs.getLong("genre_id"), rs.getString("genre_name"));
            Film film = filmsById.get(filmId);
            if (film != null) {
                film.getGenres().add(genre);
            }
            return null;
        }, args);

        for (Film film : films) {
            Set<Genre> sorted = film.getGenres().stream()
                    .sorted(Comparator.comparing(Genre::getId))
                    .collect(Collectors.toCollection(LinkedHashSet::new));
            film.setGenres(sorted);
        }
    }
}
