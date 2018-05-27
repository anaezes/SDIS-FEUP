# Serviço distribuído de backups

## Como compilar

### Linha de comandos
O projeto pode ser compilado usando o seguinte comando (na raíz do projeto):
1. rm -rf build && mkdir build
2. javac $(find -name "*.java") -d build

### Eclipse
Alternativamente pode ser usado o eclipse para abrir e compilar deste modo:
1. Abrir o eclipse.
2. No menu Files, selecionar a opção Import.
3. Escolher a pasta do projeto e confirmar.


## Como correr

### Servidor

```java -Duser.dir=$(pwd) -cp build xet.server.Server ```

ou

```java -Duser.dir=$(pwd) -cp build xet.client.Client ```




