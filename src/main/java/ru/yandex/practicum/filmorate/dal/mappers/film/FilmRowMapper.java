package ru.yandex.practicum.filmorate.dal.mappers.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.film.Director;
import ru.yandex.practicum.filmorate.model.film.Film;
import ru.yandex.practicum.filmorate.model.film.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmRowMapper implements RowMapper<Film> {

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();

        film.setId(rs.getLong("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));

        String mpa = rs.getString("mpa");
        if (mpa != null) {
            film.setMpa(MPA.valueOf(mpa));
        }

        Long directorId = rs.getObject("director_id", Long.class);
        if (directorId != null) {
            Director d = new Director();
            d.setId(directorId);
            d.setName(rs.getString("director_name"));

            film.getDirectors().add(d);
        }

        return film;
    }
}