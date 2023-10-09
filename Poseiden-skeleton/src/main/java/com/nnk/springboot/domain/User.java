package com.nnk.springboot.domain;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @NotBlank(message = "The Username is mandatory")
    @Column(name = "username")
    private String username;

    @Pattern(regexp ="^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$", message = "The password must contain at least :\n" +
            "-One capital among A to Z\n" +
            "-One digit among 0 to 9\n" +
            "-One special character among the following #?!@$%^&*\"")
    @Size(min = 8, message = "The password must be at least be 8 characters long" )
    @NotBlank(message = "The Password is mandatory")
    @Column(name = "password")
    private String password;

    @NotBlank(message = "The Full Name is mandatory")
    @Column(name = "fullname")
    private String fullname;

    @NotBlank(message = "The Role is mandatory")
    @Column(name = "role")
    private String role;

    public User() {
    }

    public User(@NotBlank(message = "Username is mandatory") String username,
                @NotBlank(message = "Password is mandatory") @Pattern(regexp = "^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{8,}$") String password,
                @NotBlank(message = "FullName is mandatory") String fullname,
                @NotBlank(message = "Role is mandatory") String role) {
        this.username = username;
        this.password = password;
        this.fullname = fullname;
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
