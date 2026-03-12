import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.ArgumentMatchers.nullable;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

import com.financewallet.redis.RedisTemplate;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisTemplateTest {
    @Mock
    private RedisClient client;

    @InjectMocks
    private RedisTemplate redisTemplate;

    @Mock
    private StatefulRedisConnection<String, String> connection;

    @Mock
    private RedisCommands<String, String> redisCommands;

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
}
