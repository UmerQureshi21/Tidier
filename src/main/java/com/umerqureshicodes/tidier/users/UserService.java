package com.umerqureshicodes.tidier.users;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
                passwordEncoder.encode(userRequestDTO.password()),
                userRequestDTO.displayedName()

        ));
        return new UserResponseDTO(user.getUsername(),user.getDisplayedName());
    }

    public UserResponseDTO login(UserRequestDTO dto) {
        Optional<AppUser> userOptional = userRepo.findByUsername(dto.username()) ;//userRepo.findByUsernameAndEmail(dto.username(), dto.username());
        if (userOptional.isPresent()){
            AppUser user = userOptional.get();
            System.out.println("Email verified!");
            return new UserResponseDTO(user.getUsername(),user.getDisplayedName());
        }
        System.out.println("Email not verified!");
        return new UserResponseDTO(null,null);
    }

    private boolean areCredentialsUnique(UserRequestDTO userRequestDTO) {
        Optional<AppUser> prevUserWithSameEmail = userRepo.findByUsername(userRequestDTO.username()) ;
        if(prevUserWithSameEmail.isPresent()) {
            return false;
        }
        Optional<AppUser> prevUserWithSameName = userRepo.findByUsername(userRequestDTO.displayedName()) ;
        if(prevUserWithSameName.isPresent()) {
            return false;
        }
        return true;
    }

    public UserResponseDTO getUser(String email) {
        Optional<AppUser> user = userRepo.findByUsername(email);
        if(user.isPresent()) {
            return new UserResponseDTO(user.get().getUsername(),user.get().getDisplayedName());
        }
        return null;
    }

    @Override
    public AppUser loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
