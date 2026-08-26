# Projeto de P3
## Orientações Gerais

* **Linguagem:** Java 21 (LTS)
* **.gitignore:** Adicionem todos os arquivos de IDE e configurações pessoais ao arquivo `.gitignore`.

Esse projeto tem como base o codigo fonte utilizado no semestre 2026.1 da disciplina de Programação 2, na UPE campus Caruaru. [Link para o repositório base](https://github.com/joaogt01/Loja-que-aluga-de-um-tudo).

---

## Como Começar

### Clone o repositório

```bash
git clone https://github.com/pabkaua/Sistema-alugueis.git
```

---

## Abrindo o Projeto

### IntelliJ IDEA

1. Abra o projeto:

```bash
idea .
```

2. Instale as dependências:

```bash
./mvnw clean install
```

### Visual Studio Code

1. Abra o projeto:

```bash
code .
```

2. Instale as dependências:

```bash
./mvnw clean install
```

---

## Regras de Contribuição

Para manter a organização do projeto, siga as orientações abaixo:

### Não faça push diretamente na `main`

Nunca realize alterações diretamente na branch `main`, Suba sua branch local e abra um pull request, analise o seu código e dê merge caso esteja tudo ok.

### Atualize seu repositório local

Antes de iniciar qualquer tarefa, execute:

```bash
git pull
```

### Crie uma branch para sua tarefa

Utilize uma branch descritiva para cada funcionalidade ou correção:

```bash
git checkout -b feature/coloca-aqui-oq-o-codigo-faz
```

### Envie suas alterações

Após finalizar sua tarefa:

1. Faça o commit das alterações.
2. Realize o push da sua branch para o repositório remoto.
3. Abra um Pull Request para revisão.

---

## Padrão de Commits

Sempre que possível, utilize o padrão **Conventional Commits** para manter o histórico organizado.

[Referência](https://github.com/iuricode/padroes-de-commits)

### Exemplos:

```text
feat: adiciona cadastro de produtos
fix: corrige validação de estoque
docs: atualiza documentação do projeto
refactor: reorganiza camada de serviços
test: adiciona testes para autenticação
```