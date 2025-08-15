package lk.ijse.gdse72.blog_management.dto;

public class UserDTO {
    private Long id;
    private String email;
    private String name;
    private String bio; // new field
    private String profileImagePath; // new field

    public UserDTO() {
    }

    public UserDTO(Long id, String email, String name, String bio, String profileImagePath) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.bio = bio;
        this.profileImagePath = profileImagePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getProfileImagePath() {
        return profileImagePath;
    }

    public void setProfileImagePath(String profileImagePath) {
        this.profileImagePath = profileImagePath;
    }
}
