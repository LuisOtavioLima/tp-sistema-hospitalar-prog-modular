package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.model.Paciente;
import br.pucminas.sistema_hospitalar.service.PacienteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PacienteController.class)
public class PacienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PacienteService service;

    @Test
    public void deveCriarPacienteComSucesso() throws Exception {
        // CPF válido gerado matematicamente apenas para o teste passar
        Paciente paciente = new Paciente(null, "Luís Lima", "52998224025", "luis@email.com", "31999999999", LocalDate.of(2000, 1, 1));
        
        Mockito.when(service.salvar(Mockito.any(Paciente.class))).thenReturn(paciente);

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isCreated());
    }

    @Test
    public void deveRetornarErro400QuandoCpfInvalido() throws Exception {
        // CPF com formato errado para forçar o erro no Hibernate Validator
        Paciente paciente = new Paciente(null, "Luís Lima", "12345678900", "luis@email.com", "31999999999", LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.cpf").exists()); // Verifica se o erro aponta especificamente para o CPF
    }

    @Test
    public void deveRetornarErro400QuandoEmailInvalido() throws Exception {
        // E-mail sem o '@' para forçar a falha da anotação @Email
        Paciente paciente = new Paciente(null, "Luís Lima", "52998224025", "luis.email.com", "31999999999", LocalDate.of(2000, 1, 1));

        mockMvc.perform(post("/pacientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paciente)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").exists()); // Verifica se o erro aponta especificamente para o e-mail
    }
}
