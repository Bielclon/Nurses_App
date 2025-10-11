package com.example.Nurse;

import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import model.Nurse;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RestController
public class NurseRepository {
    private List<Nurse> nurses = new ArrayList<>();

    public NurseRepository() {
        nurses.add(new Nurse("jdoe", "1234", "John Doe"));
        nurses.add(new Nurse("asmith", "abcd", "Anna Smith"));
        nurses.add(new Nurse("mperez", "qwerty", "Maria Perez"));
    }

    public Optional<Nurse> validateLogin(String username, String password) {
        return nurses.stream()
                     .filter(n -> n.getUsername().equals(username) && n.getPassword().equals(password))
                     .findFirst();
    }

    // New: find nurses by name (case-insensitive, substring match)
    public List<Nurse> findByName(String name) {
        if (name == null || name.isEmpty()) {
            return Collections.emptyList();
        }
        String lower = name.toLowerCase();
        return nurses.stream()
                     .filter(n -> n.getName() != null && n.getName().toLowerCase().contains(lower))
                     .collect(Collectors.toList());
    }
}