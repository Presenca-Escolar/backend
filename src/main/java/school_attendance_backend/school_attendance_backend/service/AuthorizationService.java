package school_attendance_backend.school_attendance_backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import school_attendance_backend.school_attendance_backend.repository.AuthorizationRepository;

/**
 * @author Anibal Junior
 * @version 1.0
 * @since 2026-03-11
 * Serviço responsável pela autenticação e carregamento de usuários.
 * Implementa a interface UserDetailsService do Spring Security,
 * que é usada para buscar os detalhes de um usuário a partir do seu username.
 */
@Service
public class AuthorizationService implements UserDetailsService {

    /**
     * Repositório que fornece acesso aos dados de usuário.
     * Usado para buscar usuários no banco de dados pelo username.
     */
    @Autowired
    private AuthorizationRepository repository;

    /**
     * Carrega os detalhes de um usuário pelo username.
     * Este método é usado pelo Spring Security durante o processo de autenticação.
     *
     * @param username O nome de usuário do usuário a ser carregado.
     * @return UserDetails contendo informações de autenticação e autoridade do usuário.
     * @throws UsernameNotFoundException se o usuário não for encontrado no banco de dados.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Busca o usuário no repositório pelo username
        return repository.findByUsername(username);
        // Nota: caso queira tratar usuário não encontrado, poderia lançar explicitamente:
        // UserDetails user = repository.findByUsername(username);
        // if (user == null) throw new UsernameNotFoundException("Usuário não encontrado: " + username);
        // return user;
    }
}
