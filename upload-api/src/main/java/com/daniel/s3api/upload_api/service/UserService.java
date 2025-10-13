package com.daniel.s3api.upload_api.service;

import com.daniel.s3api.upload_api.infrastructure.entities.User;
import com.daniel.s3api.upload_api.infrastructure.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {this.userRepository = userRepository;}

    public User salveUser(User user) {
        return userRepository.saveAndFlush(user);
    }
    public List<User> listUsers (){
        return userRepository.findAll();
    }
    public User searchUserById(Integer id){
        return userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
    }
    public User updateUser (Integer id, User newUser){
        User userSearch = userRepository.findById(id).orElseThrow(
                () -> new RuntimeException("Usuário não encontrado"));
        userSearch.setNome(newUser.getNome());
        userSearch.setEmail(newUser.getEmail());
        return userRepository.saveAndFlush(userSearch);
    }
    public void deleteUser (Integer id){
        userRepository.deleteById(id);
    }
}
