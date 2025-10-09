package com.example.Nurse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.Nurse;

import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/nurse")
public class NurseController {

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
    
    @GetMapping("/index")
    public ResponseEntity<ArrayList<Nurse>> getAllNurses() {
        try {
            var resource = new ClassPathResource("static/nurses.json");
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(resource.getInputStream());
            JsonNode nursesNode = root.get("nurses");

            ArrayList<Nurse> lista = mapper.readValue(
                nursesNode.toString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Nurse.class)
            );

            return ResponseEntity.ok(lista);

        } catch (IOException e) {
        	System.out.println(e.getMessage());
        	return ResponseEntity.notFound().build();
        }
    }

}
