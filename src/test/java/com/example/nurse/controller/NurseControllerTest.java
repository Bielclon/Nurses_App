package com.example.nurse.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Nurse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import repository.NurseRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(com.example.nurse.NurseController.class)
public class NurseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NurseRepository nurseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Nurse nurse1;
    private Nurse nurse2;

    @BeforeEach
    public void setUp() {
        nurse1 = new Nurse();
        nurse1.setId(1L);
        nurse1.setName("Joan");
        nurse1.setSurname("Perez");
        nurse1.setUsername("joan123");
        nurse1.setPassword("1234");
        nurse1.setEmail("joan@example.com");

        nurse2 = new Nurse();
        nurse2.setId(2L);
        nurse2.setName("Marc");
        nurse2.setSurname("Garcia");
        nurse2.setUsername("marc233");
        nurse2.setPassword("abcd");
        nurse2.setEmail("marc@example.com");
    }

    @Test
    public void postLogin_success_whenCredentialsMatch() throws Exception {
        given(nurseRepository.findByUsername("joan123")).willReturn(Optional.of(nurse1));

        String payload = objectMapper.writeValueAsString(
                java.util.Map.of("username", "joan123", "password", "1234")
        );

        mockMvc.perform(post("/nurse/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login correcto"))
                .andExpect(jsonPath("$.nurseName").value("Joan Perez"))
                .andExpect(jsonPath("$.nurseId").value(1));
    }

    @Test
    public void postLogin_failure_whenWrongPassword() throws Exception {
        given(nurseRepository.findByUsername("joan123")).willReturn(Optional.of(nurse1));

        String payload = objectMapper.writeValueAsString(
                java.util.Map.of("username", "joan123", "password", "wrong")
        );

        mockMvc.perform(post("/nurse/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    @Test
    public void postLogin_failure_whenUserNotFound() throws Exception {
        given(nurseRepository.findByUsername("unknown")).willReturn(Optional.empty());

        String payload = objectMapper.writeValueAsString(
                java.util.Map.of("username", "unknown", "password", "anything")
        );

        mockMvc.perform(post("/nurse/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos"));
    }

    @Test
    public void getIndex_returnsList() throws Exception {
        List<Nurse> list = Arrays.asList(nurse1, nurse2);
        given(nurseRepository.findAll()).willReturn(list);

        mockMvc.perform(get("/nurse/index"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].username").value("joan123"));
    }

    @Test
    public void findByUser_found() throws Exception {
        given(nurseRepository.findByUsername("joan123")).willReturn(Optional.of(nurse1));

        mockMvc.perform(get("/nurse/findByUser/joan123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("joan123"))
                .andExpect(jsonPath("$.name").value("Joan"));
    }

    @Test
    public void findByUser_notFound() throws Exception {
        given(nurseRepository.findByUsername("nope")).willReturn(Optional.empty());

        mockMvc.perform(get("/nurse/findByUser/nope"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void findByName_returnsMatches() throws Exception {
        given(nurseRepository.findByNameContainingIgnoreCase("ar")).willReturn(Arrays.asList(nurse2));

        mockMvc.perform(get("/nurse/findByName").param("name", "ar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].username").value("marc233"));
    }

    @Test
    public void postNew_success_createsNurse() throws Exception {
        Nurse toCreate = new Nurse();
        toCreate.setName("Pol");
        toCreate.setUsername("pol123");
        toCreate.setPassword("pw");

        given(nurseRepository.save(any(Nurse.class))).willAnswer(invocation -> {
            Nurse n = invocation.getArgument(0);
            n.setId(3L);
            return n;
        });

        mockMvc.perform(post("/nurse/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(toCreate)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/nurse/new/3")))
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.name").value("Pol"));
    }

    @Test
    public void postNew_badRequest_whenIdProvided() throws Exception {
        Nurse withId = new Nurse();
        withId.setId(99L);
        withId.setName("X");

        mockMvc.perform(post("/nurse/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(withId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getById_found() throws Exception {
        given(nurseRepository.findById(1L)).willReturn(Optional.of(nurse1));

        mockMvc.perform(get("/nurse/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("joan123"));
    }

    @Test
    public void getById_notFound() throws Exception {
        given(nurseRepository.findById(99L)).willReturn(Optional.empty());

        mockMvc.perform(get("/nurse/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void put_update_exists() throws Exception {
        Nurse update = new Nurse();
        update.setName("Joana");
        update.setSurname("Perez");
        update.setUsername("joan123");
        update.setPassword("newpw");
        update.setEmail("joana@example.com");

        given(nurseRepository.findById(1L)).willReturn(Optional.of(nurse1));
        given(nurseRepository.save(any(Nurse.class))).willAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(put("/nurse/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Joana"))
                .andExpect(jsonPath("$.password").value("newpw"));
    }

    @Test
    public void put_update_notFound() throws Exception {
        Nurse update = new Nurse();
        update.setName("Nobody");

        given(nurseRepository.findById(55L)).willReturn(Optional.empty());

        mockMvc.perform(put("/nurse/55")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void delete_exists_deletes() throws Exception {
        given(nurseRepository.existsById(1L)).willReturn(true);

        mockMvc.perform(delete("/nurse/1"))
                .andExpect(status().isOk());

        then(nurseRepository).should(times(1)).deleteById(1L);
    }

    @Test
    public void delete_notFound_returns404() throws Exception {
        given(nurseRepository.existsById(99L)).willReturn(false);

        mockMvc.perform(delete("/nurse/99"))
                .andExpect(status().isNotFound());
    }
}
