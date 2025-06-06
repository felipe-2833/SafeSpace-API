package br.com.fiap.safespace.config;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import br.com.fiap.safespace.model.Atendimento;
import br.com.fiap.safespace.model.AtendimentoType;
import br.com.fiap.safespace.model.Filiado;
import br.com.fiap.safespace.model.Ong;
import br.com.fiap.safespace.model.Pedido;
import br.com.fiap.safespace.model.PedidoType;
import br.com.fiap.safespace.model.Psicologo;
import br.com.fiap.safespace.model.Statustype;
import br.com.fiap.safespace.model.User;
import br.com.fiap.safespace.model.UserRole;
import br.com.fiap.safespace.model.Voluntario;
import br.com.fiap.safespace.repository.AtendimentoRepository;
import br.com.fiap.safespace.repository.FiliadoRepository;
import br.com.fiap.safespace.repository.OngRepository;
import br.com.fiap.safespace.repository.PedidoRepository;
import br.com.fiap.safespace.repository.PsicologoRepository;
import br.com.fiap.safespace.repository.UserRepository;
import br.com.fiap.safespace.repository.VoluntarioRepository;
import jakarta.annotation.PostConstruct;

@Component
public class DataBaseSeeder {
    @Autowired 
    private UserRepository userRepository;

    @Autowired 
    private OngRepository ongRepository;

    @Autowired 
    private PsicologoRepository psicologoRepository;

    @Autowired 
    private VoluntarioRepository voluntarioRepository;

    @Autowired 
    private FiliadoRepository filiadoRepository;

    @Autowired 
    private PedidoRepository pedidoRepository;
    
    @Autowired 
    private AtendimentoRepository atendimentoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void seed() {

        // USERS
        List<User> users = new ArrayList<>();
        users.add(User.builder().nome("Admin").email("admin@safespace.com").password(passwordEncoder.encode("12345")).endereco("Rua Central, 0").role(UserRole.ADMIN).build());
        users.add(User.builder().nome("user").email("user@safespace.com").password(passwordEncoder.encode("12345")).endereco("Rua Central, 1").role(UserRole.USER).build());
        users.add(User.builder().nome("Governador").email("gov@safespace.com").password(passwordEncoder.encode("12345")).endereco("Palácio, 1").role(UserRole.GOVERNO).build());

        for (int i = 1; i <= 10; i++) {
            users.add(User.builder()
                .nome("Usuário " + i)
                .email("user" + i + "@safespace.com")
                .password(passwordEncoder.encode("12345"))
                .endereco("Rua Exemplo, " + i)
                .role(UserRole.VITIMA)
                .build());
        }
        userRepository.saveAll(users);

        // ONGS
        List<Ong> ongs = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            ongs.add(Ong.builder()
                .nome("ONG " + i)
                .email("ong" + i + "@ong.com")
                .telefone("1190000000" + i)
                .endereco("Av. Solidariedade, " + i)
                .build());
        }
        ongRepository.saveAll(ongs);

        // PSICÓLOGOS
        List<Psicologo> psicologos = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            psicologos.add(Psicologo.builder()
                .nome("Psicologo " + i)
                .email("psico" + i + "@safespace.com")
                .password(passwordEncoder.encode("12345"))
                .endereco("Rua Psi, " + i)
                .crp("12345678")
                .area_atuacao("Ansiedade")
                .role(UserRole.PSICOLOGO)
                .build());
        }
        psicologoRepository.saveAll(psicologos);

        // VOLUNTÁRIOS
        List<Voluntario> voluntarios = new ArrayList<>();
        String[] areasVoluntario = {"Transporte", "Distribuição", "Alimentação", "Socorro"};
        String[] turnos = {"Manhã", "Tarde", "Noite"};
        for (int i = 1; i <= 10; i++) {
            voluntarios.add(Voluntario.builder()
                .nome("Voluntário " + i)
                .email("voluntario" + i + "@safespace.com")
                .password(passwordEncoder.encode("12345"))
                .endereco("Rua Voluntário, " + i)
                .disponibilidade(turnos[i % turnos.length])
                .area_atuacao(areasVoluntario[i % areasVoluntario.length])
                .role(UserRole.VOLUNTARIO)
                .build());
        }
        voluntarioRepository.saveAll(voluntarios);

        // FILIADOS
        List<Filiado> filiados = new ArrayList<>();
        String[] areasFiliado = {"Psicologia", "Medicina", "Tecnologia", "Logística"};
        for (int i = 0; i < 10; i++) {
            filiados.add(Filiado.builder()
                .nome("Filiado " + i)
                .email("filiado" + i + "@safespace.com")
                .password(passwordEncoder.encode("12345"))
                .endereco("Rua Filiado, " + i)
                .disponibilidade(turnos[i % turnos.length])
                .area_atuacao(areasFiliado[i % areasFiliado.length])
                .role(UserRole.FILIADO)
                .ong(ongs.get(i))
                .build());
        }
        filiadoRepository.saveAll(filiados);

        // PEDIDOS
        List<Pedido> pedidos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            pedidos.add(Pedido.builder()
                .descricao("Ajuda com abrigo para " + i + " pessoas.")
                .dataSolicitacao(LocalDate.now().minusDays(i))
                .status(Statustype.ATIVO)
                .user(users.get(i))
                .pedidoType(PedidoType.ABRIGO)
                .build());
        }
        pedidoRepository.saveAll(pedidos);

        // ATENDIMENTOS
        List<Atendimento> atendimentos = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            atendimentos.add(Atendimento.builder()
                .dataHora(LocalDateTime.now().minusHours(i * 5))
                .status(Statustype.ATIVO)
                .relatorio("Sessão de acolhimento " + i)
                .atendimentoType(AtendimentoType.PSICOLOGICO_EMERGENCIAL)
                .user(users.get(i))
                .psicologo(psicologos.get(i))
                .build());
        }
        atendimentoRepository.saveAll(atendimentos);
    }
}
