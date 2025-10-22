package model;


@Entity // This tells Hibernate to make a table out of this class
public class Nurse {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	private String user;
	private String name;
	private String password;
	private String email;	

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
