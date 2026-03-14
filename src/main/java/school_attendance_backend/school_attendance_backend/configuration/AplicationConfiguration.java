package school_attendance_backend.school_attendance_backend.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import school_attendance_backend.school_attendance_backend.repository.UserRepository;

/**
 * Classe de configuração responsável por definir as regras de segurança
 * da aplicação utilizando Spring Security.
 *
 * <p>Esta configuração define:</p>
 * <ul>
 *     <li>Pipeline de segurança da aplicação</li>
 *     <li>Estratégia de autenticação baseada em JWT</li>
 *     <li>Política de sessão stateless</li>
 *     <li>Endpoints públicos e protegidos</li>
 *     <li>Provider de autenticação</li>
 * </ul>
 *
 * <p>Arquitetura de autenticação utilizada:</p>
 * <ol>
 *     <li>Usuário realiza login ou registro</li>
 *     <li>O sistema gera um token JWT</li>
 *     <li>O cliente envia o token em cada requisição</li>
 *     <li>O filtro JWT valida o token</li>
 *     <li>Se válido, o usuário é autenticado no SecurityContext</li>
 * </ol>
 *
 * <p>A aplicação utiliza autenticação stateless, ou seja,
 * o servidor não mantém sessões de usuários.</p>
 *
 * @author Aníbal
 */
@Configuration
@EnableWebSecurity
public class AplicationConfiguration {

    /**
     * Repositório responsável por acessar os usuários no banco de dados.
     */
    @Autowired
    private UserRepository repository;

    /**
     * Filtro responsável por validar tokens JWT
     * antes da execução dos endpoints protegidos.
     */
    @Autowired
    private TokenJwtAuthenticationFilter filter;

    /**
     * Define a cadeia de filtros de segurança da aplicação.
     *
     * Estrutura da configuração:
     *
     * ETAPA 1 — Desativar CSRF
     * Como a aplicação utiliza JWT e não sessões,
     * o CSRF pode ser desativado.
     *
     * ETAPA 2 — Definir política de sessão
     * A aplicação é stateless, ou seja,
     * não mantém sessão no servidor.
     *
     * ETAPA 3 — Configurar autorização de rotas
     * Permite acesso público aos endpoints:
     * - POST /register
     * - POST /login
     *
     * Todas as outras rotas exigem autenticação.
     *
     * ETAPA 4 — Adicionar filtro JWT
     * O filtro de autenticação JWT é executado
     * antes do UsernamePasswordAuthenticationFilter.
     *
     * @param httpSecurity objeto de configuração de segurança HTTP
     * @return cadeia de filtros de segurança configurada
     * @throws Exception caso ocorra erro na configuração
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST, "/register").permitAll()
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .anyRequest().authenticated()
                )

                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class)

                .build();
    }

    /**
     * Define o provider responsável pela autenticação de usuários.
     *
     * Utiliza {@link DaoAuthenticationProvider}, que busca usuários
     * através do UserDetailsService e valida senhas com PasswordEncoder.
     *
     * @return AuthenticationProvider configurado
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider =
                new DaoAuthenticationProvider(userDetailsService());

        provider.setPasswordEncoder(passwordEncoder());

        return provider;
    }

    /**
     * Define o encoder utilizado para criptografar senhas.
     *
     * O BCrypt é considerado um padrão seguro
     * para armazenamento de senhas.
     *
     * @return instância de BCryptPasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Serviço responsável por carregar usuários durante o processo
     * de autenticação.
     *
     * O Spring Security utiliza esse serviço para localizar
     * usuários no banco de dados através do username.
     *
     * @return implementação de UserDetailsService
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> repository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    /**
     * Define o AuthenticationManager utilizado para processar
     * autenticações na aplicação.
     *
     * O AuthenticationManager delega o processo de autenticação
     * para os AuthenticationProviders configurados.
     *
     * @param configuration configuração de autenticação do Spring
     * @return AuthenticationManager configurado
     * @throws Exception caso ocorra erro na criação
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

}