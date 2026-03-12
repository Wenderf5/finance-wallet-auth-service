import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.financewallet.exceptions.RedisOperationException;
import com.financewallet.redis.RedisTemplate;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisTemplateTest {
    @Mock
    private RedisClient client;

    @Mock
    private StatefulRedisConnection<String, String> connection;

    @Mock
    private RedisCommands<String, String> redisCommands;

    @InjectMocks
    private RedisTemplate redisTemplate;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void shouldSaveValueInRedis() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.set("testKey", "testValue")).thenReturn("OK");
        doNothing().when(this.connection).close();

        this.redisTemplate.set("testKey", "testValue");

        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).set("testKey", "testValue");
        verify(this.connection).close();
    }

    @Test
    public void shouldReturnFromRedisTheValeuOfKey() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.get("testKey")).thenReturn("testValue");
        doNothing().when(this.connection).close();

        String result = this.redisTemplate.get("testKey");

        verify(this.client).connect();
        verify(this.connection).sync();
        assertEquals("testValue", result);
        verify(this.connection).close();
    }

    @Test
    public void shouldThrowAnRedisOperationException() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.get("testKey")).thenReturn(null);

        assertThrows(RedisOperationException.class, () -> this.redisTemplate.get("testKey"));
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).get("testKey");
    }
}
