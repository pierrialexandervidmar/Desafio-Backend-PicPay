package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.domain.user.UserType;
import com.picpaysimplificado.dtos.UserDTO;
import com.picpaysimplificado.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    /**
     * Valida se o usuário tem saldo suficiente para efetuar uma transação.
     *
     * @param sender O usuário remetente da transação.
     * @param amount O valor da transação a ser validado.
     * @throws Exception se o usuário for do tipo Lojista ou se o saldo for insuficiente para a transação.
     */
    public void validateTransaction(User sender, BigDecimal amount) throws Exception {
        // Verifica se o remetente é do tipo Logista e lança uma exceção se for
        if (sender.getUserType() == UserType.MERCHANT) {
            throw new Exception("Usuário do tipo Logista não está autorizado a realizar transação");
        }

        // Valida se o saldo do remetente é suficiente para a transação e lança uma exceção se não for
        if (sender.getBalance().compareTo(amount) < 0) {
            throw new Exception("Saldo Insuficiente");
        }
    }

    public User findUserById(Long id) throws Exception {
        return this.repository.findUserById(id).orElseThrow(() -> new Exception("Usuário não encontrado"));
    }

    public void saveUser(User user) {
        this.repository.save(user);
    }

    public User createUser(UserDTO data) {
        User newUser = new User(data);
        this.saveUser(newUser);
        return newUser;
    }

    public List<User> getAllUsers() {
        return this.repository.findAll();
    }
}
