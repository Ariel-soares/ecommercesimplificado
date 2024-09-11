# ecommeercesimplificado
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/LICENSE) 

# Sobre o projeto

O projeto ecommercesimplificado é um projeto feito como resposta ao desafio 3 da UOL Compass.

O projeto consiste em um sistema de ecommerce que cria, gere e atualiza vendas, produtos e usuários.

# Tecnologias utilizadas
- Java
- SpringBoot
- Maven

## Modelo de domínio
![Modelo-De-Cominio](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/assets/ecommercesimplificadoUML.jpeg)

## Implantação em produção
- Banco de dados: MySQL

# Como executar o projeto
Pré-requisitos: Java 21,
Maven
MySQL

1º Passo: Clonar o projeto

```bash
# clonar repositório
git clone https://github.com/Ariel-soares/ecommercesimplificado
```

2ºPasso: Entrar no arquivo application.properties e trocar o valor das propriedades customizadas para seu ambiente de produção:

 2.1 Configurações de Database: Propriedades url, "user" e "password" do conjunto de propriedades da base de dados. Altere estas para que se adequem ao endereço do seu banco de dados. Também recomendo que roda a aplicação diretamente da IDE uma primeira vez com a seguinte configuração "spring.jpa.hibernate.ddl-auto=create", para que as tabelas da sua base de dados estejam congruentes com o esperado na aplicação (Este procedimento irá apagar todas as tabelas e dados na base endereçada, tenha certeza de que estee passo será feito dee maneira conciente), após isso, mude de "create" para "update", ou a outra forma que preferir e que não altere a consistência de dados.

![Database-Credentials](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/assets/Database_Credentials.jpeg)

(Utilize MySQL para evitar problemas de compatibilidade).
Mas caso queira trocar a base de dados para uma de sua escolha, basta retirar a dependência do MySQL no arquivo POM.xml e adicionar a de seu gosto.

2.2 Propriedades de Email: Nesta aplicação utilizamos funcionalidades de envio de email, logo, será necessário gerar uma chave de aplicativo no seu serviço de email de preferência, após isto, configure as propriedades host, port e username para que se adequem ao seu usuário utilizado no serviço. Para a propriedade "password", é necessário que seja criada uma variável de ambiente com o valor da senha de aplicativo criada no serviço de email, não é recomendável deixar este dado exposto na aplicação em forma de código, logo, crie uma variável de ambiente, atribua um nome que faz sentido e adicione à propriedade "password" o nome desta variável seguindo a seguinte sintáxe "${MINHA.VARIAVEL}"

![Email-Credentials](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/assets/Email_credentials.jpeg)

2.3 Propriedades de cache: Aqui utilizaremos o mecanismo de Cache Redis, e será necessário configurá-lo antes de adicionar suas credenciais ao arquivo de propriedades, então certifique-se de ter o Docker instalado em sua máquina e rode o seguinte comando:

![Email-Credentials](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/assets/Email_credentials.jpeg)

```bash
docker run --name redis-container -p 6379:6379 -d redis
```

Aqui estamos subindo uma instância do Redis na porta 6379, sinta-se livre para utilizar a porta que quiser, contanto que estja corretamente endereçada na aplicação. Após isso basta configurar a propriedade "cache.type" com o valor "redis", a propriedade "redis.host" com o valor "localhost", e a proprieedade "port" com o valor quee você atribuiu à instância levantada no docker no passo anterior.

3º Passo: Executar o projeto via linha de comando

```bash
# entrar na pasta do projeto
# executar comandos maven para build do projeto
mvn clean install
mvn spring-boot:run
```
**Caso seu projeto não execute após estes passos, tente apenas rodá-lo na sua IDE de preferência.**

# Autor

Ariel Soares Franco

https://www.linkedin.com/in/ariel-soares/

# Agradecimentos

Deixo aqui meu obrigado aos orientadores:

- Franciele Ciostek
- Diego Bonetti
- Gabryel Airez de Melo

Todos estes em todo o período do estágio se disporam a me ajudar em todas as demandas que podiam, assim como também não nos deixaram desinformados sobre os acontecimentos da empresa.

Também deixo aqui meus agradecimentos aos mebros do meu squad:

- Diego Pimenta
  https://www.linkedin.com/in/diego-pimenta-dev/
- Gerson Luís Soares
  https://www.linkedin.com/in/gersonluis/
- Luís Otavio de Siqueira
  https://www.linkedin.com/in/luisotaviosiqueira/
- Pedro Roberto Chicuta
  https://www.linkedin.com/in/pedro-chicuta-5b2315257/

Que neste período do desafio, apesar de terem seus próprios projetos para elaborar, tiveram tempo de manter o contato e a boa convivência, me ajudando assim a manter a motivação e a moral fortes o bastante para continuar no projeto
