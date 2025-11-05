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

	@PostMapping("/login")
	public Map<String, Object> login(@RequestBody Map<String, String> credentials) {
		String username = credentials.get("username");
		String password = credentials.get("password"); // Contraseña en texto plano

		Map<String, Object> response = new HashMap<>();

		// 2. Buscar al 'Nurse' en la base de datos por su username
		// (Asegúrate de tener el método 'findByUsername' en tu NurseRepository)
		Optional<Nurse> nurseOptional = nurseRepository.findByUsername(username);

		// 3. Comprobar si el 'Nurse' existe
		if (nurseOptional.isPresent()) {

			Nurse nurse = nurseOptional.get(); // Obtiene el objeto Nurse de la BD

			// 4. Comparar la contraseña en TEXTO PLANO
			if (password.equals(nurse.getPassword())) {
				// Éxito: El usuario existe y la contraseña coincide
				response.put("success", true);
				response.put("message", "Login correcto");
				// Opcional: puedes añadir más datos del usuario a la respuesta
				response.put("nurseName", nurse.getName() + " " + nurse.getSurname());
				response.put("nurseId", nurse.getId());

			} else {
				// Error: La contraseña no coincide
				response.put("success", false);
				response.put("message", "Usuario o contraseña incorrectos");
			}

		} else {
			// Error: El usuario no fue encontrado
			response.put("success", false);
			response.put("message", "Usuario o contraseña incorrectos");
		}

		return response;
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