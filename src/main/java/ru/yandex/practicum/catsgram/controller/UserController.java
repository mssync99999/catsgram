package ru.yandex.practicum.catsgram.controller;


import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;
import ru.yandex.practicum.catsgram.model.Post;
import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    /*    Long id; //— уникальный идентификатор пользователя,
    String username; //— имя пользователя,
    String email; //— электронная почта пользователя,
    String password; //— пароль пользователя,
    Instant registrationDate; //— дата и время регистрации.*/


    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        // проверяем выполнение необходимых условий
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        //Если вам нужно выполнить итерацию по данным for()
        //    метод keySet() — итерирует по всем ключам;
        //метод values() — итерирует по всем значениями;
        //метод entrySet() — итерирует по всем записям (ключ и значение)
        for (User entry : users.values()) {
            //System.out.println(entry);
            if (entry.getEmail().equals(user.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

        // формируем дополнительные данные
        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        // сохраняем новую публикацию в памяти приложения
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        // проверяем необходимые условия
        if (newUser.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        for (User entry : users.values()) {
            //System.out.println(entry);
            if (entry.getEmail().equals(newUser.getEmail())) {
                throw new DuplicatedDataException("Этот имейл уже используется");
            }
        }

        User oldUser = users.get(newUser.getId());

        if (newUser.getUsername() != null) {
            //обновляем содержимое
            oldUser.setUsername(newUser.getUsername());
            return oldUser;
        }

        if (newUser.getEmail() != null) {
            //обновляем содержимое
            oldUser.setEmail(newUser.getEmail());
            return oldUser;
        }

        if (newUser.getPassword() != null) {
            //обновляем содержимое
            oldUser.setPassword(newUser.getPassword());
            return oldUser;
        }

        return oldUser;
    }


    // вспомогательный метод для генерации идентификатора нового поста
    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);

        return ++currentMaxId;
    }

}
