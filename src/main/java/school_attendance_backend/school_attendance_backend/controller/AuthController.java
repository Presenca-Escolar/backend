package school_attendance_backend.school_attendance_backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import school_attendance_backend.school_attendance_backend.dto.AuthDTO;
import school_attendance_backend.school_attendance_backend.service.AuthService;


/**
 * Controller responsável por lidar com autenticação dos usuários.
 *
 * Aqui ficam os endpoints de:
 * - Registro de usuário (/register)
 * - Login de usuário (/login)
 *
 * Esse controller recebe as requisições HTTP e delega a lógica
 * para o AuthService, que é responsável por validar os dados,
 * salvar o usuário e gerar o JWT.
 */
@RestController
@Slf4j
public class AuthController {

    /**
     * Service responsável pela lógica de autenticação.
     *
     * Ele contém os métodos que:
     * - registram usuários
     * - validam login
     * - geram o token JWT
     */
    @Autowired
    private AuthService authService;


    /**
     * Endpoint responsável pelo REGISTRO de novos usuários.
     *
     * URL:
     * POST /register
     *
     * O corpo da requisição deve conter um AuthDTO com:
     * - username
     * - password
     *
     * Fluxo:
     * 1. Recebe os dados do usuário
     * 2. Chama o service para registrar o usuário
     * 3. O service retorna um JWT
     * 4. O JWT é enviado no Header Authorization
     * 5. O JWT também é enviado no corpo da resposta
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody AuthDTO data){

        // Log para registrar tentativa de criação de usuário
        log.info("Register Info: {}", data.username());

        try {

            // Chama o service que registra o usuário e gera o token JWT
            String jwt = authService.register(data);

            // Criação do header HTTP
            HttpHeaders headers = new HttpHeaders();

            // Adiciona o token JWT no header Authorization
            headers.add("Authorization", "Bearer " + jwt);

            // Retorna resposta HTTP 200 com:
            // - Header Authorization contendo o JWT
            // - Corpo contendo o JWT
            return ResponseEntity
                    .ok()
                    .headers(headers)
                    .body(jwt);

        } catch (IllegalArgumentException e){

            // Caso ocorra algum erro na validação
            throw new RuntimeException(e);
        }
    }


    /**
     * Endpoint responsável pelo LOGIN do usuário.
     *
     * URL:
     * POST /login
     *
     * O corpo da requisição deve conter:
     * - username
     * - password
     *
     * Fluxo:
     * 1. Recebe as credenciais
     * 2. Chama o AuthService para validar
     * 3. Se estiver correto, gera um JWT
     * 4. Retorna o JWT no Header Authorization
     * 5. Retorna o JWT também no corpo da resposta
     */
    @PostMapping("/login")
    public ResponseEntity<String> login (@RequestBody AuthDTO data){

        // Log da tentativa de login
        log.info("Login Info: {}", data.username());

        // Chama o service para autenticar o usuário
        String jwt = authService.login(data);

        // Criação do header HTTP
        HttpHeaders headers = new HttpHeaders();

        // Adiciona o token no header Authorization
        headers.add("Authorization", "Bearer " + jwt);

        // Retorna resposta HTTP 200 com token
        return ResponseEntity
                .ok()
                .headers(headers)
                .body(jwt);
    }
}