package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.MpaResponse;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<MpaResponse> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{id}")
    public MpaResponse findById(@PathVariable int id) {
        return mpaService.findById(id);
    }
}