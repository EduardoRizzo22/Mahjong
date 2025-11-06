## Documentação Adicionada

Foi implementada documentação JavaDoc completa nas classes principais do projeto.

### Classes Documentadas

#### 1. **Board.java** - Gerenciamento do Jogo

#### 2. **Tile.java** - Representação de Peças

#### 3. **Action.java** - Ações do Jogo

#### 4. **AI.java** - Inteligência Artificial

#### 5. **comGUI.java** - Comunicação com Interface

---

## Como Gerar a Documentação HTML

### Comando para Gerar Documentação

```bash
javadoc -d docs -encoding UTF-8 -charset UTF-8 -author -version src/*.java
```

### Visualizar a Documentação

1. **Via Navegador:**

   ```bash
   open docs/index.html
   ```

2. **Ou acesse diretamente:**
   ```
   /Mahjong/docs/index.html
   ```

---

## Padrão JavaDoc Utilizado

### Tags Utilizadas

- `@author` - Autoria do código
- `@version` - Versão da classe
- `@since` - Desde qual versão existe
- `@param` - Parâmetros de métodos
- `@return` - Valor de retorno
- `@throws` - Exceções lançadas

### Exemplo de Documentação

```java
/**
 * Representa uma peça (tile) do jogo Mahjong.
 *
 * <p>Cada peça possui:
 * <ul>
 *   <li><b>Naipe (suit)</b>: 0=萬, 1=筒, 2=條, 3=字</li>
 *   <li><b>Valor (value)</b>: 0-8 para numéricos, 0-6 para honras</li>
 * </ul>
 * </p>
 */
public class Tile implements Comparable<Tile>{
    // ...
}
```
