package br.pucminas.sistema_hospitalar.controller;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class QuartoInternacaoEmergenciaApiTest {

    private static final String QUARTO_101 =
            "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":1,\"tipo\":\"ENFERMARIA\"}";
    private static final String INTERNACAO_PACIENTE_1 =
            "{\"pacienteId\":1,\"profissionalId\":1,\"quartoId\":1,"
                    + "\"dataEntrada\":\"2026-11-01\",\"dataPrevistaAlta\":\"2026-11-05\"}";

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
        post("/pacientes", paciente("Maria", "52998224725")).andExpect(status().isCreated());
        post("/pacientes", paciente("Joao", "11144477735")).andExpect(status().isCreated());
    }

    private String paciente(String nome, String cpf) {
        return "{\"nome\":\"" + nome + "\",\"cpf\":\"" + cpf + "\",\"email\":\"p@email.com\"}";
    }

    private ResultActions post(String url, String corpo) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    private ResultActions put(String url, String corpo) throws Exception {
        return mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(corpo));
    }

    @Test
    void crudDeQuarto() throws Exception {
        post("/quartos", QUARTO_101)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ocupacaoAtual").value(0))
                .andExpect(jsonPath("$.situacao").value("DISPONIVEL"));

        put("/quartos/1", "{\"numeroIdentificacao\":101,\"andar\":2,\"capacidadeMaxima\":3,\"tipo\":\"UTI\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.andar").value(2))
                .andExpect(jsonPath("$.tipo").value("UTI"));

        mvc.perform(get("/quartos/1")).andExpect(status().isOk()).andExpect(jsonPath("$.capacidadeMaxima").value(3));
        mvc.perform(delete("/quartos/1")).andExpect(status().isNoContent());
        mvc.perform(get("/quartos/1")).andExpect(status().isNotFound());
    }

    @Test
    void clienteNaoDefineOcupacaoNemSituacaoDoQuarto() throws Exception {
        post("/quartos", "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":2,\"tipo\":\"UTI\","
                + "\"ocupacaoAtual\":2,\"situacao\":\"OCUPADO\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ocupacaoAtual").value(0))
                .andExpect(jsonPath("$.situacao").value("DISPONIVEL"));
    }

    @Test
    void quartoComDadosInvalidosRetorna400ComMensagem() throws Exception {
        post("/quartos", "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":0,\"tipo\":\"UTI\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(containsString("capacidade")));
    }

    @Test
    void tipoDeQuartoForaDoEnumRetorna400() throws Exception {
        post("/quartos", "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":1,\"tipo\":\"SUITE\"}")
                .andExpect(status().isBadRequest());
    }

    @Test
    void numeroDeQuartoRepetidoRetorna409() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/quartos", QUARTO_101).andExpect(status().isConflict());
    }

    @Test
    void listaSomenteQuartosDisponiveis() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/quartos", "{\"numeroIdentificacao\":102,\"andar\":1,\"capacidadeMaxima\":1,\"tipo\":\"UTI\"}")
                .andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        mvc.perform(get("/quartos/disponiveis"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].numeroIdentificacao").value(102));
    }

    @Test
    void internacaoOcupaEAltaLiberaOQuarto() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());

        post("/internacoes", INTERNACAO_PACIENTE_1)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativa").value(true))
                .andExpect(jsonPath("$.dataEntrada").value("2026-11-01"))
                .andExpect(jsonPath("$.paciente.nome").value("Maria"))
                .andExpect(jsonPath("$.profissional.nome").value("Ana"));
        mvc.perform(get("/quartos/1"))
                .andExpect(jsonPath("$.ocupacaoAtual").value(1))
                .andExpect(jsonPath("$.situacao").value("OCUPADO"));

        post("/internacoes/1/alta", "{\"data\":\"2026-11-04\",\"observacoes\":\"Sem intercorrências\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ativa").value(false))
                .andExpect(jsonPath("$.dataEfetivaAlta").value("2026-11-04"))
                .andExpect(jsonPath("$.observacoesAlta").value("Sem intercorrências"));
        mvc.perform(get("/quartos/1"))
                .andExpect(jsonPath("$.ocupacaoAtual").value(0))
                .andExpect(jsonPath("$.situacao").value("DISPONIVEL"));
    }

    @Test
    void altaEntraNoHistoricoDoPaciente() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        post("/internacoes/1/alta", "{\"data\":\"2026-11-04\"}").andExpect(status().isOk());

        mvc.perform(get("/pacientes/1"))
                .andExpect(jsonPath("$.historicoMedico", hasSize(1)))
                .andExpect(jsonPath("$.historicoMedico[0]").value(containsString("quarto 101")));
    }

    @Test
    void internacaoEmQuartoLotadoRetorna409() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        post("/internacoes", INTERNACAO_PACIENTE_1.replace("\"pacienteId\":1", "\"pacienteId\":2"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("capacidade máxima")));
    }

    @Test
    void pacienteComInternacaoAtivaNaoPodeSerInternadoNovamente() throws Exception {
        post("/quartos", "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":2,\"tipo\":\"UTI\"}")
                .andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        post("/internacoes", INTERNACAO_PACIENTE_1)
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("internação ativa")));
        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacaoAtual").value(1));
    }

    @Test
    void altaAnteriorAEntradaRetorna409() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        post("/internacoes/1/alta", "{\"data\":\"2026-10-31\"}").andExpect(status().isConflict());
        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacaoAtual").value(1));
    }

    @Test
    void internacaoComReferenciaInexistenteRetorna404() throws Exception {
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isNotFound());
    }

    @Test
    void atualizarInternacaoAlteraPrevisaoEObservacoes() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        put("/internacoes/1", "{\"dataPrevistaAlta\":\"2026-11-10\",\"observacoes\":\"Em observação\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dataPrevistaAlta").value("2026-11-10"))
                .andExpect(jsonPath("$.observacoes").value("Em observação"));
    }

    @Test
    void removerInternacaoAtivaLiberaOQuarto() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        mvc.perform(delete("/internacoes/1")).andExpect(status().isNoContent());

        mvc.perform(get("/internacoes/1")).andExpect(status().isNotFound());
        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacaoAtual").value(0));
    }

    @Test
    void removerInternacaoEncerradaNaoAlteraOcupacao() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());
        post("/internacoes/1/alta", "{\"data\":\"2026-11-04\"}").andExpect(status().isOk());
        post("/internacoes", INTERNACAO_PACIENTE_1.replace("\"pacienteId\":1", "\"pacienteId\":2"))
                .andExpect(status().isCreated());

        mvc.perform(delete("/internacoes/1")).andExpect(status().isNoContent());

        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacaoAtual").value(1));
    }

    @Test
    void naoRemoveQuartoComPacienteInternado() throws Exception {
        post("/quartos", QUARTO_101).andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        mvc.perform(delete("/quartos/1")).andExpect(status().isConflict());
    }

    @Test
    void edicaoDoQuartoMantemAOcupacaoVistaPelaInternacao() throws Exception {
        post("/quartos", "{\"numeroIdentificacao\":101,\"andar\":1,\"capacidadeMaxima\":2,\"tipo\":\"UTI\"}")
                .andExpect(status().isCreated());
        post("/internacoes", INTERNACAO_PACIENTE_1).andExpect(status().isCreated());

        put("/quartos/1", "{\"numeroIdentificacao\":101,\"andar\":3,\"capacidadeMaxima\":2,\"tipo\":\"UTI\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ocupacaoAtual").value(1));

        mvc.perform(get("/internacoes/1")).andExpect(jsonPath("$.quarto.andar").value(3));
        post("/internacoes/1/alta", "{\"data\":\"2026-11-04\"}").andExpect(status().isOk());
        mvc.perform(get("/quartos/1")).andExpect(jsonPath("$.ocupacaoAtual").value(0));
    }

    @Test
    void emergenciasAguardandoSaemOrdenadasPorPrioridade() throws Exception {
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Febre\",\"prioridade\":\"BAIXA\"}")
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.situacao").value("AGUARDANDO"));
        post("/consultas/emergencias", "{\"pacienteId\":2,\"motivo\":\"Infarto\",\"prioridade\":\"CRITICA\"}")
                .andExpect(status().isCreated());
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Fratura\",\"prioridade\":\"ALTA\"}")
                .andExpect(status().isCreated());

        mvc.perform(get("/consultas/emergencias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].prioridade").value("CRITICA"))
                .andExpect(jsonPath("$[1].prioridade").value("ALTA"))
                .andExpect(jsonPath("$[2].prioridade").value("BAIXA"));
    }

    @Test
    void fluxoDaEmergenciaEListagemPorSituacao() throws Exception {
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Febre\",\"prioridade\":\"MODERADA\"}")
                .andExpect(status().isCreated());

        post("/consultas/emergencias/1/iniciar", "{\"profissionalId\":1}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("INICIADO"))
                .andExpect(jsonPath("$.profissional.nome").value("Ana"));
        mvc.perform(get("/consultas/emergencias")).andExpect(jsonPath("$", hasSize(0)));
        mvc.perform(get("/consultas/emergencias").param("situacao", "INICIADO")).andExpect(jsonPath("$", hasSize(1)));

        post("/consultas/emergencias/1/concluir", "").andExpect(status().isOk())
                .andExpect(jsonPath("$.situacao").value("CONCLUIDO"));
        mvc.perform(get("/consultas/emergencias").param("situacao", "CONCLUIDO")).andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void emergenciaNaoConcluiSemIniciar() throws Exception {
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Febre\",\"prioridade\":\"BAIXA\"}")
                .andExpect(status().isCreated());

        post("/consultas/emergencias/1/concluir", "")
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value(containsString("iniciado")));
    }

    @Test
    void prioridadeInvalidaOuAusenteRetorna400() throws Exception {
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Febre\",\"prioridade\":\"URGENTE\"}")
                .andExpect(status().isBadRequest());
        post("/consultas/emergencias", "{\"pacienteId\":1,\"motivo\":\"Febre\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(containsString("prioridade")));
    }

    @Test
    void situacaoInvalidaNaListagemRetorna400() throws Exception {
        mvc.perform(get("/consultas/emergencias").param("situacao", "XYZ")).andExpect(status().isBadRequest());
    }
}
