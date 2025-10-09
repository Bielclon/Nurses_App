package model;

public class Nurse {
    private String username;
    private String password;
    private String name;
    

    public Nurse() {
		super();
	}

	public Nurse(String username, String password, String name) {
        this.username = username;
        this.password = password;
        this.name = name;
    }

    // Getters y setters
    public String getName() {return name;}
	public void setName(String name) {this.name = name;}
	
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

}
