package com.testing;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;

import static com.testing.HelloWorldController.MESSAGE_KEY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class HelloWorldIntegrationTest {
    
    @LocalServerPort
    int port;

    @Autowired
    private TestRestTemplate template;

    private Map<String, String> result;
    private String url;
    
    @Test
    public void responseShouldContainHelloWorldKey() {
        url = "http://localhost:" + port + "/";
        
        ResponseEntity<Map> response = template.getForEntity(url, Map.class);
        result = response.getBody();

        assertThat(result.containsKey(MESSAGE_KEY)).isTrue();
        assertThat(result.get(MESSAGE_KEY)).isEqualTo("Hello World!");
    }
}
