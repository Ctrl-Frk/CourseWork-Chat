package client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import resources.JsonParser;

import java.util.Objects;

public class ClientTests {

    @Test
    public void portTest() {
        //arrange
        int expected = 8089;

        //act
        int result = Integer.parseInt(Objects.requireNonNull(JsonParser.parseJson("port")));

        //assert
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void hostTest() {
        //arrange
        String expected = "127.0.0.1";

        //act
        String result = JsonParser.parseJson("host");

        //assert
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void setNameTest() {
        //arrange
        Client.setUserName("Javaslavskiy");
        String expected = "Javaslavskiy";

        //act
        String result = Client.getUserName();

        //assert
        Assertions.assertEquals(expected, result);
    }
}
