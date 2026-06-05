# Roteiro de Testes de Caixa-Preta — Anime RPG Gacha System

**Disciplina:** Programação Orientada a Objetos (POO)  
**Projeto:** Anime RPG Gacha System  
**Tipo de Teste:** Teste de Caixa-Preta (Black Box Testing)  
**Data:** 05/06/2026  

---

## 1. Introdução

O **Teste de Caixa-Preta** é uma técnica de teste de software que avalia a funcionalidade do sistema sem examinar seu código interno. O foco está nas **entradas** e **saídas** esperadas, garantindo que o sistema atenda aos requisitos funcionais especificados.

Neste roteiro, testamos o **Anime RPG Gacha System**, um jogo baseado em invocação (gacha) de personagens e itens com arquitetura cliente-servidor em 3 camadas (Apresentação Swing, API TCP Sockets, Regras de Negócio).

### 1.1 Classificação dos Testes

- ✅ **Válido (V):** Entradas dentro das regras de negócio
- ❌ **Inválido (I):** Entradas que violam regras ou restrições
- 🔶 **Limite (L):** Entradas nos valores-limite (boundary)

### 1.2 Custo das Ações

| Ação | Custo em Gems |
|------|---------------|
| Invocação Simples (1x) | 160 |
| Invocação Múltipla (10x) | 1.600 |
| Ver Inventário | Grátis |
| Ver Perfil do Jogador | Grátis |
| Level Up | Grátis (usa materiais) |
| Melhorar Item | Grátis (usa materiais) |

---

## 2. Roteiro de Testes

---

### 2.1 Criação e Gerenciamento do Jogador (Player)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-P01 | Criar jogador com nome e gems válidos | Sistema iniciado | `new Player("Naruto", 5000)` | Jogador criado com nome="Naruto", gems=5000, pityCounter=0, softPityCounter=0, totalPulls=0 | V |
| TC-P02 | Criar jogador com gems zerados | Sistema iniciado | `new Player("Teste", 0)` | Jogador criado com gems=0 | V |
| TC-P03 | Criar jogador com gems negativos | Sistema iniciado | `new Player("Bug", -100)` | Jogador criado — gems negativos não são validados no construtor (comportamento observado) | L |
| TC-P04 | Adicionar gems ao jogador | Jogador com 1000 gems | `player.addGems(500)` | gems = 1500 | V |
| TC-P05 | Ver dados do jogador (comando VIEW_PLAYER) | Jogador criado | Enviar comando VIEW_PLAYER | Retorna nome, gems, pity, softPity, totalPulls, commonCurrency, rareCurrency | V |
| TC-P06 | Adicionar gems negativos | Jogador com 1000 gems | `player.addGems(-200)` | gems = 800 (valor negativo é somado, reduzindo o saldo) | I |

---

### 2.2 Sistema de Invocação Gacha (Summon)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-G01 | Invocação simples com gems suficientes | Jogador com 1000 gems | Enviar SUMMON_SINGLE (custo: 160 gems) | Retorna 1 personagem OU 1 item; gems = 840; pityCounter++; softPityCounter++; totalPulls++ | V |
| TC-G02 | Invocação simples com gems insuficientes | Jogador com 100 gems | Enviar SUMMON_SINGLE | Lança `InsufficientGemsException`: "Not enough gems! Required: 160, Available: 100"; gems permanecem 100 | I |
| TC-G03 | Invocação simples com gems exatos (160) | Jogador com exatamente 160 gems | Enviar SUMMON_SINGLE | Invocação realizada com sucesso; gems = 0 | L |
| TC-G04 | Invocação simples com 159 gems | Jogador com 159 gems | Enviar SUMMON_SINGLE | Lança `InsufficientGemsException` | L |
| TC-G05 | Invocação múltipla (10x) com gems suficientes | Jogador com 10000 gems | Enviar SUMMON_TEN (custo: 1600 gems) | Retorna lista com 10 itens/personagens; gems = 8400; pityCounter += 10 | V |
| TC-G06 | Invocação múltipla (10x) com gems insuficientes | Jogador com 500 gems | Enviar SUMMON_TEN | Lança `InsufficientGemsException`: "Required: 1600, Available: 500" | I |
| TC-G07 | Invocação múltipla com gems exatos (1600) | Jogador com exatamente 1600 gems | Enviar SUMMON_TEN | 10 invocações realizadas; gems = 0 | L |
| TC-G08 | Invocação múltipla com 1599 gems | Jogador com 1599 gems | Enviar SUMMON_TEN | Lança `InsufficientGemsException` | L |
| TC-G09 | Verificar dedução correta de gems após invocação | Jogador com 5000 gems | Enviar SUMMON_SINGLE | gems = 5000 - 160 = 4840; totalPulls = 1 | V |
| TC-G10 | Verificar tipo do resultado da invocação | Jogador com gems suficientes | Enviar SUMMON_SINGLE várias vezes | Resultado é instância de `Character` (Hero/Support/Antagonist) OU `InventoryItem` (Weapon/Artifact) | V |

