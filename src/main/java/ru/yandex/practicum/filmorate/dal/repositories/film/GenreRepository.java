package ru.yandex.practicum.filmorate.dal.repositories.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.repositories.BaseRepository;
import ru.yandex.practicum.filmorate.model.film.Genre;

import java.util.*;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM genres";

    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";

    private static final String FIND_EXISTING_IDS_QUERY = "SELECT id FROM genres WHERE id IN (%s)";


    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    public List<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Optional<Genre> findById(long genreId) {
        return findOne(FIND_BY_ID_QUERY, genreId);
    }

    public Set<Long> findExistingIds(Set<Long> ids) {
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));

        String query = FIND_EXISTING_IDS_QUERY.formatted(placeholders);

        return new HashSet<>(jdbc.queryForList(query, Long.class, ids.toArray()));
    }
}