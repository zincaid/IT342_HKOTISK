package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.Constant.ResponseCode;
import com.CSIT321.Hkotisk.DTO.ReqRes;
import com.CSIT321.Hkotisk.Entity.User;
import com.CSIT321.Hkotisk.Exception.UserCustomException;
import com.CSIT321.Hkotisk.Repository.UserRepository;
import com.CSIT321.Hkotisk.Response.ServerResponse;
import com.CSIT321.Hkotisk.Service.JWTService;
import com.CSIT321.Hkotisk.Service.MyUserDetailService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MyUserDetailService myUserDetailService;

    @Autowired
    AuthenticationManager authManager;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private JWTService jwtService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @GetMapping("/signin")
        public ResponseEntity<String> showLoginPage() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Please log in.");
    }


    @PostMapping("/signin")
    public ResponseEntity<ReqRes> login(@Valid @RequestBody ReqRes loginRequest) {
        ReqRes response = new ReqRes();
        Optional<User> userOptional = userRepo.findByEmail(loginRequest.getEmail());

        if (userOptional.isEmpty()) {
            response.setStatusCode(401);
            response.setMessage("Invalid email.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        System.out.println("Login:"+loginRequest);
        User user = userOptional.get();

        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            response.setStatusCode(401);
            response.setMessage("Invalid password");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        try {
            Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            if (authentication.isAuthenticated()) {
                UserDetails userDetails = myUserDetailService.loadUserByUsername(user.getEmail());
                String token = jwtService.generateToken(userDetails);
                
                // Add all user details to response
                response.setStatusCode(200);
                response.setMessage("Login successful");
                response.setToken(token);
                response.setRole(user.getRole());
                response.setEmail(user.getEmail());
                response.setUsername(user.getUsername());
                
                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                response.setStatusCode(401);
                response.setMessage("Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage("An unexpected error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ServerResponse> addUser(@Valid @RequestBody User user) {
        ServerResponse resp = new ServerResponse();
        try {
            if (userRepo.findByEmail(user.getEmail()).isPresent()) {
                resp.setStatus(ResponseCode.FAILURE_CODE);
                resp.setMessage("Email already in use");
                return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
            }
            // Encode the password before saving the user
            user.setPassword(encoder.encode(user.getPassword()));

            // Save the user
            userRepo.save(user);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage(ResponseCode.STUD_REG);

        } catch (Exception e) {

            return new ResponseEntity<>(resp, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(resp, HttpStatus.ACCEPTED);
    }

    @GetMapping("/signout")
    public ResponseEntity<ServerResponse> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ServerResponse resp = new ServerResponse();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            resp.setStatus(ResponseCode.SUCCESS_CODE);
            resp.setMessage("Successfully logged out");
        } else {
            resp.setStatus(ResponseCode.FAILURE_CODE);
            resp.setMessage("No user is currently logged in");
        }
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    

    private Authentication authenticate(String email, String password) {

        System.out.println(email+"---++----"+password);

        UserDetails userDetails = myUserDetailService.loadUserByUsername(email);

        System.out.println("Sign in user details"+ userDetails);

        if(userDetails == null) {
            System.out.println("Sign in details - null" + userDetails);

            throw new BadCredentialsException("Invalid username and password");
        }
        if(!encoder.matches(password,userDetails.getPassword())) {
            System.out.println("Sign in userDetails - password mismatch"+userDetails);

            throw new BadCredentialsException("Invalid password");
        }
        return new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());

    }
}
