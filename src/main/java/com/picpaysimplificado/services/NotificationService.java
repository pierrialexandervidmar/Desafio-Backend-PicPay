package com.picpaysimplificado.services;

import com.picpaysimplificado.domain.user.User;
import com.picpaysimplificado.dtos.NotificationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationService {

    @Autowired
    private RestTemplate restTemplate;

    /**
     * Envia uma notificação para o usuário especificado.
     *
     * @param user    O usuário para quem a notificação será enviada.
     * @param message A mensagem da notificação a ser enviada.
     * @throws Exception se ocorrer um erro ao enviar a notificação ou se o serviço de notificação estiver fora do ar.
     */
    public void sendNotification(User user, String message) throws Exception {
        // Obtém o endereço de e-mail do usuário
        String email = user.getEmail();

        // Cria um objeto NotificationDTO para enviar a notificação
        NotificationDTO notificationRequest = new NotificationDTO(email, message);

        // Envia a solicitação de notificação para o serviço externo
        ResponseEntity<String> notificationResponse = restTemplate.postForEntity("https://run.mocky.io/v3/54dc2cf1-3add-45b5-b5a9-6bf7e7f1f4a6", notificationRequest, String.class);

        // Verifica se a resposta da solicitação de notificação é bem-sucedida
        if (!(notificationResponse.getStatusCode() == HttpStatus.OK)) {
            System.out.println("Erro ao enviar notificação");
            throw new Exception("Serviço de notificação está fora do ar");
        }
    }
}
