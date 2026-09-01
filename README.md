# Filmorate

## Схема базы данных
![Изображение.png](../../Library/Containers/com.apple.Preview/Data/tmp/VKTemp/C36CB48E-C287-494B-B9DC-4EE28749EC5C/%D0%98%D0%B7%D0%BE%D0%B1%D1%80%D0%B0%D0%B6%D0%B5%D0%BD%D0%B8%D0%B5.png)

Схема базы данных состоит из следующих таблиц:

* `users` — хранит информацию о пользователях;
* `friendships` — хранит связи дружбы между пользователями и их статус;
* `films` — хранит информацию о фильмах;
* `genres` — хранит список жанров;
* `film_genres` — связывает фильмы и жанры;
* `film_likes` — хранит информацию о лайках пользователей.

## Основные запросы

### Получение всех пользователей

```sql
SELECT *
FROM users;
```

### Получение всех фильмов

```sql
SELECT *
FROM films;
```

### Получение друзей пользователя

```sql
SELECT u.*
FROM users u
JOIN friendships f
    ON u.id = f.friend_id
WHERE f.user_id = :userId
  AND f.status = 'CONFIRMED';
```

### Добавление лайка фильму

```sql
INSERT INTO film_likes (film_id, user_id)
VALUES (:filmId, :userId);
```

### Удаление лайка

```sql
DELETE FROM film_likes
WHERE film_id = :filmId
  AND user_id = :userId;
```

### Получение популярных фильмов

```sql
SELECT f.*
FROM films f
LEFT JOIN film_likes fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY COUNT(fl.user_id) DESC
LIMIT :count;
```

### Получение общих друзей

```sql
SELECT u.*
FROM users u
JOIN friendships f1
    ON u.id = f1.friend_id
JOIN friendships f2
    ON u.id = f2.friend_id
WHERE f1.user_id = :userId
  AND f2.user_id = :otherUserId
  AND f1.status = 'CONFIRMED'
  AND f2.status = 'CONFIRMED';
```
