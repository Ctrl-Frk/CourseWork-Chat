package server;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.JsonParser;

import java.util.Objects;

public class ServerTests {

    @Test
    public void portTest() {
        //arrange
        int expected = 8089;

        //act
        int result = Integer.parseInt(Objects.requireNonNull(JsonParser.parseJson("port")));

        //assert
        Assertions.assertEquals(expected, result);
    }
}
