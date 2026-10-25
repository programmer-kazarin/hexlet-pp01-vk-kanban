package ru.kazarin.kanban.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ru.kazarin.kanban.users.User;
import ru.kazarin.kanban.users.UserDto;
import ru.kazarin.kanban.users.UserService;

@RestController
@RequestMapping("/api/users")
public class UsersController {

	private final UserService userService;
	private final Validator validator;

	public UsersController(UserService userService, Validator validator) {
		this.userService = userService;
		this.validator = validator;
	}

	@PostMapping("/register")
	public ResponseEntity<UserDto> register(@RequestBody UserDto dto, HttpServletRequest request) {
		validate(dto, UserDto.Register.class);
		User user = userService.register(dto);
		establishSession(user, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(UserDto.from(user));
	}

	@PostMapping("/login")
	public ResponseEntity<UserDto> login(@RequestBody UserDto dto, HttpServletRequest request) {
		validate(dto, UserDto.Login.class);
		User user = userService.login(dto);
		establishSession(user, request);
		return ResponseEntity.ok(UserDto.from(user));
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletRequest request) {
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		return ResponseEntity.noContent().build();
	}

	private void validate(UserDto dto, Class<?> group) {
		Set<ConstraintViolation<UserDto>> violations = validator.validate(dto, group);
		if (!violations.isEmpty()) {
			String message = violations.stream()
					.map(ConstraintViolation::getMessage)
					.collect(Collectors.joining("; "));
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
		}
	}

	private void establishSession(User user, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
				user.getEmail(),
				null,
				AuthorityUtils.createAuthorityList("ROLE_USER"));

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authentication);
		SecurityContextHolder.setContext(context);

		HttpSession session = request.getSession(true);
		session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
	}
}
