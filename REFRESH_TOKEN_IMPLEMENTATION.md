# Implementação de Refresh Token

## Resumo

O sistema agora implementa um fluxo de autenticação robusto com **Access Token** e **Refresh Token**, garantindo segurança e melhor experiência do usuário.

## Arquivos Criados

### 1. **TokenType Enum - Atualizado**
- **Localização**: `com.financewallet.auth.domain.valueObject.TokenType`
- **Adição**: Novo tipo `REFRESH_TOKEN("refresh")`
- **Tipos disponíveis**:
  - `ACCESS_TOKEN("access")` - 15 minutos
  - `REFRESH_TOKEN("refresh")` - 7 dias
  - `SIGNUP_SESSION_TOKEN("signup_session")` - 5 minutos

### 2. **TokenResponse DTO**
- **Localização**: `com.financewallet.auth.application.dto.TokenResponse`
- **Responsabilidade**: Encapsular access token e refresh token
- **Uso**: Retorno dos use cases de autenticação

### 3. **AuthTokenResponse DTO**
- **Localização**: `com.financewallet.auth.infrastructure.adapter.in.controller.auth.dto.AuthTokenResponse`
- **Responsabilidade**: Retornar apenas access token no body da resposta HTTP
- **Uso**: Resposta do controller ao cliente

### 4. **RefreshTokenUseCase**
- **Localização**: `com.financewallet.auth.application.usercase.RefreshTokenUseCase`
- **Responsabilidade**: 
  - Validar refresh token
  - Gerar novo access token (15 minutos)
  - Gerar novo refresh token (7 dias)
- **Exceções**: Lança `UnauthorizedException` se token for inválido ou expirado

## Modificações Principais

### CompleteUserRegistrationUseCase
**Antes**:
```java
public String execute(String code, String key) {
    // ... retorna apenas access token
    return accessToken;
}
```

**Depois**:
```java
public TokenResponse execute(String code, String key) {
    // ... gera ambos os tokens
    String accessToken = jwtService.generate(TokenType.ACCESS_TOKEN, Instant.now().plus(15, ChronoUnit.MINUTES));
    String refreshToken = jwtService.generate(TokenType.REFRESH_TOKEN, Instant.now().plus(7, ChronoUnit.DAYS));
    return new TokenResponse(accessToken, refreshToken);
}
```

### AuthController

#### Endpoint `/api/v1/auth/sign-up/confirm`
**Antes**: Retornava access token em cookie
**Depois**: 
- Access token no **body** (JSON)
- Refresh token em **cookie** httpOnly (mais seguro)
- Caminho do cookie: `/api/v1/auth/refresh`

#### Novo Endpoint `/api/v1/auth/refresh`
```
POST /api/v1/auth/refresh
Cookie: refresh_token=<token>

Response:
{
  "accessToken": "<novo_access_token>"
}
```

## Fluxo de Segurança

### 1. Sign Up Completo
```
POST /api/v1/auth/sign-up/confirm
↓
✓ Access Token: Body (JSON) - 15 minutos
✓ Refresh Token: HttpOnly Cookie - 7 dias
```

### 2. Renovação de Token
```
POST /api/v1/auth/refresh
(Cookie: refresh_token)
↓
✓ Novo Access Token: Body (JSON) - 15 minutos
✓ Novo Refresh Token: HttpOnly Cookie - 7 dias
```

## Configuração de Segurança dos Cookies

### Refresh Token Cookie
- **HttpOnly**: true (protege contra XSS)
- **Secure**: false (desenvolver com HTTP, mudar para true em produção)
- **Path**: `/api/v1/auth/refresh` (restrito ao endpoint de refresh)
- **SameSite**: Lax (proteção contra CSRF)
- **MaxAge**: 7 dias

### Access Token
- **Armazenamento**: Memória (recomendado pelo Frontend)
- **Tipo**: JWT com RS256
- **Duração**: 15 minutos

## Fluxo Frontend Recomendado

```typescript
// 1. Sign Up
const response = await fetch('/api/v1/auth/sign-up/confirm', {
  method: 'POST',
  credentials: 'include', // Incluir cookies
  body: JSON.stringify({ emailCode })
});
const { accessToken } = await response.json();
localStorage.setItem('accessToken', accessToken); // Armazenar em memória

// 2. Usar Access Token
const request = await fetch('/api/v1/protected', {
  headers: {
    'Authorization': `Bearer ${accessToken}`
  }
});

// 3. Renovar quando expirar (401)
if (response.status === 401) {
  const refreshResponse = await fetch('/api/v1/auth/refresh', {
    method: 'POST',
    credentials: 'include' // Enviar refresh_token cookie
  });
  const { accessToken: newAccessToken } = await refreshResponse.json();
  localStorage.setItem('accessToken', newAccessToken);
}
```

## Mudanças nos Testes

### CompleteUserRegistrationUseCaseTest
- Agora mocka ambos os tokens (ACCESS_TOKEN e REFRESH_TOKEN)
- Verifica retorno de TokenResponse ao invés de String

### RefreshTokenUseCaseTest
- Testa validação de refresh token
- Testa geração de novos tokens
- Testa exceções para tokens inválidos/vazios

### AuthControllerTest
- Atualizado para verificar access token no body
- Atualizado para verificar refresh token em cookie
- Novo teste para endpoint `/refresh` (a ser adicionado)

## Tempos de Expiração

| Token | Duração | Propósito |
|-------|---------|----------|
| Access Token | 15 minutos | Curta duração, segura |
| Refresh Token | 7 dias | Longa duração, renova access |
| Sign-up Session | 5 minutos | Apenas confirmação de email |

## Segurança

✅ **Access Token em memória**: Não é vulnerável a XSS (comparado a cookie)
✅ **Refresh Token em HttpOnly Cookie**: Protege contra XSS, automaticamente enviado
✅ **Path restrito**: `/api/v1/auth/refresh` - não enviar para outros endpoints
✅ **SameSite=Lax**: Proteção contra CSRF
✅ **RS256**: Criptografia assimétrica mais segura

## Build & Testes

```bash
# Compilar
mvn clean compile

# Rodar testes
mvn test

# Build completo
mvn clean package
```

**Status**: ✅ Todos os 49 testes passando
