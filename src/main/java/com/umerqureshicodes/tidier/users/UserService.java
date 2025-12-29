package com.umerqureshicodes.tidier.users;
import org.springframework.beans.factory.annotation.Autowired;
import com.umerqureshicodes.tidier.users.AppUser ;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;


    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        AppUser user = userRepo.save(new AppUser(
                userRequestDTO.username(),
                userRequestDTO.email(),
                passwordEncoder.encode(userRequestDTO.password())
        ));
        return new UserResponseDTO(user.getUsername(),user.getEmail());
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return null;
    }
}
