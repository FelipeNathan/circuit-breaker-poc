# NOME DA APLICAÇÃO

Descrição do propósito da aplicação e o que ela faz.

## Requisitos

Descrever os requisitos para rodar a aplicação localmente e para que seja possivel o desenvolvimento. Exemplo:

- [JDK 17](https://sdkman.io/)
- [Kotlin 1.6.0](https://sdkman.io/)

## Tecnologias/Frameworks *[Opcional]*

Lista com as tecnologias/frameworks utilizados no projeto, juntamente com o link de referência de cada um. Exemplo:

- [Gradle](https://gradle.org/)
- [Spring Framework](https://spring.io/)
- [Ktor](https://ktor.io/)

## Execução

Descrever o passo a passo para executar o projeto localmente, bem como suas dependências (Banco de Dados, Cache, etc.). Exemplo:

Para executar a aplicação, é só rodar a task *run* do Gradle Wrapper.

```bash
./gradlew run
```

## Endpoints

URL de produção: *URL DO SERVIÇO EM PRODUÇÃO*

### REST

Mapeamento dos Endpoints que podem ser acionados utilizando o padrão REST.

- ***HTTP_VERB*** /example/route - Breve descrição do propósito do Endpoint.
  <details>
      <summary><b>Request Body</b></summary><p>
  
  ```json
  {
  	"name" : "string",
  	"count": 0,
  	"example": {
  		"example": true,
  		"object": [
  			"string"
  		]
  	}
  }
  ```
  </p>
  </details></br>
  
  <details>
    <summary><b>Response Body</b></summary><p>

    ```json
    {
    	"status" : true
    }
    ```
  </p>
  </details></br>

### Protocolo de eventos

Todos os eventos que a aplicação recebe deverá ser descrito nessa seção com os parâmetros que deverão ser passados.

Para saber mais sobre o protocolo de eventos acesse o [link](https://github.com/GuiaBolso/events-protocol).

Todo evento deve ser feito um ***POST*** em `/events`

- ***EVENT:EXAMPLE***

    <details>
        <summary><b>Request Body</b></summary><p>

    ```json
    {
       "name": "event:example",
        "version": 1,
        "id": 123456,
        "payload": null,
        "identity": {
            "userId": 123456
        },
        "flowId": "1",
        "metadata": {},
        "auth": {}
    }
    ```
    </p>
    </details></br>

    <details>
        <summary><b>Response Body</b></summary><p>

    ```json
    {
      "name": "event:example",
      "version": 1,
      "id": 123456,
      "payload": {
        "url": "http://example.com.br"
      },
      "identity": {
        "userId": 123456
      },
      "flowId": "1",
      "metadata": {},
      "auth": {}
    }
    ```
    </p>
    </details></br>

## Deploy

Descrever todas as etapas para que o Deploy da aplicação seja realizado. Exemplo:

Ao ser aberto um Pull Request, será disparado um [job de validação de testes](https://jenkins.obk-tools.limbo.work/) no Jenkins. Após ser validado com sucesso, o responsável pelo deploy deverá ir para o [job de deploy](https://jenkins.obk-tools.limbo.work/) e acompanhar todo o processo enquanto verifica se o mesmo não apresenta problemas no ambiente de produção. 

## Monitoramento

Descrever o colocar os links de onde a aplicação poderá ser monitorada. Exemplo:

Para acompanhar o processo, utilize o OpenSearch ([QA](https://obk-logs-qa.observability.ppay.me/_dashboards/app/home), [Prod](https://obk-logs-prod.observability.ppay.me/_dashboards/app/home)) e o Datadog ([QA](https://app.datadoghq.com/apm/home?env=qa), [Prod](https://app.datadoghq.com/apm/home?env=prod)).
