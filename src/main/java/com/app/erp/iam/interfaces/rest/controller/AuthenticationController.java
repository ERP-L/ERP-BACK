package com.app.erp.iam.interfaces.rest.controller;

import com.app.erp.iam.application.dto.commands.LoginCommand;
import com.app.erp.iam.application.dto.commands.RegisterCommand;
import com.app.erp.iam.application.dto.results.RegisterResult;
import com.app.erp.iam.application.internal.outboundservices.tokens.TokenService;
import com.app.erp.iam.application.usecase.RegisterUserHandler;
import com.app.erp.iam.application.usecase.LoginHandler;
import com.app.erp.iam.interfaces.rest.resources.*;
import com.app.erp.iam.interfaces.rest.transform.SignUpMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    private final RegisterUserHandler registerUserHandler;
    private final LoginHandler loginHandler;
    private final TokenService tokenService;
    private final HttpServletRequest request;
    private final SignUpMapper signUpMapper;


    public AuthenticationController(RegisterUserHandler registerUserHandler, LoginHandler loginHandler,
                                    TokenService tokenService, HttpServletRequest request, SignUpMapper signUpMapper) {
        this.registerUserHandler = registerUserHandler;
        this.loginHandler = loginHandler;
        this.tokenService = tokenService;
        this.request = request;
        this.signUpMapper = signUpMapper;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<RegisterResponse> signUp(@Valid @RequestBody SignUpRequest request) {
        RegisterCommand command = signUpMapper.toCommand(request);
        RegisterResult result = registerUserHandler.handle(command);

        RegisterResponse response = new RegisterResponse(
                result.getCompanyId(),
                result.getSecurityUserId(),
                result.getAuthUserId()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> login(
            @RequestBody SignInRequest body,
            HttpServletRequest req
    ) {
        var cmd = new LoginCommand(
                body.email(),
                body.password(),
                req.getRemoteAddr(),
                req.getHeader("X-Device"),
                req.getHeader("User-Agent")
        );
        var result = loginHandler.handle(cmd);
        return ResponseEntity.ok(SignInResponse.of(result));
    }

}
