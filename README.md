# ecommeercesimplificado
[![NPM](https://img.shields.io/npm/l/react)](https://github.com/Ariel-soares/ecommercesimplificado/blob/main/LICENSE) 

# Sobre o projeto

O projeto ecommercesimplificado é um projeto feito como resposta ao desafio 3 da UOL Compass

O projeto consiste em um sistema de ecommeerce que cria, gere e atualiza vendas e usuários de um ecommerce.

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
git clone https://github.com/Ariel-soares/Ajudando-o-Proximo
```

2ºPasso: Entrar no arquivo persistence.xml e trocar o valor das propriedades "user" e "password" pelas credenciais de usuário do seu banco de dados

![Persistence directory](https://github.com/Ariel-soares/Ajudando-o-Proximo/blob/main/assets/persistence-files.jpg)

![DB-credentials](https://github.com/Ariel-soares/Ajudando-o-Proximo/blob/main/assets/DB-credentials.jpg)

(Utilize MySQL para evitar problemas de compatibilidade).
Mas caso queira trocar a base de dados para uma de sua escolha, basta retirar a dependência do MySQL no arquivo POM.xml e adicionar a de seu gosto.

3º Passo: Executar o projeto via linha de comando

```bash
# entrar na pasta do projeto
# executar comandos maven para build do projeto
mvn clean install
mvn clean compile
mvn package
# entrar na pasta target
cd target
# executar o projeto
java -jar arquivo.jar que está na pasta
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
