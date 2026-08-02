# JWT RS256 Setup Guide

Esta aplicação agora usa JWT com o algoritmo RS256 (RSA com SHA-256) para assinatura e validação de tokens.

## Gerando as Chaves RSA

Para gerar um par de chaves RSA 2048 bits, execute os seguintes comandos:

### 1. Gerar chave privada
```bash
openssl genrsa -out private_key.pem 2048
```

### 2. Gerar chave pública a partir da chave privada
```bash
openssl rsa -in private_key.pem -pubout -out public_key.pem
```

## Configuração das Variáveis de Ambiente

As chaves devem ser configuradas como variáveis de ambiente:

### No arquivo `.env`
```bash
JWT_PRIVATE_KEY="-----BEGIN PRIVATE KEY-----
...conteúdo da chave privada...
-----END PRIVATE KEY-----"

JWT_PUBLIC_KEY="-----BEGIN PUBLIC KEY-----
...conteúdo da chave pública...
-----END PUBLIC KEY-----"
```

### No application.yml
```yaml
jwt:
  private-key: ${JWT_PRIVATE_KEY}
  public-key: ${JWT_PUBLIC_KEY}
```

## Formato das Chaves

As chaves devem ser fornecidas em formato PEM (Privacy Enhanced Mail):

- **Chave Privada**: Começa com `-----BEGIN PRIVATE KEY-----` e termina com `-----END PRIVATE KEY-----`
- **Chave Pública**: Começa com `-----BEGIN PUBLIC KEY-----` e termina com `-----END PUBLIC KEY-----`

Inclua os headers e footers ao configurar as variáveis de ambiente.

## Exemplo Prático

### Gerar chaves de teste:
```bash
# Gerar chave privada
openssl genrsa -out private_key.pem 2048

# Gerar chave pública
openssl rsa -in private_key.pem -pubout -out public_key.pem

# Ver conteúdo da chave privada (copiar para env)
cat private_key.pem

# Ver conteúdo da chave pública (copiar para env)
cat public_key.pem
```

## Validação

Para validar as chaves geradas, você pode usar:

```bash
# Verificar se a chave privada está válida
openssl pkey -in private_key.pem -check

# Verificar se a chave pública corresponde à chave privada
openssl pkey -in private_key.pem -pubout | diff - public_key.pem
```

## Algoritmo RS256

- **R**: RSA (criptografia assimétrica)
- **S**: SHA (algoritmo de hash)
- **256**: SHA-256

O RS256 é mais seguro para ambientes de produção pois:
- Usa criptografia assimétrica (chave privada para assinar, chave pública para validar)
- Permite distribuir a chave pública sem comprometer a segurança
- Ideal para microserviços e APIs que precisam validar tokens
