package br.com.clinicaveterinaria.config;

import br.com.clinicaveterinaria.enums.*;
import br.com.clinicaveterinaria.model.*;
import br.com.clinicaveterinaria.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carregador de dados iniciais para demonstração e inicialização do sistema.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final FuncionarioRepository funcionarioRepository;
    private final TutorRepository tutorRepository;
    private final PetRepository petRepository;
    private final AgendamentoRepository agendamentoRepository;
    private final ProntuarioClinicoRepository prontuarioRepository;
    private final GuiaManejoSilvestreRepository guiaRepository;
    private final VacinaTratamentoRepository vacinaRepository;

    public DataInitializer(FuncionarioRepository funcionarioRepository,
                           TutorRepository tutorRepository,
                           PetRepository petRepository,
                           AgendamentoRepository agendamentoRepository,
                           ProntuarioClinicoRepository prontuarioRepository,
                           GuiaManejoSilvestreRepository guiaRepository,
                           VacinaTratamentoRepository vacinaRepository) {
        this.funcionarioRepository = funcionarioRepository;
        this.tutorRepository = tutorRepository;
        this.petRepository = petRepository;
        this.agendamentoRepository = agendamentoRepository;
        this.prontuarioRepository = prontuarioRepository;
        this.guiaRepository = guiaRepository;
        this.vacinaRepository = vacinaRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (funcionarioRepository.count() > 0) {
            log.info("Base de dados já inicializada com registros prévios.");
            return;
        }

        log.info("Populando base de dados com massa de testes para a Clínica Veterinária...");

        // 1. Funcionários
        Funcionario drCarlos = Funcionario.builder()
                .nome("Dr. Carlos Eduardo")
                .cpf("111.222.333-44")
                .email("carlos.vet@vetcare.com")
                .telefone("(11) 98888-1111")
                .cargo(PerfilFuncionario.VETERINARIO)
                .crmv("CRMV/SP 12345")
                .ativo(true)
                .build();
        funcionarioRepository.save(drCarlos);

        Funcionario lucasMendes = Funcionario.builder()
                .nome("Lucas Mendes")
                .cpf("555.666.777-88")
                .email("lucas.adestrador@vetcare.com")
                .telefone("(11) 97777-2222")
                .cargo(PerfilFuncionario.ESPECIALISTA_SILVESTRES)
                .crmv(null)
                .ativo(true)
                .build();
        funcionarioRepository.save(lucasMendes);

        Funcionario anaSilva = Funcionario.builder()
                .nome("Ana Silva")
                .cpf("999.888.777-66")
                .email("ana.recepcao@vetcare.com")
                .telefone("(11) 96666-3333")
                .cargo(PerfilFuncionario.ATENDENTE)
                .crmv(null)
                .ativo(true)
                .build();
        funcionarioRepository.save(anaSilva);

        Funcionario marianaAdmin = Funcionario.builder()
                .nome("Mariana Oliveira")
                .cpf("444.333.222-11")
                .email("mariana.admin@vetcare.com")
                .telefone("(11) 95555-4444")
                .cargo(PerfilFuncionario.ADMINISTRADOR)
                .crmv(null)
                .ativo(true)
                .build();
        funcionarioRepository.save(marianaAdmin);

        // 2. Tutores e Pets
        Tutor tutor1 = Tutor.builder()
                .nome("Mariana Oliveira Santos")
                .cpf("123.456.789-00")
                .telefone("(11) 98765-4321")
                .endereco("Rua das Flores, 120 - Jardins, SP")
                .build();
        tutorRepository.save(tutor1);

        Pet kiko = Pet.builder()
                .nome("Kiko")
                .categoria(CategoriaPet.SILVESTRE)
                .especieRaca("Papagaio Verdadeiro (Amazona aestiva)")
                .sexo(SexoPet.MACHO)
                .idade("4 anos")
                .peso("420g")
                .habitatObservacoes("Viveiro interno com luz natural e enriquecimento para forrageamento.")
                .tutor(tutor1)
                .build();
        petRepository.save(kiko);

        Pet nina = Pet.builder()
                .nome("Nina")
                .categoria(CategoriaPet.DOMESTICO)
                .especieRaca("Gato Siamês")
                .sexo(SexoPet.FEMEA)
                .idade("2 anos")
                .peso("3.8kg")
                .habitatObservacoes("Apartamento com enriquecimento vertical e arranhadores.")
                .tutor(tutor1)
                .build();
        petRepository.save(nina);

        Tutor tutor2 = Tutor.builder()
                .nome("Roberto Almeida")
                .cpf("987.654.321-11")
                .telefone("(11) 91234-5678")
                .endereco("Av. Paulista, 900 - Bela Vista, SP")
                .build();
        tutorRepository.save(tutor2);

        Pet tufao = Pet.builder()
                .nome("Tufão")
                .categoria(CategoriaPet.SILVESTRE)
                .especieRaca("Jabuti Piranga (Chelonoidis carbonarius)")
                .sexo(SexoPet.MACHO)
                .idade("6 anos")
                .peso("2.4kg")
                .habitatObservacoes("Recinto externo de 4m² com lâmpada UVB 5.0 e aquecimento focal.")
                .tutor(tutor2)
                .build();
        petRepository.save(tufao);

        Tutor tutor3 = Tutor.builder()
                .nome("Fernanda Costa")
                .cpf("456.789.123-22")
                .telefone("(11) 99887-7665")
                .endereco("Rua Augusta, 45 - Consolação, SP")
                .build();
        tutorRepository.save(tutor3);

        Pet thor = Pet.builder()
                .nome("Thor")
                .categoria(CategoriaPet.DOMESTICO)
                .especieRaca("Cão - Golden Retriever")
                .sexo(SexoPet.MACHO)
                .idade("3 anos")
                .peso("28kg")
                .habitatObservacoes("Casa com quintal e passeios diários.")
                .tutor(tutor3)
                .build();
        petRepository.save(thor);

        Tutor tutor4 = Tutor.builder()
                .nome("Bruno Albuquerque")
                .cpf("321.654.987-33")
                .telefone("(11) 97123-4567")
                .endereco("Rua Pamplona, 500 - Jardim Paulista, SP")
                .build();
        tutorRepository.save(tutor4);

        Pet pipoca = Pet.builder()
                .nome("Pipoca")
                .categoria(CategoriaPet.EXOTICO)
                .especieRaca("Ferret / Furão (Mustela putorius furo)")
                .sexo(SexoPet.FEMEA)
                .idade("1 ano")
                .peso("850g")
                .habitatObservacoes("Gaiola de múltiplos andares com redes e tubos de estimulação.")
                .tutor(tutor4)
                .build();
        petRepository.save(pipoca);

        // 3. Agendamentos do Dia
        LocalDate hoje = LocalDate.now();

        Agendamento ag1 = Agendamento.builder()
                .data(hoje)
                .hora("09:00")
                .pet(kiko)
                .tutor(tutor1)
                .tipoServico(TipoServico.CONSULTORIA_MANEJO)
                .profissional(lucasMendes)
                .status(StatusAgendamento.EM_ATENDIMENTO)
                .observacoes("Queixa de vocalização excessiva matinal.")
                .build();
        agendamentoRepository.save(ag1);

        Agendamento ag2 = Agendamento.builder()
                .data(hoje)
                .hora("10:30")
                .pet(tufao)
                .tutor(tutor2)
                .tipoServico(TipoServico.CONSULTA_CLINICA)
                .profissional(drCarlos)
                .status(StatusAgendamento.AGUARDANDO)
                .observacoes("Avaliação clínica de carapaça e suplementação vitamínica.")
                .build();
        agendamentoRepository.save(ag2);

        Agendamento ag3 = Agendamento.builder()
                .data(hoje)
                .hora("14:00")
                .pet(thor)
                .tutor(tutor3)
                .tipoServico(TipoServico.CONSULTA_CLINICA)
                .profissional(drCarlos)
                .status(StatusAgendamento.AGENDADO)
                .observacoes("Check-up clínico e reforço vacinal V10.")
                .build();
        agendamentoRepository.save(ag3);

        Agendamento ag4 = Agendamento.builder()
                .data(hoje)
                .hora("15:30")
                .pet(pipoca)
                .tutor(tutor4)
                .tipoServico(TipoServico.CHECKUP_NUTRICIONAL)
                .profissional(lucasMendes)
                .status(StatusAgendamento.AGENDADO)
                .observacoes("Adequação nutricional e prevenção de bolas de pelo.")
                .build();
        agendamentoRepository.save(ag4);

        // 4. Prontuários Médicos Prévios
        ProntuarioClinico prontuarioTufao = ProntuarioClinico.builder()
                .pet(tufao)
                .veterinario(drCarlos)
                .dataAtendimento(LocalDateTime.now().minusDays(30))
                .queixaPrincipal("Check-up clínico de rotina e integridade da carapaça.")
                .sinaisVitais("FC: 45 bpm | T: 28°C (Temperatura corporal de quelônio)")
                .mucosas("Mucosa oral rósea, hidratado, carapaça firme.")
                .exameFisico("Ausculta sem ruídos respiratórios. Sem sinais de piramidismo ou osteodistrofia.")
                .hipoteseDiagnostica("Animal hígido, discreta deficiência de radiação UVB.")
                .condutaPrescricao("Prescrito Carbonato de Cálcio com Vitamina D3 oral (1 pitada 2x/semana) e banho de sol direto diário.")
                .receituarioEmitido("Carbonato de Cálcio + Vitamina D3 pó: Polvilhar 1 pitada sobre o alimento 2 vezes por semana, durante 60 dias.\nBanho de sol: 30 minutos diários em horário brando (antes das 10h).")
                .build();
        prontuarioRepository.save(prontuarioTufao);

        ProntuarioClinico prontuarioThor = ProntuarioClinico.builder()
                .pet(thor)
                .veterinario(drCarlos)
                .dataAtendimento(LocalDateTime.now().minusDays(15))
                .queixaPrincipal("Avaliação clínica geral pré-vacinal e controle parasitário.")
                .sinaisVitais("T: 38.6°C | FC: 110 bpm | FR: 24 mpm")
                .mucosas("Normocoradas, TPC 1.5s")
                .exameFisico("Linfonodos normais, ausculta cardiopulmonar límpida.")
                .hipoteseDiagnostica("Animal saudável e apto para protocolo vacinal anual.")
                .condutaPrescricao("Aplicação de Vacina V10 Polivalente Canina e Antirrábica. Prescrito vermífugo palatável.")
                .receituarioEmitido("Milbemicina Oxima + Praziquantel comprimidos palatáveis: Administrar 1 comprimido por via oral a cada 3 meses para prevenção de verminoses e dirofilariose.")
                .build();
        prontuarioRepository.save(prontuarioThor);

        // 5. Guias de Manejo Prévios
        GuiaManejoSilvestre guiaKiko = GuiaManejoSilvestre.builder()
                .pet(kiko)
                .especialista(lucasMendes)
                .dataAtendimento(LocalDateTime.now().minusDays(20))
                .queixaComportamental("Vocalização excessiva ao amanhecer e arranque de penugem peitoral por tédio.")
                .dimensoesRecinto("Viveiro recomendado de 2.0m x 1.5m x 2.0m com múltiplos poleiros de diâmetros variáveis.")
                .parametrosIluminacao("Lâmpada UVB 5.0 (10h diárias com timer) e 12h de repouso em ambiente escuro e silencioso.")
                .parametrosTemperatura("Temperatura ambiente ideal entre 24°C e 28°C sem correntes de ar diretas.")
                .tipoSubstrato("Galhos naturais higienizados de goiabeira/jabuticabeira e casca de pinus atóxica.")
                .enriquecimentoAmbiental("Forrageamento diário: quebra-cabeças com pinhas, caixas de papelão e rolos de papel contendo castanhas e sementes.")
                .planoTreinamentoAdestramento("Treinamento de alvo (target stick) e reforço positivo para pousar no braço voluntariamente sem bicar.")
                .build();
        guiaRepository.save(guiaKiko);

        // 6. Vacinas e Tratamentos
        VacinaTratamento v1 = VacinaTratamento.builder()
                .pet(thor)
                .nome("Vacina V10 Polivalente Canina")
                .dataAplicacao(LocalDate.now().minusDays(15))
                .dataProximaDose(LocalDate.now().plusMonths(12).minusDays(15))
                .lote("V10-BR-9982")
                .veterinarioResponsavel("Dr. Carlos Eduardo (CRMV/SP 12345)")
                .observacoes("Animal tolerou bem sem reações alérgicas imediatas.")
                .build();
        vacinaRepository.save(v1);

        VacinaTratamento v2 = VacinaTratamento.builder()
                .pet(thor)
                .nome("Vacina Antirrábica")
                .dataAplicacao(LocalDate.now().minusDays(15))
                .dataProximaDose(LocalDate.now().plusMonths(12).minusDays(15))
                .lote("RAB-2026-041")
                .veterinarioResponsavel("Dr. Carlos Eduardo (CRMV/SP 12345)")
                .observacoes("Dose anual obrigatória.")
                .build();
        vacinaRepository.save(v2);

        VacinaTratamento v3 = VacinaTratamento.builder()
                .pet(nina)
                .nome("Vacina Quádrupla Felina (V4)")
                .dataAplicacao(LocalDate.now().minusMonths(3))
                .dataProximaDose(LocalDate.now().plusMonths(9))
                .lote("FEL-V4-8831")
                .veterinarioResponsavel("Dr. Carlos Eduardo (CRMV/SP 12345)")
                .observacoes("Proteção contra panleucopenia, calicivirose, rinotraqueíte e clamidiose.")
                .build();
        vacinaRepository.save(v3);

        log.info("Base de dados da Clínica Veterinária inicializada com sucesso!");
    }
}
