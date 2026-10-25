package ru.kazarin.kanban.users;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

	private final UserRepo userRepo;
	private final PasswordEncoder passwordEncoder;

	public UserService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
		this.userRepo = userRepo;
		this.passwordEncoder = passwordEncoder;
	}

	public User register(UserDto dto) {
		if (userRepo.existsByEmail(dto.getEmail())) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
		}

		User user = new User();
		user.setEmail(dto.getEmail().trim().toLowerCase());
		user.setName(dto.getName().trim());
		user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
		return userRepo.save(user);
	}

	public User login(UserDto dto) {
		User user = userRepo.findByEmail(dto.getEmail().trim().toLowerCase())
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password"));

		if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
		}

		return user;
	}
}
