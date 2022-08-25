package Data.Entity;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

public enum Role {
    ADMIN, EDIT, WATCH;

    public static final List<String> roles = getAllRoleType();

    private static List<String> getAllRoleType(){
        Field[] fields
                = Role.class.getDeclaredFields();
        List<String> roles = Stream.of(fields)
                .filter(target -> target.getType().equals(Role.class))
                .map(Field::getName)
                .toList();
        return roles;
    }

}