---

### 2.3 Sistema de Pity (Piedade)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-PT01 | Pity counter incrementa a cada invocação | Jogador novo (pity=0) | Realizar 1 invocação | pityCounter = 1; softPityCounter = 1 | V |
| TC-PT02 | Hard Pity garante Lendário no pull 90 | Jogador com pityCounter=89 | Realizar 1 invocação | Resultado obrigatoriamente LEGENDARY; pityCounter reseta para 0 | L |
| TC-PT03 | Soft Pity garante Raro no pull 10 | Jogador com softPityCounter=9 | Realizar 1 invocação | Resultado obrigatoriamente RARE ou LEGENDARY; softPityCounter reseta | L |
| TC-PT04 | Hard Pity reseta ao obter Lendário | Jogador com pityCounter=45 | Invocar até obter Lendário | Após Lendário: pityCounter = 0, softPityCounter = 0 | V |
| TC-PT05 | Soft Pity reseta ao obter Raro | Jogador com softPityCounter=5 | Invocar até obter Raro | Após Raro: softPityCounter = 0; pityCounter continua incrementando | V |
| TC-PT06 | Taxa de Lendário aumenta após pull 70 | Jogador com pityCounter=75 | Invocar 1x | Taxa de Lendário = 0.006 + (75-70) × 0.01 = 5.6% (maior que a base) | V |
| TC-PT07 | Taxa base de Lendário antes do pull 70 | Jogador com pityCounter=50 | Invocar 1x | Taxa de Lendário = 0.6% (taxa base) | V |

---

### 2.4 Sistema de Inventário (Inventory)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-I01 | Adicionar personagem ao inventário | Inventário vazio (capacidade 200) | `inv.addCharacter(hero)` | Personagem adicionado; characterCount = 1 | V |
| TC-I02 | Adicionar item ao inventário | Inventário vazio | `inv.addItem(weapon)` | Item adicionado; itemCount = 1 | V |
| TC-I03 | Remover personagem do inventário | Inventário com 1 personagem | `inv.removeCharacter(hero.getId())` | Personagem removido; characterCount = 0 | V |
| TC-I04 | Remover item do inventário | Inventário com 1 item | `inv.removeItem(weapon.getId())` | Item removido; itemCount = 0 | V |
| TC-I05 | Ver inventário (comando VIEW_INVENTORY) | Inventário com personagens e itens | Enviar VIEW_INVENTORY | Retorna lista de personagens e itens com nomes, raridade e nível | V |
| TC-I06 | Inventário cheio — adicionar personagem | Inventário com 200 itens | `inv.addCharacter(new Hero(...))` | Lança `InventoryFullException`: "Inventory is full! Capacity: 200" | I |
| TC-I07 | Inventário cheio — adicionar item | Inventário com 200 itens | `inv.addItem(new Weapon(...))` | Lança `InventoryFullException` | I |
| TC-I08 | Inventário quase cheio (199 itens) | Inventário com 199 itens | `inv.addCharacter(hero)` | Personagem adicionado; inventário agora está cheio (200) | L |
| TC-I09 | Adicionar personagem duplicado | Inventário já contém "Rimuru" (Hero) | `inv.addCharacter(outroHeroChamadoRimuru)` | Lança `DuplicateCharacterException`: "Duplicate character: Rimuru! Converting to constellation." | I |
| TC-I10 | Adicionar item com mesmo nome (não duplica) | Inventário com "Dragon Slayer" | `inv.addItem(outroDragonSlayer)` | Item adicionado normalmente (itens não têm verificação de duplicata) | V |
| TC-I11 | Capacidade padrão do inventário | Sistema iniciado | Verificar `inv.getCapacity()` | Capacidade = 200 | V |

---

