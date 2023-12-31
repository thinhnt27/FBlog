package tech.fublog.FuBlog.controller;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tech.fublog.FuBlog.auth.AuthenticationResponse;
import tech.fublog.FuBlog.auth.AuthenticationRequest;
import tech.fublog.FuBlog.auth.MessageResponse;
import tech.fublog.FuBlog.auth.SignupRequest;
import tech.fublog.FuBlog.dto.UserDTO;
import tech.fublog.FuBlog.entity.RoleEntity;
import tech.fublog.FuBlog.entity.UserEntity;
import tech.fublog.FuBlog.repository.CategoryCustomRepo;
import tech.fublog.FuBlog.repository.RoleCustomRepo;
import tech.fublog.FuBlog.repository.RoleRepository;
import tech.fublog.FuBlog.repository.UserRepository;
import tech.fublog.FuBlog.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import tech.fublog.FuBlog.service.JwtService;
import tech.fublog.FuBlog.service.UserService;

import java.util.*;


@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "https://fublog.tech"})
//@CrossOrigin(origins = "*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final JwtService jwtService;

    @Autowired
    CategoryCustomRepo categoryCustomRepo;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    UserService userService;

    @Autowired
    RoleCustomRepo roleCustomRepo;

    @GetMapping("/getAllUser")
    public List<UserEntity> getAllUser(){
        return  userService.getAllUser();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authenticationRequest){
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<UserEntity> o_user = userRepository.findByUsernameAndStatusTrue(authenticationRequest.getUsername());
        if(o_user.isPresent()){
            String encodedPasswordFromDatabase  = o_user.get().getPassword();
//            if (!userRepository.existsByUsername(authenticationRequest.getUsername()))
//                return ResponseEntity.badRequest().body(new MessageResponse("Error: User or password are incorect"));
//            else
                if(!passwordEncoder.matches(authenticationRequest.getPassword(),encodedPasswordFromDatabase)){
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username or password is wrong!"));
            }else{
                return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
            }
        }else return ResponseEntity.badRequest().body(new MessageResponse("Error: Username or password is wrong!"));
//        String encodedPasswordFromDatabase  = getEncodedPasswordFromDatabase(authenticationRequest.getUsername());
        //        }else if((storedPassword != hashedPassword))
        //        if (!userRepository.existsByUsername(authenticationRequest.getUsername())) {
        //            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username or password is wrong!"));


    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        if(signUpRequest.getPicture() == null){
            UserEntity user = new UserEntity(signUpRequest.getFullName(),
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()),
                    "https://firebasestorage.googleapis.com/v0/b/fublog-6a7cf.appspot.com/o/files%2Fdefault-avatar.png?alt=media&token=876d6a33-39a1-4d03-a81c-cd291144fdef&_gl=1*1allgyf*_ga*MTYyODg2MDg2MC4xNjg0Njg2NjQy*_ga_CW55HF8NVT*MTY5Njk0MzI1My4xMDMuMS4xNjk2OTQzMzk0LjM2LjAuMA&fbclid=IwAR3D93i-DgqUvJPJkuAe0eoNEJV6atVqChekdobAkufvqDgN4qDinZQxoiM",
                    true,
                    true,
                    0.0
            );

            Set<RoleEntity> roleEntities = new HashSet<>();
            RoleEntity userRole = roleRepository.findByName("USER");
            roleEntities.add(userRole);
            user.setRoles(roleEntities);

            userRepository.save(user);
        }else{
            UserEntity user = new UserEntity(signUpRequest.getFullName(),
                    signUpRequest.getUsername(),
                    signUpRequest.getEmail(),
                    encoder.encode(signUpRequest.getPassword()),
                    signUpRequest.getPicture(),
                    true,
                    true,
                    0.0
            );

            Set<RoleEntity> roleEntities = new HashSet<>();
            RoleEntity userRole = roleRepository.findByName("USER");
            roleEntities.add(userRole);
            user.setRoles(roleEntities);

            userRepository.save(user);
        }

        AuthenticationRequest authenticationRequest = new AuthenticationRequest(signUpRequest.getUsername(), signUpRequest.getPassword());
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @PostMapping("google")
    public ResponseEntity<?> loginGoogle(@Valid @RequestBody SignupRequest signUpRequest) {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        Optional<UserEntity> o_user = userRepository.findByUsernameAndStatusTrue(signUpRequest.getEmail());
        if (o_user.isPresent()) {
            String encodedPasswordFromDatabase = o_user.get().getPassword();
            if (!passwordEncoder.matches(signUpRequest.getPassword(), encodedPasswordFromDatabase)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Error: Username or password is wrong!"));
            } else {
                AuthenticationRequest authenticationRequest = new AuthenticationRequest(signUpRequest.getEmail(), signUpRequest.getPassword());
                return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
            }
        }
        UserEntity user = new UserEntity(signUpRequest.getFullName(),
                signUpRequest.getEmail(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()),
                signUpRequest.getPicture(),
                true,
                true,
                0.0
        );
        Set<RoleEntity> roleEntities = new HashSet<>();
        RoleEntity userRole = roleRepository.findByName("USER");
        roleEntities.add(userRole);
        user.setRoles(roleEntities);
        userRepository.save(user);
        AuthenticationRequest authenticationRequest = new AuthenticationRequest(signUpRequest.getEmail(), signUpRequest.getPassword());
        return ResponseEntity.ok(authenticationService.authenticate(authenticationRequest));
    }

    @GetMapping("/getUserInfo")
    public UserDTO InfoUser(@RequestHeader("Authorization") String token) {

        String username = jwtService.extractTokenToGetUser(token.substring(7));
        List <String> roles = jwtService.extractTokenToGetRoles(token.substring(7));
        Optional<UserEntity> user = userRepository.findByUsernameAndStatusTrue(username);
//        AuthenticationReponse authenticationReponse = new AuthenticationReponse();
//        authenticationReponse.setFullname(user.get().getFullName());
//        authenticationReponse.setPicture(user.get().getPicture());
//        authenticationReponse.setEmail(user.get().getEmail());
//        authenticationReponse.setId(user.get().getId());
//        authenticationReponse.setPassword(user.get().getHashedpassword());

//        return authenticationReponse;
        UserDTO userDTO = new UserDTO();
        userDTO.setFullName(user.get().getFullName());
        userDTO.setPicture(user.get().getPicture());
        userDTO.setEmail(user.get().getEmail());
        userDTO.setId(user.get().getId());
        userDTO.setPassword(user.get().getHashedpassword());
        userDTO.setRoles(roles);
        userDTO.setUsername(user.get().getUsername());

//        userDTO.setCategories();
        return userDTO;

    }
    @GetMapping("/refreshToken")
    public ResponseEntity<?> getNewToken(@RequestHeader("Authorization") String refreshToken) {
        String username = jwtService.extractTokenToGetUser(refreshToken.substring(7));
//        Optional<UserEntity> user = userRepository.findByUsername(username);
        if (username != null) {
            UserEntity user = userRepository.findByUsernameAndStatusTrue(username).orElseThrow();
            List<RoleEntity> role = null;
            if (user != null) {
                role = roleCustomRepo.getRole(user);
            }
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            Set<RoleEntity> set = new HashSet<>();
            role.stream().forEach(c -> set.add(new RoleEntity(c.getName())));
            user.setRoles(set);
            set.stream().forEach(i -> authorities.add(new SimpleGrantedAuthority(i.getName())));
            var jwtToken = jwtService.generateToken(user, authorities);
//            var jwtRefreshToken = jwtService.generateRefreshToken(user, authorities);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setToken(jwtToken);
            authenticationResponse.setRefreshToken(refreshToken.substring(7));
//            System.out.println(authenticationReponse);
            return ResponseEntity.ok(authenticationResponse);
        }
        return ResponseEntity.badRequest().body(new MessageResponse("Can not have new token!!!"));

    }
//   @GetMapping("/logout")
//public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
//       String token = authorizationHeader.substring(7);
//    // Lấy token từ yêu cầu HTTP
//    String token = request.getHeader("Authorization");
//
//    // Xóa token khỏi session
//    SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
//    SecurityContextHolder.getContext().setAuthentication(null);
//    SecurityContextHolder.getContext().getAuthentication().invalidate();
//
//    // Truyền thông với người dùng rằng họ đã đăng xuất
//    return ResponseEntity.ok("Đăng xuất thành công!");
//}
//
//}
}
