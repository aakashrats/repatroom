package com.repatroom.model.entity;


import com.repatroom.model.enums.UserRole;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "users")
public class User {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;
    private String password;
    private UserRole role;

    @Data
    public static class Profile {
        private String firstName;
        private String lastName;
        private String phone;
        private String avatar;
    }

}