### 2.5 Sistema de Constelação (Duplicatas)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-C01 | Duplicata de Hero consteção C0 | Hero "Goku" no inventário, consteção=0 | Invocar outro "Goku" | `DuplicateCharacterException` capturada; Goku evolui para C1 | V |
| TC-C02 | Duplicata evolui consteção C0 → C1 | Hero com consteção=0 | `hero.upgradeConstellation()` | consteção = 1 | V |
| TC-C03 | Constelação máxima C6 | Hero com consteção=6 | `hero.upgradeConstellation()` | consteção = 7 (excede máximo?) | L |
| TC-C04 | Duplicata de Support não-Hero | Support "Sakura" no inventário | Invocar outra "Sakura" | `DuplicateCharacterException`; comportamento depende de como o sistema trata não-Heroes | V |
| TC-C05 | Constelação não aplica a itens | Weapon no inventário | Invocar mesma weapon | Novo item adicionado normalmente (itens não têm consteção) | V |

---

### 2.6 Sistema de Progressão (Level Up & Enhance)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-L01 | Level up de personagem | Hero nível 1 | `progressionSystem.levelUp(hero, 5)` | Hero agora é nível 6; HP, Attack, Defense recalculados | V |
| TC-L02 | Level up com 0 níveis | Hero nível 10 | `progressionSystem.levelUp(hero, 0)` | Hero permanece nível 10 | L |
| TC-L03 | Level up com nível negativo | Hero nível 10 | `progressionSystem.levelUp(hero, -3)` | Hero nível 7 (comportamento observado: soma direta, sem validação de negativo) | I |
| TC-L04 | Enhance de item | Weapon nível 1 | `progressionSystem.enhanceItem(weapon, 5)` | Weapon agora é nível 6 | V |
| TC-L05 | Cálculo de Power Level | Hero nível 50, HP=5000, ATK=600, DEF=400 | `progressionSystem.calculatePowerLevel(hero)` | Power = 5000 + (600×2) + (400×1.5) = 5000 + 1200 + 600 = 6800 | V |
| TC-L06 | Rank S (Power ≥ 5000) | Character com power ≥ 5000 | `progressionSystem.getCharacterRank(c)` | Retorna "S" | V |
| TC-L07 | Rank A (Power ≥ 3000) | Character com power ≥ 3000 e < 5000 | `progressionSystem.getCharacterRank(c)` | Retorna "A" | V |
| TC-L08 | Rank B (Power ≥ 1500) | Character com power ≥ 1500 e < 3000 | `progressionSystem.getCharacterRank(c)` | Retorna "B" | V |
| TC-L09 | Rank C (Power < 1500) | Character com power < 1500 | `progressionSystem.getCharacterRank(c)` | Retorna "C" | V |
| TC-L10 | Level up gasta commonCurrency | Player com commonCurrency=500 | Realizar level up | Custo deduzido do commonCurrency; se insuficiente, retorna false | V |

---

### 2.7 Sistema de Recursos e Missões Diárias (ResourceSystem)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-R01 | Completar missão "First Summon" | Nenhuma invocação anterior | Realizar 1 invocação | Missão "summon_1" completada; recompensa de 60 gems creditada | V |
| TC-R02 | Completar missão "Multi Summon" | Nenhuma invocação anterior | Realizar 10x invocação | Missão "summon_10" completada; recompensa de 80 gems | V |
| TC-R03 | Completar missão "Jackpot!" | Nenhuma invocação lendária | Obter personagem/item LENDÁRIO | Missão "get_legendary" completada; recompensa de 300 gems | V |
| TC-R04 | Completar missão "Rarity Hunter" | Sem pulls hoje | Obter 1 Common + 1 Rare + 1 Legendary no mesmo dia | Missão "all_3_rarities" completada; recompensa de 400 gems | V |
| TC-R05 | Completar missão "Constellation Master" | Nenhuma consteção hoje | Trigger constellation upgrade | Missão "constellation" completada; recompensa de 150 gems | V |
| TC-R06 | Completar missão "Gacha Addict" | 0 pulls hoje | Realizar 5 invocações | Missão "pulls_5" completada; recompensa de 80 gems | V |
| TC-R07 | Missão não completada — pulls insuficientes | 3 pulls hoje | Verificar missão "pulls_5" | Missão permanece incompleta | I |
| TC-R08 | Verificar missões diárias inicializadas | Novo jogador | Verificar lista de missões | 12 missões diárias inicializadas com status "incomplete" | V |

---

