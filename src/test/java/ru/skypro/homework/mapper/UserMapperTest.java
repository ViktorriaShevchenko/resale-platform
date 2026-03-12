package ru.skypro.homework.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skypro.homework.dto.Role;
import ru.skypro.homework.dto.User;
import ru.skypro.homework.entity.UserEntity;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    private UserEntity userEntity;
    private User userDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1);
        userEntity.setEmail("test@test.com");
        userEntity.setFirstName("Иван");
        userEntity.setLastName("Петров");
        userEntity.setPhone("+7(999)123-45-67");
        userEntity.setRole(Role.USER);
        userEntity.setImage("/users/1/image");

        userDto = new User();
        userDto.setId(1);
        userDto.setEmail("test@test.com");
        userDto.setFirstName("Иван");
        userDto.setLastName("Петров");
        userDto.setPhone("+7(999)123-45-67");
        userDto.setRole(Role.USER);
        userDto.setImage("/users/1/image");
    }

    @Test
    void toUserDto_ShouldMapAllFields() {
        User result = userMapper.toUserDto(userEntity);

        assertNotNull(result);
        assertEquals(userEntity.getId(), result.getId());
        assertEquals(userEntity.getEmail(), result.getEmail());
        assertEquals(userEntity.getFirstName(), result.getFirstName());
        assertEquals(userEntity.getLastName(), result.getLastName());
        assertEquals(userEntity.getPhone(), result.getPhone());
        assertEquals(userEntity.getRole(), result.getRole());
        assertEquals(userEntity.getImage(), result.getImage());
    }

    @Test
    void toUserEntity_ShouldMapAllFields() {
        UserEntity result = userMapper.toUserEntity(userDto);

        assertNotNull(result);
        assertEquals(userDto.getId(), result.getId());
        assertEquals(userDto.getEmail(), result.getEmail());
        assertEquals(userDto.getFirstName(), result.getFirstName());
        assertEquals(userDto.getLastName(), result.getLastName());
        assertEquals(userDto.getPhone(), result.getPhone());
        assertEquals(userDto.getRole(), result.getRole());
        assertEquals(userDto.getImage(), result.getImage());
    }
}
