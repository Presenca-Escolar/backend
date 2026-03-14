package school_attendance_backend.school_attendance_backend.configuration;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import school_attendance_backend.school_attendance_backend.service.AuthorizationService;
import school_attendance_backend.school_attendance_backend.service.TokenJwtService;

import java.io.IOException;

/**
 * Filtro responsável por interceptar todas as requisições HTTP e realizar
 * a autenticação baseada em token JWT.
 *
 * <p>Este filtro é executado uma vez por requisição através da classe
 * {@link OncePerRequestFilter}. Ele verifica a presença do header
 * {@code Authorization} contendo um token no formato:</p>
 *
 * <pre>
 * Authorization: Bearer {token}
 * </pre>
 *
 * <p>Fluxo de funcionamento:</p>
 * <ol>
 *     <li>Obtém o header {@code Authorization} da requisição.</li>
 *     <li>Verifica se o token está presente e segue o padrão {@code Bearer}.</li>
 *     <li>Extrai o username contido no token JWT.</li>
 *     <li>Carrega os detalhes do usuário através do {@link AuthorizationService}.</li>
 *     <li>Valida o token utilizando o {@link TokenJwtService}.</li>
 *     <li>Caso válido, cria um objeto {@link UsernamePasswordAuthenticationToken}
 *     e o registra no {@link SecurityContextHolder}.</li>
 *     <li>Permite que a requisição continue na cadeia de filtros.</li>
 * </ol>
 *
 * <p>Caso o token seja inválido, o filtro retorna status HTTP {@code 401 - Unauthorized}.</p>
 *
 * <p>Este filtro é parte fundamental da estratégia de autenticação stateless
 * baseada em JWT utilizada pelo sistema.</p>
 *
 * @author Anibal Júnior
 */
@Component
public class TokenJwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Serviço responsável por operações relacionadas ao token JWT,
     * como extração de username, validação e obtenção de informações
     * contidas no token.
     */
    @Autowired
    private TokenJwtService tokenJwtService;

    /**
     * Serviço responsável por carregar os detalhes do usuário
     * a partir do username durante o processo de autenticação.
     */
    @Autowired
    private AuthorizationService authorizationService;


    /**
     * Executa o filtro de autenticação JWT para cada requisição HTTP.
     *
     * Estrutura do fluxo do filtro:
     *
     * ETAPA 1 — Capturar o header Authorization
     * O filtro verifica se a requisição possui o header:
     * Authorization: Bearer {token}
     *
     * Caso não exista ou não esteja no formato correto,
     * a requisição continua normalmente sem autenticação.
     *
     * ETAPA 2 — Extrair o token JWT
     * Remove o prefixo "Bearer " para obter apenas o token.
     *
     * ETAPA 3 — Extrair o username do token
     * Utiliza o TokenJwtService para ler o username armazenado no JWT.
     *
     * ETAPA 4 — Verificar se o usuário já está autenticado
     * Caso o SecurityContext já tenha uma autenticação válida,
     * o filtro não precisa autenticar novamente.
     *
     * ETAPA 5 — Carregar os dados do usuário
     * O AuthorizationService busca o usuário no banco
     * utilizando o username extraído do token.
     *
     * ETAPA 6 — Validar o token
     * O TokenJwtService verifica:
     * - assinatura do token
     * - validade (expiração)
     * - correspondência com o usuário
     *
     * ETAPA 7 — Criar objeto de autenticação
     * Caso o token seja válido, o filtro cria um
     * UsernamePasswordAuthenticationToken contendo:
     * - UserDetails
     * - userId
     * - authorities (roles do usuário)
     *
     * ETAPA 8 — Registrar autenticação no SecurityContext
     * O objeto de autenticação é armazenado no SecurityContextHolder,
     * permitindo que o Spring Security reconheça o usuário como autenticado.
     *
     * ETAPA 9 — Continuar a cadeia de filtros
     * A requisição segue para os próximos filtros ou controllers.
     *
     * Caso o token seja inválido, a resposta retorna:
     * HTTP 401 - Unauthorized
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // ETAPA 1 — Verificar presença do header Authorization
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request,response);
            return;
        }

        // ETAPA 2 — Extrair token JWT
        jwt = authHeader.substring(7);

        // ETAPA 3 — Extrair username do token
        username = tokenJwtService.extractUsername(jwt);

        // ETAPA 4 — Verificar se já existe autenticação
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null){

            // ETAPA 5 — Carregar dados do usuário
            UserDetails userDetails = this.authorizationService.loadUserByUsername(username);

            // ETAPA 6 — Validar token
            if (tokenJwtService.isTokenValid(jwt, userDetails)) {

                // extrair ID do usuário
                Integer userId = tokenJwtService.extractUserId(jwt);

                // ETAPA 7 — Criar objeto de autenticação
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                userId,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // ETAPA 8 — Registrar autenticação
                SecurityContextHolder.getContext().setAuthentication(authToken);

            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // ETAPA 9 — Continuar cadeia de filtros
        filterChain.doFilter(request,response);
    }
}