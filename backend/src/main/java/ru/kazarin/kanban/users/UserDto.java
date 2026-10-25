package ru.kazarin.kanban.users;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

	public interface Register {
	}

	public interface Login {
	}

	private Integer id;

	@NotBlank(groups = {Register.class, Login.class})
	@Email(groups = {Register.class, Login.class})
	private String email;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@NotBlank(groups = {Register.class, Login.class})
	@Size(min = 6, max = 100, groups = {Register.class, Login.class})
	private String password;

	@NotBlank(groups = Register.class)
	@Size(max = 255, groups = Register.class)
	private String name;

	public UserDto() {
	}

	public UserDto(Integer id, String email, String name) {
		this.id = id;
		this.email = email;
		this.name = name;
	}

	public static UserDto from(User user) {
		return new UserDto(user.getId(), user.getEmail(), user.getName());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
