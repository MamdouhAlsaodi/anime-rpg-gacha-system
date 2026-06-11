# Code Review – Decisões de Design e Tratamento de Erros

## 1. Por que a classe Character foi definida como Abstrata e não como uma Interface?

A classe `Character` foi implementada como uma classe abstrata porque ela representa um modelo base para todos os personagens do sistema, contendo atributos e comportamentos compartilhados.

### Evidências encontradas no código

A classe possui atributos comuns a qualquer personagem:

```java
protected String name;
protected Rarity rarity;
protected Element element;
protected int level;
protected int hp;
protected int attack;
protected int defense;
```

Além disso, possui construtor e implementação de métodos reutilizáveis.

### Justificativa

Uma interface seria utilizada apenas para definir contratos de comportamento. Entretanto, a classe `Character` possui:

* Estado compartilhado (atributos)
* Construtor
* Regras de cálculo de atributos
* Implementação de métodos reutilizáveis

Por esse motivo, utilizar uma classe abstrata reduz duplicação de código e garante que todas as futuras subclasses de personagens herdem automaticamente os comportamentos básicos do sistema.

---

## 2. Fluxo do Polimorfismo

O polimorfismo está presente através do método:

```java
describeYourself()
```

### Estrutura de herança

```text
BaseEntity
│
├── Character
│
└── InventoryItem
     │
     ├── Weapon
     ├── Artifact
     └── Relic
```

A classe `BaseEntity` define o contrato:

```java
public abstract String describeYourself();
```

Cada tipo de item sobrescreve esse método, permitindo comportamentos diferentes em tempo de execução.

### Exemplo de execução

```java
InventoryItem item = new Weapon(...);
item.describeYourself();
```

Mesmo que a referência seja do tipo `InventoryItem`, o Java executa a implementação da classe concreta (`Weapon`, `Artifact` ou `Relic`).

### Benefícios

* Flexibilidade
* Baixo acoplamento
* Facilidade de expansão

---

## 3. Cenário de Teste que Dispara uma Exception Customizada

### Exception escolhida

```java
InsufficientGemsException
```

### Objetivo

Garantir que um jogador não consiga realizar invocações (summons) sem possuir gemas suficientes.

### Cenário de teste

Estado inicial:

```text
Gemas disponíveis: 100
```

Tentativa:

```text
Summon x1
Custo: 160 gemas
```

Resultado:

```java
throw new InsufficientGemsException(160, 100);
```

Mensagem:

```text
Not enough gems!
Required: 160
Available: 100
```

### Importância

Sem essa validação seria possível:

* Realizar summons sem recursos.
* Gerar saldo negativo de gemas.
* Quebrar a economia do jogo.
* Comprometer a integridade dos dados.

---

# Conclusão

O projeto utiliza corretamente os principais conceitos de Programação Orientada a Objetos:

* Abstração
* Herança
* Polimorfismo
* Encapsulamento
* Tratamento de Exceções

As exceções customizadas garantem que regras críticas do jogo sejam respeitadas e tornam a aplicação mais segura, organizada e fácil de manter.
