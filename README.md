# SERVER XET

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

```java -Duser.dir=$(pwd) -cp build xet.server.Server <IP_SERVER>```

ou

```java -Duser.dir=$(pwd) -cp build xet.client.Client <IP_SERVER>```

## Credenciais

Credenciais de utilizadores para teste

### SDIS Test User A
Email: sdis_kiqyuiv_a@tfbnw.net
Password: SDISfeup123

### SDIS Test User B
Email: sdis_nncqyfv_b@tfbnw.net
Password: SDISfeup123

### SDIS Test User C
Email: sdis_ngtdohj_c@tfbnw.net
Password: SDISfeup123

### SDIS Test User D
Email: 	sdis_msianee_d@tfbnw.net
Password: SDISfeup123

## Notas
Visto não termos conseguido exportar o repositório git para svn de forma a preservar os commits,
foi adicionado na secção de documentos do redmine um ficheiro txt com todos os log's do projeto.
