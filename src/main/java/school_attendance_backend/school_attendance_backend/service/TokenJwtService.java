package school_attendance_backend.school_attendance_backend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import school_attendance_backend.school_attendance_backend.entity.User;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;



/**
 * Serviço responsável pela geração e validação de tokens JWT para autenticação.
 * Permite criar tokens, extrair informações e validar validade.
 *
 * @author SeuNome
 * @version 1.0
 * @since 2026-03-11
 */
@Service
public class TokenJwtService {

    /**
     * Chave secreta usada para assinar os tokens JWT.
     */
    private final String secret;

    /**
     * Construtor que injeta a chave secreta a partir das propriedades do Spring.
     *
     * @param secretKey A chave secreta definida no application.properties ou variáveis de ambiente.
     */

    public TokenJwtService(@Value("${jwt.secret}") String secretKey){
        this.secret = secretKey;
    }

    // --------------------- MÉTODOS PÚBLICOS ---------------------

    /**
     * Gera um token JWT para um usuário, incluindo claims extras.
     *
     * @param extraClaims Claims adicionais a serem incluídos no token.
     * @param userDetails Dados do usuário.
     * @return Token JWT assinado.
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // Expira em 24 minutos (ajustar se necessário)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Gera um token JWT para um usuário sem claims extras.
     *
     * @param userDetails Dados do usuário.
     * @return Token JWT assinado.
     */
    public String generateToken(UserDetails userDetails){
        Map<String, Object> claims = new HashMap<>();
        // Se for um usuário da sua entidade, adiciona o ID como claim
        if (userDetails instanceof User user){
            claims.put("userId", user.getId());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Extrai o nome de usuário de um token JWT.
     *
     * @param token Token JWT.
     * @return Nome de usuário presente no token.
     */
    public String extractUsername(String token){
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrai o ID do usuário de um token JWT.
     *
     * @param token Token JWT.
     * @return ID do usuário presente no token.
     */
    public Integer extractUserId(String token){
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    /**
     * Valida se um token JWT é válido para determinado usuário.
     *
     * @param token Token JWT.
     * @param userDetails Dados do usuário.
     * @return true se o token for válido e não expirado, false caso contrário.
     */
    public boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // --------------------- MÉTODOS PRIVADOS ---------------------

    /**
     * Extrai uma claim específica de um token JWT usando uma função.
     *
     * @param token Token JWT.
     * @param claimsResolver Função para extrair a claim.
     * @param <T> Tipo da claim.
     * @return Valor da claim.
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrai todas as claims de um token JWT.
     *
     * @param token Token JWT.
     * @return Claims presentes no token.
     */
    private Claims extractAllClaims(String token){
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Verifica se o token JWT expirou.
     *
     * @param token Token JWT.
     * @return true se estiver expirado, false caso contrário.
     */
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrai a data de expiração de um token JWT.
     *
     * @param token Token JWT.
     * @return Data de expiração do token.
     */
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Converte a chave secreta em bytes para assinar o token JWT.
     *
     * @return Chave secreta em bytes.
     */

    private SecretKey getSignInKey() {
        String secretBase64 = System.getenv("JWT_SECRET");
        if (secretBase64 == null || secretBase64.isBlank()) {
            throw new IllegalArgumentException("JWT_SECRET não está definido no sistema");
        }
        byte[] keyBytes = Base64.getDecoder().decode(secretBase64);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}