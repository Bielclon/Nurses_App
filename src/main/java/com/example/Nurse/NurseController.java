package com.example.Nurse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/nurse")
public class NurseController {

    @Autowired
    private NurseRepository nurseRepository;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");

        Map<String, Object> response = new HashMap<>();

        if ("jdoe".equals(username) && "1234".equals(password)) {
            response.put("success", true);
            response.put("message", "Login correcto");
        } else {
            response.put("success", false);
            response.put("message", "Usuario o contraseña incorrectos");
        }
        return response;
    }

    // RodrigoMD : search nurses by name (case-insensitive, substring match)
    @GetMapping("/search")
    public List<Map<String, String>> findByName(@RequestParam(name = "name") String name) {
        List<Nurse> found = nurseRepository.findByName(name);
        return found.stream()
                    .map(n -> {
                        Map<String, String> m = new HashMap<>();
                        m.put("username", n.getUsername());
                        m.put("name", n.getName());
                        return m;
                    })
                    .collect(Collectors.toList());
    }

}