### 2.8 Sistema de Conquistas (Achievements)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-A01 | Desbloquear "First Summon" | Nenhuma conquista | Realizar 1 invocação | Conquista "First Summon" desbloqueada | V |
| TC-A02 | Desbloquear "Summon 10 characters" | 9 invocações | Realizar 10ª invocação | Conquista desbloqueada | V |
| TC-A03 | Desbloquear "Summon 50 characters" | 49 invocações | Realizar 50ª invocação | Conquista desbloqueada | L |
| TC-A04 | Desbloquear "Get first Legendary" | Nenhum Lendário | Obter primeiro Lendário | Conquista desbloqueada | V |
| TC-A05 | Desbloquear "First Constellation" | Nenhuma consteção | Trigger primeira consteção | Conquista desbloqueada | V |
| TC-A06 | Desbloquear "Fill inventory 50%" | Inventário com 99 itens | Adicionar 1 item (total: 100) | Conquista desbloqueada (100/200 = 50%) | L |
| TC-A07 | Desbloquear "Fill inventory 100%" | Inventário com 199 itens | Adicionar 1 item (total: 200) | Conquista desbloqueada | L |
| TC-A08 | Conquista não repete | "First Summon" já desbloqueada | Verificar após nova invocação | Conquista permanece desbloqueada, não duplica recompensa | V |

---

### 2.9 Economia de Moedas (Gems + Materiais)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-E01 | Saldo inicial de moedas | Novo jogador | Verificar moedas | commonCurrency = 500, rareCurrency = 120 | V |
| TC-E02 | Gastar commonCurrency | Player com 500 C | `spendCommonCurrency(200)` | commonCurrency = 300; retorna true | V |
| TC-E03 | Gastar commonCurrency insuficiente | Player com 100 C | `spendCommonCurrency(200)` | Retorna false; commonCurrency permanece 100 | I |
| TC-E04 | Gastar rareCurrency | Player com 120 R | `spendRareCurrency(50)` | rareCurrency = 70; retorna true | V |
| TC-E05 | Gastar rareCurrency insuficiente | Player com 30 R | `spendRareCurrency(50)` | Retorna false; rareCurrency permanece 30 | I |
| TC-E06 | Gastar valor negativo em commonCurrency | Player com 500 C | `spendCommonCurrency(-100)` | Retorna false (validação interna) | I |
| TC-E07 | Gastar ambas moedas juntas | Player com 500 C, 120 R | `spendUpgradeCurrencies(200, 50)` | C = 300, R = 70; retorna true | V |
| TC-E08 | Gastar ambas — uma insuficiente | Player com 500 C, 20 R | `spendUpgradeCurrencies(200, 50)` | Retorna false; C e R permanecem inalterados | I |
| TC-E09 | Adicionar commonCurrency negativa | Player com 500 C | `addCommonCurrency(-50)` | C = 500 (Math.max(0, -50) = 0, somado = 500) | L |
| TC-E10 | Gems nunca ficam negativos via spendGems | Player com 100 gems | `spendGems(200)` | Lança `InsufficientGemsException`; gems permanece 100 | V |

---

### 2.10 Tipos de Personagens e Itens

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-T01 | Hero possui weaponType e consteção | Criar Hero | `new Hero("Goku", LEGENDARY, FIRE, 1, SWORD)` | Hero com weaponType=SWORD, consteção=0 | V |
| TC-T02 | Support possui healType e healMultiplier | Criar Support | `new Support("Sakura", RARE, ICE, 1, "HoT", 2.0)` | Support com healType="HoT", healMultiplier=2.0 | V |
| TC-T03 | Antagonist possui bossTitle e isRaidBoss | Criar Antagonist | `new Antagonist("Frieza", LEGENDARY, FIRE, 90, "The Destroyer", true)` | Antagonist com bossTitle="The Destroyer", isRaidBoss=true | V |
| TC-T04 | Antagonist lendário é sempre Raid Boss | Invocar Antagonist Lendário | `new Antagonist("Madara", LEGENDARY, ..., true)` | isRaidBoss=true para raridade LEGENDARY | V |
| TC-T05 | Weapon possui weaponType, baseDamage, passiveSkill | Criar Weapon | `new Weapon("Dragon Slayer", RARE, 10, SWORD, 150, "Critical Boost")` | Weapon com todos os atributos | V |
| TC-T06 | Artifact e Relic são InventoryItems | Verificar herança | `artifact instanceof InventoryItem` | true | V |
| TC-T07 | Character generateReport inclui dados específicos | Hero nível 20 | `hero.generateReport()` | Inclui nome, raridade, elemento, nível, weapon, consteção | V |
| TC-T08 | describeYourself funciona para todos os tipos | Qualquer entidade | `entity.describeYourself()` | Retorna descrição textual com dados formatados | V |

---

