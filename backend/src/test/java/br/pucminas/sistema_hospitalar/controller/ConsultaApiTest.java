package br.pucminas.sistema_hospitalar.controller;

import br.pucminas.sistema_hospitalar.model.EspecialistaDeTeste;
import br.pucminas.sistema_hospitalar.model.ProfissionalSaude;
import br.pucminas.sistema_hospitalar.repository.ProfissionalSaudeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ConsultaApiTest {

    // sempre no futuro, senão o agendamento é recusado
    private static final String DIA = LocalDate.now().plusDays(7).toString();
    private static final String OUTRO_DIA = LocalDate.now().plusDays(8).toString();

    @Autowired
    private WebApplicationContext contexto;

    @Autowired
    private ProfissionalSaudeRepository profissionalRepository;

    private MockMvc mvc;

    @BeforeEach
    void preparar() throws Exception {
        mvc = MockMvcBuilders.webAppContextSetup(contexto).build();
        profissionalRepository.salvar(new ProfissionalSaude(null, "Ana", "CRM-1", "3199999", "ana@email.com") {
        });
        profissionalRepository.salvar(new EspecialistaDeTeste(null, "Bruno", "Cardiologia"));
        post("/pacientes", "{\"nome\":\"Maria\",\"cpf\":\"52998224725\",\"email\":\"maria@email.com\"}")
                .andExpect(status().isCreated());
    }

    private String rotina(long profissionalId, String data, String horario) {
        return "{\"tipo\":\"ROTINA\",\"pacienteId\":1,\"profissionalId\":" + profissionalId
                + ",\"data\":\"" + data + "\",\"horario\":\"" + horario + "\",\"motivo\":\"Check-up\"}";
    }

    private String especializada(long profissionalId, String especialidade) {
        return "{\"tipo\":\"ESPECIALIZADA\",\"pacienteId\":1,\"profissionalId\":" + profissionalId
                + ",\"data\":\"" + DIA + "\",\"horario\":\"10:00\",\"motivo\":\"Dor no peito\""
                + ",\"especialidade\":\"" + especialidade + "\"}";
    }

    private ResultActions post(String url, String corpo) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    private ResultActions post(String url) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.post(url));
    }

    private ResultActions put(String url, String corpo) throws Exception {
        return mvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    @Test
    void agendaConsultaDeRotina() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.tipo").value("ROTINA"))
                .andExpect(jsonPath("$.situacao").value("AGENDADA"))
                .andExpect(jsonPath("$.paciente.nome").value("Maria"))
                .andExpect(jsonPath("$.profissional.nome").value("Ana"));

        mvc.perform(get("/consultas/1")).andExpect(status().isOk()).andExpect(jsonPath("$.motivo").value("Check-up"));
        mvc.perform(get("/consultas")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void fluxoCompletoRegistraNoHistoricoDoPaciente() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00")).andExpect(status().isCreated());

        post("/consultas/1/confirmar").andExpect(jsonPath("$.situacao").value("CONFIRMADA"));
        post("/consultas/1/iniciar").andExpect(jsonPath("$.situacao").value("EM_ATENDIMENTO"));
        post("/consultas/1/concluir", "{\"observacoes\":\"Pressão normal\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("CONCLUIDA"))
                .andExpect(jsonPath("$.observacoes").value("Pressão normal"));

        mvc.perform(get("/pacientes/1"))
                .andExpect(jsonPath("$.historicoMedico", hasSize(1)))
                .andExpect(jsonPath("$.historicoMedico[0]", containsString("Pressão normal")));
    }

    @Test
    void concluirSemBodyFunciona() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/confirmar");
        post("/consultas/1/iniciar");
        post("/consultas/1/concluir").andExpect(status().isOk()).andExpect(jsonPath("$.situacao").value("CONCLUIDA"));
    }

    @Test
    void transicaoInvalidaVolta400() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));

        post("/consultas/1/concluir")
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("AGENDADA para CONCLUIDA")));
    }

    @Test
    void concluidaNaoVoltaParaAgendada() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/confirmar");
        post("/consultas/1/iniciar");
        post("/consultas/1/concluir");

        post("/consultas/1/reagendar", "{\"data\":\"" + OUTRO_DIA + "\",\"horario\":\"09:00\"}")
                .andExpect(status().isBadRequest());
        mvc.perform(get("/consultas/1")).andExpect(jsonPath("$.situacao").value("CONCLUIDA"));
    }

    @Test
    void canceladaPrecisaDeNovoAgendamentoParaConcluir() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/cancelar").andExpect(jsonPath("$.situacao").value("CANCELADA"));
        post("/consultas/1/concluir").andExpect(status().isBadRequest());

        post("/consultas/1/reagendar", "{\"data\":\"" + OUTRO_DIA + "\",\"horario\":\"09:00\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("AGENDADA"))
                .andExpect(jsonPath("$.data").value(OUTRO_DIA));
    }

    @Test
    void naoCompareceu() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/nao-compareceu").andExpect(jsonPath("$.situacao").value("NAO_COMPARECEU"));
    }

    @Test
    void profissionalNaoTemDuasConsultasNoMesmoHorario() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00")).andExpect(status().isCreated());

        post("/consultas", rotina(1, DIA, "14:00"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("já possui um atendimento")));

        post("/consultas", rotina(1, DIA, "15:00")).andExpect(status().isCreated());
    }

    @Test
    void cancelarLiberaOHorario() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/cancelar");

        post("/consultas", rotina(1, DIA, "14:00")).andExpect(status().isCreated());
    }

    @Test
    void naoAgendaNoPassado() throws Exception {
        post("/consultas", rotina(1, LocalDate.now().minusDays(1).toString(), "14:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void consultaEspecializadaValidaAEspecialidade() throws Exception {
        post("/consultas", especializada(2, "Cardiologia"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipo").value("ESPECIALIZADA"))
                .andExpect(jsonPath("$.especialidade").value("Cardiologia"));

        post("/consultas", especializada(2, "Neurologia"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("não atende a especialidade")));

        post("/consultas", especializada(1, "Cardiologia")).andExpect(status().isBadRequest());
    }

    @Test
    void atualizaConsultaAgendada() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));

        put("/consultas/1", "{\"profissionalId\":1,\"data\":\"" + OUTRO_DIA + "\",\"horario\":\"16:00\",\"motivo\":\"Retorno\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.motivo").value("Retorno"))
                .andExpect(jsonPath("$.data").value(OUTRO_DIA));
    }

    @Test
    void naoRemoveConsultaConcluida() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas/1/confirmar");
        post("/consultas/1/iniciar");
        post("/consultas/1/concluir");

        mvc.perform(delete("/consultas/1")).andExpect(status().isBadRequest());

        post("/consultas", rotina(1, DIA, "15:00"));
        mvc.perform(delete("/consultas/2")).andExpect(status().isNoContent());
        mvc.perform(get("/consultas/2")).andExpect(status().isNotFound());
    }

    @Test
    void filtraPorSituacaoEPaciente() throws Exception {
        post("/consultas", rotina(1, DIA, "14:00"));
        post("/consultas", rotina(1, DIA, "15:00"));
        post("/consultas/2/cancelar");

        mvc.perform(get("/consultas").param("situacao", "AGENDADA")).andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/consultas").param("pacienteId", "1")).andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/consultas").param("situacao", "QUALQUER")).andExpect(status().isBadRequest());
    }

    @Test
    void erros404E400() throws Exception {
        mvc.perform(get("/consultas/99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Consulta não encontrada"));

        post("/consultas", "{\"tipo\":\"ROTINA\",\"pacienteId\":1,\"profissionalId\":1}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.motivo").exists())
                .andExpect(jsonPath("$.data").exists());

        post("/consultas", rotina(1, DIA, "14:00").replace("ROTINA", "CIRURGIA")).andExpect(status().isBadRequest());
        post("/consultas", rotina(99, DIA, "14:00")).andExpect(status().isNotFound());
    }

    @Test
    void emergenciasContinuamNaRotaPropria() throws Exception {
        mvc.perform(get("/consultas/emergencias")).andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(0)));
    }
}
