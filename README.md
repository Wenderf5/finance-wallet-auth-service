## Routes

> ***/api/v1/user***
>
> POST: Verifica se o usuário já existe, gera e envia um código de confirmação de E-mail e seta um cookei de sessão para criar uma conta. </br>
>
> Request body:
> ```json
> {
>    "email": "string",
>    "password": "string",
>    "userName": "string"
> }
> ```
> Response body:
> ```json
> {
>     "status": "number",
>     "message": "string"
> }
> ```
---

## Services

> ***E-mail service***
>
> É uma serviço para enviar E-mail. Está no pacote `com.financewallet.services.EmailService.java`, os métodos para enviar são chamados em cadeia (getSession(), setEmail(), send()). O método getSession() precisa do email do provedor de email, senha do email e um arquivo de propriedades que vou deixar um modelo de referência.
>
> ```properties
> mail.smtp.host
> mail.smtp.port
> mail.smtp.auth
> mail.smtp.starttls.enable
> ```
---

> ***Redis template***
>
> A classe RedisTemplate no pacote `com.financewallet.redis.RedisTemplate.java` é um cliente redis síncrono que usa a lib lettuce. Usa a variável de ambiente `REDIS_URL_CONNECTION` no modelo `redis://:password@host:port`, pode ser injetado com a classe `RedisTemplate` e contém métodos como `RedisTemplate.set(String key, String value)` e `RedisTemplate.get(String key)`.
---
