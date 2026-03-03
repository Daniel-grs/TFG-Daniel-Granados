package es.metrica.sept25.evolutivo.domain.dto.user;

public class UserBasicInfoDTO {
	private String email;
	private String name;
	private String surname;

	public UserBasicInfoDTO(String email, String name, String surname) {
		super();
		this.email = email;
		this.name = name;
		this.surname = surname;
	}

	public UserBasicInfoDTO() {
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	@Override
	public String toString() {
		return "UserDTO [email=" + email + ", name=" + name + ", surname=" + surname + "]";
	}
}
