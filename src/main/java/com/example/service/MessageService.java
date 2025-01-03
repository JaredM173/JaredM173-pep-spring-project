package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import com.example.entity.Message;
import com.example.repository.MessageRepository;
import java.util.Optional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final AccountRepository accountRepository;
    
    @Autowired
    public MessageService( MessageRepository messageRepository, AccountRepository accountRepository){
        this.messageRepository = messageRepository;
        this.accountRepository = accountRepository;
    }

    public String getusernameByAccountId(Integer accountId){
        Account account = accountRepository.findByAccountId(accountId);
        return account != null ? account.getUsername() : null;
    }

    public ResponseEntity<Message> createMessage(Message message){
        //checking if valid message
        if(message.getMessageText() == null || message.getMessageText().trim().isEmpty() || message.getMessageText().length() > 255 || message.getPostedBy() == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        //valid account?
        String username = getusernameByAccountId(message.getPostedBy());
        if (username == null || !accountRepository.existsByUsername(username)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        Message savedMessage = messageRepository.save(message);
        return ResponseEntity.ok(savedMessage);
    }

    public List<Message> getAllMessages(){
        return messageRepository.findAll();
    }

    public ResponseEntity<Message> getMessageById(Integer messageId){
        Optional<Message> optionalMessage = messageRepository.findById(messageId);
        if(optionalMessage.isPresent()){
            return ResponseEntity.ok(optionalMessage.get());
        }else{
            return ResponseEntity.ok().build();
        }
    }

    public ResponseEntity<Object> deleteMessageById(Integer messageId){
        if(messageRepository.existsById(messageId)){
            messageRepository.deleteById(messageId);
            return ResponseEntity.ok().body("1");
        }else{
            return ResponseEntity.ok().build();
        }
    }
    public ResponseEntity<Object> updateMessageText(Integer messageId, Message newMessage){
        Optional<Message> optionalMessage = messageRepository.findById(messageId);

        if(optionalMessage.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("no message");
        }

        Message message = optionalMessage.get();

        if(newMessage.getMessageText() == null || newMessage.getMessageText().trim().isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("new message is empty");
        }

        if(newMessage.getMessageText().length() > 255){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("message to large");
        }

        message.setMessageText(newMessage.getMessageText());
        messageRepository.save(message);
        return ResponseEntity.ok().body("1");

    }

    public ResponseEntity<List<Message>> getMessagesByAccountId(Integer accountId){
        List<Message> messages = messageRepository.findByPostedBy(accountId);
        return ResponseEntity.ok(messages);
    }
}
