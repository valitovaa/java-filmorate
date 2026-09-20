    package ru.yandex.practicum.filmorate.dal.repositories.film;

    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.jdbc.core.RowMapper;
    import org.springframework.stereotype.Repository;
    import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
    import ru.yandex.practicum.filmorate.exception.NotFoundException;
    import ru.yandex.practicum.filmorate.model.film.Director;
    import ru.yandex.practicum.filmorate.model.film.Film;

    import java.util.Collections;
    import java.util.List;
    import java.util.Optional;

    @Repository
    public class FilmRepository extends BaseRepository<Film> {

        private final FilmDirectorsRepository filmDirectorsRepository;

        private static final String FIND_ALL_QUERY = "SELECT * FROM films";

        private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";

        private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa) " +
                "VALUES (?, ?, ?, ?, ?)";
        private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa = ? WHERE id = ?";

        private static final String FIND_POPULAR_QUERY = "SELECT f.* " + "FROM films f " + "LEFT JOIN film_likes fl ON f.id = fl.film_id " + "GROUP BY f.id " + "ORDER BY COUNT(fl.user_id) DESC " + "LIMIT ?";

        private static final String FIND_FILMS_BY_LIKES = "SELECT f.*, " +
                "(SELECT COUNT(*) FROM film_likes fl WHERE fl.film_id = f.id) AS like_count " +
                "FROM films f " +
                "JOIN film_directors fd ON f.id = fd.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY like_count DESC, f.release_date DESC";
        private static final String FIND_FILMS_BY_YEAR = "SELECT f.* " +
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

        public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, FilmDirectorsRepository filmDirectorsRepository) {
            super(jdbc, mapper);
            this.filmDirectorsRepository = filmDirectorsRepository;
        }

        public List<Film> findAll() {
            List<Film> films = findMany(FIND_ALL_QUERY);
            films.forEach(f -> f.setDirectors(filmDirectorsRepository.getDirectorsByFilm(f.getId())));
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
            return findMany(FIND_POPULAR_QUERY, count);
        }

        public List<Film> findFilmsByDirector(Long directorId, String sortBy) {
            if (sortBy.equals("likes")) {
                return findFilmsByLikes(directorId);
            } else if (sortBy.equals("year")) {
                return findFilmsByYear(directorId);
            }

            throw new IllegalArgumentException("Неподдерживаемый тип сортировки: " + sortBy + ". Допустимые значения: 'likes', 'year'");
        }

        private List<Film> findFilmsByLikes(Long directorId) {
            List<Film> films = findMany(FIND_FILMS_BY_LIKES, directorId);
            films.forEach(f -> f.setDirectors(filmDirectorsRepository.getDirectorsByFilm(f.getId())));
            return films;
        }

        private List<Film> findFilmsByYear(Long directorId) {
            List<Film> films = findMany(FIND_FILMS_BY_YEAR, directorId);
            films.forEach(f -> f.setDirectors(filmDirectorsRepository.getDirectorsByFilm(f.getId())));
            return films;
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
