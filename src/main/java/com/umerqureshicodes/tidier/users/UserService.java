package com.umerqureshicodes.tidier.users;
import org.springframework.beans.factory.annotation.Autowired;
import com.umerqureshicodes.tidier.users.AppUser ;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        if (!areCredentialsUnique(userRequestDTO )){
            return new UserResponseDTO(null,null);
        }
        AppUser user = userRepo.save(new AppUser(
                userRequestDTO.username(),
                userRequestDTO.email(),
                passwordEncoder.encode(userRequestDTO.password())
        ));
        return new UserResponseDTO(user.getUsername(),user.getEmail());
    }

    public UserResponseDTO login(UserRequestDTO dto) {
        Optional<AppUser> userOptional = userRepo.findByUsernameAndEmail(dto.username(), dto.email());
        if (userOptional.isPresent()){
            AppUser user = userOptional.get();
            System.out.println("Email verified!");
            return new UserResponseDTO(user.getUsername(),user.getEmail());
        }
        System.out.println("Email not verified!");
        return new UserResponseDTO(null,null);
    }

    private boolean areCredentialsUnique(UserRequestDTO userRequestDTO) {
        Optional<AppUser> prevUserWithSameEmail = userRepo.findByEmail(userRequestDTO.email());
        if(prevUserWithSameEmail.isPresent()) {
            return false;
        }
        Optional<AppUser> prevUserWithSameName = userRepo.findByUsername(userRequestDTO.username()) ;
        if(prevUserWithSameName.isPresent()) {
            return false;
        }
        return true;
    }
    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
