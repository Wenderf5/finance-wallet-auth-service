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
import io.lettuce.core.RedisException;
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
    public void shouldThrowAnRedisOperationExceptionForSet() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.set("testKey", "testValue")).thenThrow(RedisException.class);

        assertThrows(RedisOperationException.class, () -> this.redisTemplate.set("testKey", "testValue"));
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).set("testKey", "testValue");
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
    public void shouldThrowAnRedisOperationExceptionForGet() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.get("testKey")).thenReturn(null);

        assertThrows(RedisOperationException.class, () -> this.redisTemplate.get("testKey"));
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).get("testKey");
    }

    @Test
    public void shouldSaveValueInRedisWithTtl() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.setex("testKey", 300L, "testValue")).thenReturn("OK");
        doNothing().when(this.connection).close();

        this.redisTemplate.set("testKey", "testValue", 300L);

        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).setex("testKey", 300L, "testValue");
        verify(this.connection).close();
    }

    @Test
    public void shouldThrowRedisOperationExceptionForSetWithTtl() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.setex("testKey", 300L, "testValue")).thenThrow(RedisException.class);

        assertThrows(RedisOperationException.class, () -> this.redisTemplate.set("testKey", "testValue", 300L));
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).setex("testKey", 300L, "testValue");
    }

    @Test
    public void shouldReturnTtlOfRecordFromRedis() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.ttl("testKey")).thenReturn(300L);
        doNothing().when(this.connection).close();

        Long result = this.redisTemplate.getTtl("testKey");

        assertEquals(300L, result);
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).ttl("testKey");
        verify(this.connection).close();
    }

    @Test
    public void shouldThrowRedisOperationExceptionForGetTtlMinusOne() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.ttl("testKey")).thenReturn(-1L);

        RedisOperationException exception = assertThrows(RedisOperationException.class,
                () -> this.redisTemplate.getTtl("testKey"));

        assertEquals("The 'testKey' key has not ttl.", exception.getMessage());
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).ttl("testKey");
    }

    @Test
    public void shouldThrowRedisOperationExceptionForGetTtlMinusTwo() {
        when(this.client.connect()).thenReturn(this.connection);
        when(this.connection.sync()).thenReturn(this.redisCommands);
        when(this.redisCommands.ttl("testKey")).thenReturn(-2L);

        RedisOperationException exception = assertThrows(RedisOperationException.class,
                () -> this.redisTemplate.getTtl("testKey"));

        assertEquals("The 'testKey' key was not found.", exception.getMessage());
        verify(this.client).connect();
        verify(this.connection).sync();
        verify(this.redisCommands).ttl("testKey");
    }
}
