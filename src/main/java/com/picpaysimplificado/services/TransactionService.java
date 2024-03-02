package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.transaction.Transaction;
import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.NotificationDTO;
import com.picpaysimplificado.dtos.TransactionDTO;
import com.picpaysimplificado.repositories.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Service
public class TransactionService {

    @Autowired
    private UserService userService;

    @Autowired
    private TransactionRepository repository;

    @Autowired
    private RestTemplate restTemplate;

    private NotificationService notificationService;

    /**
     * Cria uma transação entre dois usuários.
     *
     * @param transaction O objeto TransactionDTO contendo os detalhes da transação a ser criada.
     * @throws Exception se ocorrer um erro durante o processo de criação da transação ou se a transação não for autorizada.
     */
    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
        // Encontra o remetente e o destinatário da transação
        User sender = this.userService.findUserById(transaction.senderId());
        User receiver = this.userService.findUserById(transaction.receiverId());

        // Valida a transação do ponto de vista do remetente
        userService.validateTransaction(sender, transaction.value());

        // Verifica se o remetente está autorizado a realizar a transação
        boolean isAuthorized = this.authorizeTransaction(sender, transaction.value());

        // Se não estiver autorizado, lança uma exceção
        if (!isAuthorized) {
            throw new Exception("Transação não autorizada!");
        }

        // Cria uma nova instância de Transaction e define seus atributos
        Transaction newTransaction = new Transaction();
        newTransaction.setAmount(transaction.value());
        newTransaction.setSender(sender);
        newTransaction.setReceiver(receiver);
        newTransaction.setTimestamp(LocalDateTime.now());

        // Atualiza os saldos do remetente e do destinatário
        sender.setBalance(sender.getBalance().subtract(transaction.value()));
        receiver.setBalance(receiver.getBalance().add(transaction.value()));

        // Salva a nova transação e atualiza os usuários no repositório
        this.repository.save(newTransaction);
        this.userService.saveUser(sender);
        this.userService.saveUser(receiver);

        this.notificationService.sendNotification(sender, "Transação realizada com Sucesso");

        this.notificationService.sendNotification(receiver, "Transação recebida com Sucesso");

        return newTransaction;
    }


    /**
     * Autoriza uma transação consultando um serviço externo.
     *
     * @param sender O usuário remetente da transação.
     * @param value O valor da transação a ser autorizado.
     * @return true se a transação for autorizada, false caso contrário.
     */
    public boolean authorizeTransaction(User sender, BigDecimal value) {
        // Faz uma requisição para verificar a autorização da transação em um serviço externo
        ResponseEntity<Map> authorizationResponse = restTemplate.getForEntity("https://run.mocky.io/v3/5794d450-d2e2-4412-8131-73d0293ac1cc", Map.class);

        // Verifica se a resposta da autorização é bem-sucedida e se a mensagem é "Autorizado"
        if(authorizationResponse.getStatusCode() == HttpStatus.OK &&
                authorizationResponse.getBody().get("message").equals("Autorizado")) {
            return true;
        } else {
            return false;
        }
    }
}