### 2.11 Tratamento de Exceções

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-X01 | InsufficientGemsException — categoria e código | Gems insuficientes | Capturar exceção | getErrorCategory() = "ECONOMY"; getErrorCode() = 1001 | V |
| TC-X02 | InsufficientGemsException — dados da exceção | Tentar gastar 200 gems tendo 50 | Capturar exceção | getRequired() = 200; getAvailable() = 50 | V |
| TC-X03 | InventoryFullException — categoria e código | Inventário cheio | Capturar exceção | getErrorCategory() = "INVENTORY"; getErrorCode() = 1002; getCapacity() = 200 | V |
| TC-X04 | DuplicateCharacterException — categoria e código | Personagem duplicado | Capturar exceção | getErrorCategory() = "CONSTELLATION"; getErrorCode() = 1003; getCharacterName() = nome do personagem | V |
| TC-X05 | GachaBaseException — errorTime registrado | Qualquer exceção | Capturar exceção | getErrorTime() retorna LocalDateTime válido (não nulo) | V |
| TC-X06 | Exceções não travam o servidor | Servidor rodando | Enviar comando inválido várias vezes | Servidor continua operacional; cliente recebe mensagem de erro | V |
| TC-X07 | Bloco try-catch em GameEngine protege o fluxo | Invocação com inventário cheio | Realizar invocação | Erro interceptado; resultado do gacha ainda é exibido; servidor não trava | V |

---

### 2.12 Comunicação Cliente-Servidor (TCP Sockets)

| TC-ID | Descrição | Pré-condições | Entrada / Passos | Resultado Esperado | Tipo |
|-------|-----------|---------------|------------------|--------------------|------|
| TC-S01 | Conexão do cliente ao servidor | Servidor rodando | Iniciar `GachaClientApp` | Cliente conecta ao servidor; tela de apresentação exibida | V |
| TC-S02 | Servidor rejeita conexões simultâneas problemáticas | Servidor rodando | 2 clientes conectam ao mesmo tempo | Servidor gerencia múltiplas conexões sem travar | V |
| TC-S03 | Desconexão do cliente não derruba o servidor | Servidor com 1 cliente | Cliente fecha abruptamente | Servidor continua rodando; sem exception visível | V |
| TC-S04 | Comando EXIT encerra conexão limpa | Cliente conectado | Enviar comando EXIT | Conexão encerrada normalmente; servidor libera recursos | V |
| TC-S05 | Comando HELP lista opções | Cliente conectado | Enviar comando HELP | Lista todos os comandos disponíveis com descrição | V |
| TC-S06 | Protocolo serializa request/response | Cliente conectado | Enviar qualquer comando | `GameRequest` e `GameResponse` serializados via ObjectOutputStream/ObjectInputStream | V |

---

## 3. Resumo dos Testes

| Categoria | Válidos | Inválidos | Limite | Total |
|-----------|---------|-----------|--------|-------|
| 2.1 Player | 4 | 1 | 1 | 6 |
| 2.2 Gacha (Summon) | 4 | 2 | 4 | 10 |
| 2.3 Pity System | 3 | 0 | 4 | 7 |
| 2.4 Inventário | 5 | 2 | 2 | 9 |
| 2.5 Constelação | 3 | 0 | 2 | 5 |
| 2.6 Progressão | 5 | 1 | 1 | 7 |
| 2.7 Missões Diárias | 6 | 1 | 0 | 7 |
| 2.8 Conquistas | 6 | 0 | 2 | 8 |
| 2.9 Economia | 4 | 4 | 2 | 10 |
| 2.10 Tipos de Entidades | 8 | 0 | 0 | 8 |
| 2.11 Exceções | 7 | 0 | 0 | 7 |
| 2.12 Comunicação | 6 | 0 | 0 | 6 |
| **TOTAL** | **61** | **11** | **18** | **90** |

---

## 4. Considerações Finais

Este roteiro cobre **90 casos de teste** distribuídos entre entradas **válidas** (67.8%), **inválidas** (12.2%) e de **limite** (20%), garantindo cobertura abrangente dos requisitos funcionais do Anime RPG Gacha System.

### Pontos Fortes Identificados pelo Teste de Caixa-Preta:
- **Encapsulamento robusto:** Gems nunca ficam negativos; inventário respeita capacidade máxima
- **Pity System garantido:** Jogador SEMPRE recebe Lendário no pull 90 (Hard Pity)
- **Tratamento de exceções:** Hierarquia customizada com códigos e categorias claras
- **Constelação automática:** Duplicatas não quebram o jogo — convertem em upgrade
- **Progressão consistente:** Fórmulas de poder e rank bem definidas

### Áreas de Atenção:
- Construtor de `Player` não valida gems negativos
- `levelUp()` aceita valores negativos (pode reduzir nível)
- `addGems()` aceita valores negativos (reduz saldo)
- Constelação pode exceder C6 sem validação explícita
