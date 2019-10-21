package models;

import org.hibernate.type.descriptor.java.LocalDateTimeJavaDescriptor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "full_name")
    private String fullName;

    private String username;

    @Column(name = "password_encrypted")
    private String passwordEncrypted;

    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    private String role;

    private boolean original;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Post> posts;

    public User() {
    }


    public User(String fullName, String username, String passwordEncrypted, String email,
                LocalDateTime createdAt, String role, boolean original) {
        this.fullName = fullName;
        this.username = username;
        this.passwordEncrypted = passwordEncrypted;
        this.email = email;
        this.createdAt = createdAt;
        this.role = role;
        this.original = original;
        posts = new ArrayList<>();
    }

    public void addPost(Post post) {
        post.setUser(this);
        posts.add(post);
    }

    public void removePost(Post post) {
        posts.remove(post);
    }

    public UUID getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordEncrypted() {
        return passwordEncrypted;
    }

    public void setPasswordEncrypted(String passwordEncrypted) {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isOriginal() {
        return original;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
    }

    @Override
    public String toString() {
        return "models.User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", username=" + username + '\'' +
                ", password_encrypted=" + passwordEncrypted + '\'' +
                ", email=" + email + '\'' +
                ", created_at=" + createdAt + '\'' +
                ", role=" + role + '\'' +
                ", original=" + original +
                '}';
    }
}
