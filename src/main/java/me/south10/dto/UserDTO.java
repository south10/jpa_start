package me.south10.dto;

/**
 * Created by spring on 2017-05-27.
 */
public class UserDTO {
    private String username;
    private int age;

    public UserDTO() {
    }

    public UserDTO(String username, int age) {
        this.username = username;
        this.age = age;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "username='" + username + '\'' +
                ", age=" + age +
                '}';
    }
}
