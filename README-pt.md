# 🎮 Anime RPG Gacha System

> Projeto completo em Java OOP para um sistema Gacha / Loot Box inspirado em jogos de anime, com arquitetura Client-Server, interface Swing e uma camada de apresentação preparada para demonstração acadêmica.

## 🌐 Idiomas disponíveis

O projeto está disponível em três idiomas:

- Português — este arquivo.
- Árabe — `README-ar.md`.
- Inglês — `README.md`.

O código permanece em inglês por convenção Java, mas a explicação e a entrega podem ser feitas em português, árabe ou inglês.

---

## 🏗️ Estrutura e justificativa

O projeto usa uma **arquitetura em 3 camadas**:

1. **Client / Presentation Layer**
   - Contém a interface Swing: `MainScreen`, `SummonScreen`, `InventoryScreen`, `PresentationScreen` e `GamePanel`.
   - Responsável por exibir o jogo e receber ações do usuário.

2. **API / Protocol Layer**
   - Contém `CommandCode`, `GameRequest`, `GameResponse`, `CommandHandler` e `CommandRouter`.
   - Define o contrato de comunicação entre cliente e servidor.

3. **Server / Business Layer**
   - Contém `GameEngine`, models, services, factories e exceptions.
   - Mantém as regras reais do jogo: gems, summon rates, pity, inventory, missions, upgrades e rewards.

### Por que essa estrutura?

- Separação clara entre interface, protocolo e regras de negócio.
- Testes mais fáceis pelo `Main.java`, sem depender da interface Swing.
- Facilidade para adicionar novos comandos, telas ou modos de jogo.
- Demonstração forte dos conceitos de OOP: abstraction, inheritance, polymorphism, encapsulation, factory method e custom exceptions.

---

## 🔌 Documentação da API

O cliente envia um `GameRequest` serializável via TCP Socket. O servidor roteia o comando pelo `CommandRouter`, executa o handler correto e retorna um `GameResponse`.

### Commands

- `SUMMON_SINGLE` — custo 160 gems, uma invocação.
- `SUMMON_TEN` — custo 1600 gems, dez invocações.
- `VIEW_INVENTORY` — mostra o inventário.
- `VIEW_PLAYER` — mostra perfil, gems e estatísticas do jogador.
- `LEVEL_UP` — aumenta o nível de um personagem.
- `ENHANCE_ITEM` — melhora um item.
- `HELP` — lista comandos disponíveis.
- `EXIT` — encerra a conexão.

### Fluxo

```text
Client UI → GameRequest → ServerConnector → GachaGameServer → CommandRouter → Handler → GameEngine → GameResponse
```

---

## 📺 Camada de apresentação / relatório

`PresentationScreen.java` foi melhorado para deixar a entrega mais profissional:

- Introdução visual do projeto.
- Explicação da arquitetura em 3 camadas.
- Explicação do gameplay loop: summon → loadout → fight → earn → upgrade.
- Prévia de cinco fases desenhadas com `Graphics2D`.
- Destaques de OOP.
- Explicação das gacha rates, pity e rewards.
- Caminho claro para a demonstração em sala.

---

## ▶️ Como executar

```bash
find src -name "*.java" > sources.txt
javac -d out @sources.txt
```

### Contas de demonstração

```bash
# Conta normal: começa com gems padrão
printf 'player1\n' | java -cp out Main

# Conta VIP: possui muitas gems para teste e apresentação
printf 'mamdouh\n' | java -cp out Main
```

Para abrir a interface:

```bash
java -cp out server.GachaGameServer
java -cp out client.GachaClientApp
```

Quando a janela de nome aparecer:

- Use `player1` para a conta normal.
- Use `mamdouh` para a conta VIP com muitas gems.

---

## 🗺️ Plano

O plano principal está em:

- `plan.md` — resumo da versão 0.0, versão 0.1 e futuras atualizações.
- `PLAN_v0.0.md` — plano original.
- `PLAN_v0.1.md` — plano aprimorado.

