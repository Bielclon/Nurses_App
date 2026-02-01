package com.example.Nurse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.net.URI;
import entity.Nurse;
import repository.NurseRepository;

import java.util.*;

@RestController
@RequestMapping("/nurse")
public class NurseController {
	@Autowired
	private NurseRepository nurseRepository;

	// --- ENDPOINT LOGIN ---
		@PostMapping("/login")
		public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> credentials) {
			// La App Android envía "email" (o username) y "password"
			String emailOrUser = credentials.get("email"); 
			if (emailOrUser == null) emailOrUser = credentials.get("username"); // Por seguridad
			
			String password = credentials.get("password");

			Map<String, Object> response = new HashMap<>();

			// 1. Intentamos buscar por Username
			Optional<Nurse> nurseOptional = nurseRepository.findByUsername(emailOrUser);
			
			// 2. Si no está, intentamos buscar por Email
			if (nurseOptional.isEmpty()) {
				nurseOptional = nurseRepository.findByEmail(emailOrUser);
			}

			if (nurseOptional.isPresent()) {
				Nurse nurse = nurseOptional.get();
				// 3. Comprobar contraseña (en texto plano según tu código actual)
				if (password != null && password.equals(nurse.getPassword())) {
					// LOGIN ÉXITO
					response.put("success", true);
					response.put("id", nurse.getId());
					// Generamos un token simulado (en el futuro usar JWT real)
					response.put("token", "TOKEN_FALSO_" + nurse.getId()); 
					response.put("message", "Login correcto");
					response.put("user", nurse); // Devolvemos datos del usuario
					return ResponseEntity.ok(response);
				}
			}

			// LOGIN FALLIDO
			response.put("success", false);
			response.put("message", "Credenciales incorrectas");
			return ResponseEntity.status(401).body(response);
		}

		// --- ENDPOINT REGISTRO ---
		@PostMapping("/register") // Coincide con la llamada de Retrofit
		public ResponseEntity<?> register(@RequestBody Nurse nurse) {
			try {
				// Validaciones básicas
				if (nurseRepository.findByUsername(nurse.getUsername()).isPresent()) {
					return ResponseEntity.badRequest().body(Collections.singletonMap("message", "El usuario ya existe"));
				}
				if (nurseRepository.findByEmail(nurse.getEmail()).isPresent()) {
					return ResponseEntity.badRequest().body(Collections.singletonMap("message", "El email ya existe"));
				}

				// Guardar el enfermero
				Nurse nurseGuardada = nurseRepository.save(nurse);

				// Responder con formato de éxito + Token
				Map<String, Object> response = new HashMap<>();
				response.put("success", true);
				response.put("token", "TOKEN_NUEVO_" + nurseGuardada.getId());
				response.put("user", nurseGuardada);

				return ResponseEntity.ok(response);

			} catch (Exception e) {
				return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Error al registrar"));
			}
		}

	@GetMapping("/index")
	public ResponseEntity<List<Nurse>> getAllNurses() {
		try {
			return ResponseEntity.ok(nurseRepository.findAll());
		} catch (Exception e) {
			return ResponseEntity.notFound().build();
		}

	}

	// Find a nurse by username
	@GetMapping("/findByUser/{username}")
	public ResponseEntity<Nurse> findByUser(@PathVariable("username") String username) {
		try {
			return nurseRepository.findByUsername(username).map(nurse -> ResponseEntity.ok(nurse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception e) {
			return ResponseEntity.status(500).build();
		}
	}

	// Find nurses by a (partial) name match
	@GetMapping("/findByName")
	public ResponseEntity<List<Nurse>> findByName(@RequestParam("name") String name) {
		try {
			List<Nurse> nurses = nurseRepository.findByNameContainingIgnoreCase(name);
			return ResponseEntity.ok(nurses);
		} catch (Exception e) {
			return ResponseEntity.status(500).build();
		}
	}

	@PostMapping("/new")
    public ResponseEntity<Nurse> newNurses(@RequestBody Nurse nurse) {
        try {
            if (nurse.getId() != null) {
                return ResponseEntity.badRequest().build();
            }
            
            Nurse nurseGuardada = nurseRepository.save(nurse);

            URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(nurseGuardada.getId())
                    .toUri();
            return ResponseEntity.created(location).body(nurseGuardada);

        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

	@GetMapping("/{id}")
	public ResponseEntity<Nurse> getNurseById(@PathVariable Long id) {
		return nurseRepository.findById(id).map(nurse -> ResponseEntity.ok(nurse))
				.orElse(ResponseEntity.notFound().build());
	}

	@PutMapping("/{id}")
	public ResponseEntity<Nurse> updateNurse(@PathVariable Long id, @RequestBody Nurse nurseDetalles) {
		try {
			Optional<Nurse> optionalNurse = nurseRepository.findById(id);

			if (optionalNurse.isEmpty()) {
				return ResponseEntity.notFound().build();
			}

			Nurse nurseExistente = optionalNurse.get();
			nurseExistente.setName(nurseDetalles.getName());
			nurseExistente.setSurname(nurseDetalles.getSurname());
			nurseExistente.setUsername(nurseDetalles.getUsername());
			nurseExistente.setPassword(nurseDetalles.getPassword());
			nurseExistente.setEmail(nurseDetalles.getEmail());

			Nurse nurseActualizada = nurseRepository.save(nurseExistente);
			return ResponseEntity.ok(nurseActualizada);

		} catch (Exception e) {
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteNurse(@PathVariable Long id) {
		if (nurseRepository.existsById(id)) {
			nurseRepository.deleteById(id);
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}