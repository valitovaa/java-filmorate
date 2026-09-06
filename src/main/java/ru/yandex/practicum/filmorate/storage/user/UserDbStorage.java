package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.dal.repositories.user.FriendshipRepository;
import ru.yandex.practicum.dal.repositories.user.UserRepository;
import ru.yandex.practicum.filmorate.model.user.User;

import java.util.List;
import java.util.Optional;

@Component("userDbStorage")
public class UserDbStorage implements UserStorage {

    private final UserRepository userRepository;
    private final FriendshipRepository friendshipRepository;

    public UserDbStorage(
            UserRepository userRepository,
            FriendshipRepository friendshipRepository
    ) {
        this.userRepository = userRepository;
        this.friendshipRepository = friendshipRepository;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User addUser(User user) {
        return userRepository.addUser(user);
    }

    @Override
    public User update(User user) {
        return userRepository.update(user);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        friendshipRepository.addFriend(userId, friendId);
    }


    @Override
    public void removeFriend(Long userId, Long friendId) {
        friendshipRepository.removeFriend(userId, friendId);
    }

    @Override
    public List<User> getFriends(Long userId) {
        return friendshipRepository.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(Long userId, Long otherUserId) {
        return friendshipRepository.getCommonFriends(userId, otherUserId);
    }
}