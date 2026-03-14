package school_attendance_backend.school_attendance_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import school_attendance_backend.school_attendance_backend.dto.AuthDTO;
import school_attendance_backend.school_attendance_backend.entity.User;
import school_attendance_backend.school_attendance_backend.entity.UserRole;
import school_attendance_backend.school_attendance_backend.repository.UserRepository;
/**
 * Service responsável pelo processo de autenticação e registro de usuários.
 *
 * <p>Este serviço implementa as regras de negócio relacionadas ao cadastro
 * e login no sistema, utilizando autenticação baseada em JWT.</p>
 *
 * <p>Fluxos principais:</p>
 * <ul>
 *     <li><b>Registro:</b> cria um novo usuário no sistema, criptografa a senha
 *     utilizando {@link PasswordEncoder} e gera um token JWT.</li>
 *     <li><b>Login:</b> autentica o usuário através do {@link AuthenticationManager}
 *     e retorna um token JWT válido para acesso aos recursos protegidos.</li>
 * </ul>
 *
 * <p>Roles utilizadas no sistema:</p>
 * <ul>
 *     <li>ADMIN</li>
 *     <li>TEACHER</li>
 *     <li>STUDENT</li>
 * </ul>
 *
 * <p>No fluxo de registro padrão, novos usuários são criados com a role
 * <b>STUDENT</b>.</p>
 *
 * @author Anibal Júnior
 */
@Service
public class AuthService {

    /**
     * Repositório responsável pela persistência dos usuários.
     */
    @Autowired
    private UserRepository repository;

    /**
     * Serviço responsável pela geração e manipulação de tokens JWT.
     */
    @Autowired
    private TokenJwtService jwtService;

    /**
     * Encoder utilizado para criptografar senhas antes de serem armazenadas.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Gerenciador de autenticação do Spring Security responsável
     * por validar credenciais durante o login.
     */
    @Autowired
    private AuthenticationManager authenticationManager;


    /**
     * Realiza o registro de um novo usuário no sistema.
     *
     * <p>Fluxo do método:</p>
     * <ol>
     *     <li>Verifica se já existe um usuário com o mesmo username.</li>
     *     <li>Criptografa a senha utilizando {@link PasswordEncoder}.</li>
     *     <li>Cria um novo usuário com role padrão {@code STUDENT}.</li>
     *     <li>Salva o usuário no banco de dados.</li>
     *     <li>Gera e retorna um token JWT para autenticação.</li>
     * </ol>
     *
     * @param request objeto contendo username e password para cadastro
     * @return token JWT gerado para o usuário registrado
     * @throws RuntimeException caso o username já exista no sistema
     */
    public String register(AuthDTO request) {
        if (repository.findByUsername(request.username()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role(UserRole.STUDENT)
                .build();

        repository.save(user);

        return jwtService.generateToken(user);
    }


    /**
     * Realiza a autenticação de um usuário no sistema.
     *
     * <p>Fluxo do método:</p>
     * <ol>
     *     <li>Autentica as credenciais utilizando {@link AuthenticationManager}.</li>
     *     <li>Busca o usuário no banco de dados.</li>
     *     <li>Gera um token JWT válido.</li>
     *     <li>Retorna o token para o cliente.</li>
     * </ol>
     *
     * @param request objeto contendo username e password do usuário
     * @return token JWT gerado após autenticação bem-sucedida
     */
    public String login(AuthDTO request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = repository.findByUsername(request.username())
                .orElseThrow();

        return jwtService.generateToken(user);
    }
}