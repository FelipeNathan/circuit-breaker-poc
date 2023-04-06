# circuit-breaker-poc

### Bibliotecas e frameworks
- Redis (https://github.com/redis/jedis)
- Resilience4j Circuit Breaker (https://resilience4j.readme.io/docs/circuitbreaker) 

## Docker
- Este Dockerfile foi alterado para apenas copiar a pasta `application-1.0` que será gerado no `root` ao executar o makefile

## Makefile
- Para auxiliar no processo de build, criei o makefile com as tasks `clean`, `build-project`, `extract`, `build-image` e as tarefas agrupadas `run` e `run-build-image`
1. `clean`: remove a pasta `application-1.0` do root
2. `build-project`: "rebuilda" o projeto com `clean build` (ignorando testes, é apenas uma poc, relaxa)
3. `extract`: extrai a pasta `application-1.0` de `./application/build/distributions/application-1.0.tar` pro `root`
4. `build-image`: cria a imagem da aplicação chamada `myapp`
5. `run`: executa os passos 1, 2 e 3
6. `run-build-image`: executa o passo 5 e 4

## Usage
- Só buildar e subir no docker compose que tem 3 instancias do app nas portas 8080, 8081 e 8082 (foi mais rapido assim do que criar um k8s local, etc)

```
$ make run-build-image
$ docker compose up 
```

## Events
- Evento `circuit:breaker:V1`
- Payload: throwException: true|false (Simular alguma falha pra abrir o circuito)
```json
{
  "throwException":true|false
}
```
- Metadata: Origin PFM|PNP (opcional, em caso de não passar ou passar incorreto, será usado um circuito "Default")
```json
{
  "metadata": {
    "origin": "PNP"
  }
}
```

## Exemplo
- Evento que será executado no circuit breaker de PNP e **não** lançará exceção, o circuito **não** abrirá
```json
{
  "name": "circuit:breaker",
  "version": 1,
  "id": "ui-id",
  "flowId": "ui-id",
  "payload": {
    "throwException":false
  },	  
  "identity": {},	  
  "auth": {},	  
  "metadata": {
    "origin": "PNP"
  }
}
```

- Evento que será executado no circuit breaker de PFM e **lançará** exceção, o circuito **abrirá** após ter 50% de 4 requisições com falha
```json
{	  
  "name": "circuit:breaker",	  
  "version": 1,	  
  "id": "ui-id",	  
  "flowId": "ui-id",	  
  "payload": {	    
    "throwException":true
  },	  
  "identity": {},	  
  "auth": {},	  
  "metadata": {	    
    "origin": "PFM"
  }
}
```

- Evento que será executado no circuit breaker Default
```json
{
  "name": "circuit:breaker",
  "version": 1,
  "id": "ui-id",
  "flowId": "ui-id",
  "payload": {
    "throwException":true
  },
  "identity": {},
  "auth": {},
  "metadata": {} // não contém origin
}
```