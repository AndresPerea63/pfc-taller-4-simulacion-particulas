# Ejemplo de informe de corrección

Fundamentos de Programación Funcional y Concurrente.
Documento realizado por el docente Juan Francisco Díaz.

## 1. Argumentar la corrección de programas recursivos

Sea $f : A \to B$ una función, y $A$ un conjunto definido recursivamente
(recordar la definición de Matemáticas Discretas I), como por ejemplo los
naturales o las listas.

Sea $P_f$ un programa recursivo (lineal o en árbol) desarrollado en Scala (o en
cualquier lenguaje de programación) hecho para calcular $f$:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  ...
}
```

¿Cómo argumentar que $P_f(a)$ siempre devuelve $f(a)$ como respuesta? Es decir,
¿cómo argumentar que $P_f$ es correcto con respecto a su especificación?

La respuesta es sencilla: demostrando el siguiente teorema.

```math
\forall a \in A : P_f(a) == f(a)
```

Cuando uno tiene que demostrar que algo se cumple para todos los elementos de
un conjunto definido recursivamente, es natural usar inducción estructural. En
términos prácticos, esto significa demostrar que:

- Para cada valor básico $a$ de $A$, se tiene que $P_f(a) == f(a)$.
- Para cada valor $a \in A$ construido recursivamente a partir de otro(s)
  valor(es) $a' \in A$, se tiene que
  $P_f(a') == f(a') \rightarrow P_f(a) == f(a)$. (Esta es la hipótesis de
  inducción).

### Ejemplo: factorial recursivo

Sea $f : \mathbb{N} \to \mathbb{N}$ la función que calcula el factorial de un
número natural, es decir, $f(n) = n!$. Y sea $P_f$ el siguiente programa en
Scala:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  if (n == 0) 1 else n * Pf(n - 1)
}
```

Vamos a demostrar que $\forall n \in \mathbb{N} : P_f(n) == n!$

**Caso base:** $n = 0$

```math
P_f(0) \rightarrow \text{if } (0 == 0)\ 1 \text{ else } 0 \ast P_f(-1) \rightarrow 1
```

Por otro lado, $f(0) = 0! = 1$. Entonces $P_f(0) == f(0)$.

**Caso de inducción:** $n = k + 1$, $k \geq 0$. Hay que demostrar:
$P_f(k) == f(k) \rightarrow P_f(k + 1) == f(k + 1)$

```math
P_f(k+1) \rightarrow \text{if } (k+1 == 0)\ 1 \text{ else } (k+1) \ast P_f(k) \rightarrow (k+1) \ast P_f(k)
```

Usando la hipótesis de inducción (HI):

```math
\rightarrow (k+1) \ast k! = (k+1)!
```

Por lo tanto, $P_f(k + 1) == f(k + 1)$.

Concluimos por inducción que $\forall n \in \mathbb{N} : P_f(n) == n!$

### Ejemplo: el máximo de una lista

Sea $f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula el
máximo de una lista de enteros positivos, no vacía. Y sea $P_f$ el siguiente
programa en Scala:

```scala
def maxLin(l: List[Int]): Int = {
  if (l.tail.isEmpty) l.head
  else math.max(maxLin(l.tail), l.head)
}
```

Demostraremos que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

**Caso base:** $n = 1$

```math
P_f(\text{List}(a_1)) \rightarrow \text{if } \text{List}(a_1).\text{tail.isEmpty then } \text{List}(a_1).\text{head else } \ldots \rightarrow \text{List}(a_1).\text{head} \rightarrow a_1
```

Por otro lado, $f(\text{List}(a_1)) = a_1$. Entonces
$P_f(\text{List}(a_1)) == f(\text{List}(a_1))$.

**Caso de inducción:** $n = k + 1$, $k \geq 1$. Se debe demostrar:

```math
P_f(\text{List}(b_1, b_2, \ldots, b_k)) == f(\text{List}(b_1, b_2, \ldots, b_k)) \rightarrow P_f(\text{List}(a_1, a_2, \ldots, a_{k+1})) == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))
```

Empecemos por calcular qué devuelve $P_f$ usando el modelo de sustitución:

```math
P_f(L) \rightarrow \text{if } L.\text{tail.isEmpty then } L.\text{head else math.max}(P_f(L.\text{tail}), L.\text{head})
```

```math
\rightarrow \text{math.max}(P_f(\text{List}(a_2, \ldots, a_{k+1})), a_1)
```

Sea $b = P_f(\text{List}(a_2, \ldots, a_{k+1}))$; por la hipótesis de
inducción, $b = f(\text{List}(a_2, \ldots, a_{k+1}))$. Hay dos posibilidades:

- Si $\text{math.max}(b, a_1) = b$, entonces $b \geq a_1$ y
 $b == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.
- Si $\text{math.max}(b, a_1) = a_1$, entonces $a_1 \geq b$ y
 $a_1 == f(\text{List}(a_1, a_2, \ldots, a_{k+1}))$.

Por lo tanto, $P_f(L) == f(L)$.

Concluimos por inducción que:

```math
\forall n \in \mathbb{N} \setminus \{0\} : P_f(\text{List}(a_1, a_2, \ldots, a_n)) == f(\text{List}(a_1, a_2, \ldots, a_n))
```

## 2. Argumentar la corrección de programas iterativos

Para argumentar la corrección de programas iterativos, se debe formalizar cómo
es la iteración. Esto implica definir:

- Cómo se representa un estado de la iteración, $s$.
- Cuál es el estado inicial, $s_0$.
- Cuál es el estado final (o cómo se reconoce que un estado es final): $s_f$.
- Qué condición (o predicado) cumple todo estado: $\text{Inv}(s)$ (invariante
  de la iteración).
- El mecanismo para pasar de un estado al siguiente: $\text{transformar}(s)$.
  Si $s_i$ es el estado $i$, entonces $\text{transformar}(s_i) = s_{i+1}$.

Un programa iterativo tiene la siguiente forma:

```scala
def Pf(a: A): B = { // Pf recibe a de tipo A, y devuelve f(a) de tipo B
  def Pf_iter(s: Estado): B =
    if (esFinal(s)) respuesta(s) else Pf_iter(transformar(s))
  Pf_iter(s0)
}
```

Demostración de corrección:

- $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.
- Si $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:
  el nuevo estado cumple la condición invariante si el estado anterior la
  cumplía.
- De lo anterior se concluye $\text{Inv}(s_f)$, es decir, el estado final
  cumple la condición invariante. Luego,
  $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$.
- Finalmente, demostrar que siempre se llega al estado final $s_f$. Esto
  implica que
  $P_f(a) == \text{iter}(s_0) == \text{respuesta}(s_f) == f(a)$.

### Ejemplo: factorial iterativo

Considere el siguiente programa iterativo en Scala para calcular la función
factorial:

```scala
def Pf(n: Int): Int = { // Pf recibe n de tipo Int, y devuelve n! de tipo Int
  def Pf_iter(i: Int, n: Int, ac: Int): Int =
    if (i > n) ac else Pf_iter(i + 1, n, i * ac)
  Pf_iter(1, n, 1)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (i, n, ac)$.
- El estado inicial es $s_0 = (1, n, 1)$.
- $(i, n, ac)$ es final si $i > n$, o lo que es lo mismo, si $i = n + 1$.
- La invariante de ciclo es
  $\text{Inv}(i, n, ac) \equiv i \leq n + 1 \land ac = (i-1)!$.
  La invariante de ciclo es una relación que SIEMPRE se cumple en el ciclo.
- $\text{transformar}((i, n, ac)) = (i+1, n, i \ast ac)$.

Ahora, demostramos los puntos mencionados:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (1, n, 1) \implies 1 \leq n + 1 \land 1 = 0!
```

**2.** La invariante se mantiene con la transformación de estados,
$(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$:

1. Primer cambio, $i = i + 1$, lo que implica $ac = ((i+1) - 1)! = i!$.
2. Segundo cambio, $ac = i \ast ac$, entonces $ac = (i - 1)! \ast i = i!$.
3. Como se puede ver en ambos cambios indicados en la transformación, la
   invariante se mantiene.

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
(n + 1 \leq n + 1) \land ac = ((n+1)-1)! \rightarrow ac == n!
```

**4.** En cada paso, la componente $i$ del estado incrementa, acercándose a $n+1$.
Después de $n$ iteraciones, se alcanza $n+1$.

Esto implica que $P_f(n) == \text{iter}(1, n, 1) == n!$

### Ejemplo: el máximo de una lista

Se desea calcular el máximo de una lista de enteros positivos, no vacía. Sea
$f : \text{List}[\mathbb{N}] \to \mathbb{N}$ la función que calcula ese valor.
Y sea $P_f$ el siguiente programa en Scala:

```scala
def maxIt(l: List[Int]): Int = {
  def maxAux(max: Int, l: List[Int]): Int = {
    if (l.isEmpty) max
    else maxAux(math.max(max, l.head), l.tail)
  }
  maxAux(l.head, l.tail)
}
```

Este programa implementa el siguiente proceso iterativo:

- Un estado $s = (max, l)$ donde $l = \text{List}(a_i, a_{i+1}, \ldots, a_k)$
  es una cola de $L$.
- El estado inicial es
  $s_0 = (L.\text{head}, L.\text{tail}) = (a_1, \text{List}(a_2, \ldots, a_k))$.
- $s = (max, l)$ es final si $l$ es vacía.
- $\text{Inv}(max, l) \equiv l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))$.
- $\text{transformar}((max, l)) = (nmax, l.\text{tail})$ donde $nmax = max$ si
  $max \geq l.\text{head}$, y $nmax = l.\text{head}$ si no.

Demostración de los puntos:

**1.** $\text{Inv}(s_0)$: el estado inicial cumple la condición invariante.

```math
s_0 = (a_1, \text{List}(a_2, \ldots, a_k)) \implies a_1 = f(\text{List}(a_1))
```

**2.** $(s_i \neq s_f \land \text{Inv}(s_i)) \rightarrow \text{Inv}(\text{transformar}(s_i))$

```math
\neg\, l.\text{isEmpty} \land l = \text{List}(a_i, a_{i+1}, \ldots, a_k) \land max = f(\text{List}(a_1, a_2, \ldots, a_{i-1}))
```

```math
\rightarrow l.\text{tail} = \text{List}(a_{i+1}, \ldots, a_k) \land nmax = f(\text{List}(a_1, \ldots, a_i))
```

**3.** $\text{Inv}(s_f) \rightarrow \text{respuesta}(s_f) == f(a)$

```math
\text{Inv}((max, \text{List}())) \rightarrow max = f(\text{List}(a_1, \ldots, a_k))
```

**4.** En cada paso, la lista $l$ se reduce, acercándose a ser vacía. Después de
$k$ iteraciones, $l = \text{List}()$.

Esto implica que $P_f(L) == \text{maxAux}(L.\text{head}, L.\text{tail}) == f(L)$

# Informe de corrección — Taller 4: simulación de partículas
 
Este informe argumenta que cada función de los puntos 1 y 2 hace lo que pide
el enunciado. Se piden dos cosas en concreto: que partir el sistema en
segmentos no pierde ni repite partículas, y que la operación con que se
combinan los resultados parciales es asociativa. También se argumenta que la
versión paralela da el mismo resultado que la secuencial.
 
## 0. Notación
 
Un sistema de $n$ partículas es una secuencia
$S = (p_0, p_1, \ldots, p_{n-1})$, y cada partícula es una tupla
$p_i = (x_i, v_i, m_i)$ con posición $x_i$, velocidad $v_i$ y masa $m_i$.
Para $0 \leq a \leq b \leq n$, el segmento $S[a, b)$ es la secuencia
$(p_a, p_{a+1}, \ldots, p_{b-1})$, que tiene $b - a$ elementos.
 
Se usa $++$ para la concatenación de secuencias y $\text{mov}(p)$ para el
resultado de `mover(p, dt)`.
 
---
 
## 1. Punto 1: un paso de la simulación
 
### 1.1 Especificación
 
El enunciado pide que el sistema después de un paso de tiempo $dt$ sea:
 
```math
\text{paso}(S, dt) = (q_0, q_1, \ldots, q_{n-1}) \quad\text{con}\quad q_i = (x_i + v_i \cdot dt,\ v_i,\ m_i)
```
 
### 1.2 `mover` es correcta
 
```scala
def mover(p: Particula, dt: Double): Particula =
  Particula(p.pos + p.vel * dt, p.vel, p.masa)
```
 
Por sustitución, `mover(p, dt)` devuelve la tupla
$(x + v \cdot dt,\ v,\ m)$, que es exactamente $q_i$ cuando $p = p_i$. La
velocidad y la masa se copian sin cambios. Además, `mover` no usa estado que
cambie: para los mismos $p$ y $dt$ siempre da el mismo resultado.
 
### 1.3 `pasoSegmento` es correcta
 
```scala
def pasoSegmento(s: Sistema, dt: Double, desde: Int, hasta: Int): Sistema =
  s.slice(desde, hasta).map(p => mover(p, dt))
```
 
Para $0 \leq a \leq b \leq n$, `slice(a, b)` devuelve $S[a, b)$ y `map` aplica
$\text{mov}$ a cada elemento conservando el orden. Entonces:
 
```math
\text{pasoSegmento}(S, dt, a, b) = (\text{mov}(p_a), \text{mov}(p_{a+1}), \ldots, \text{mov}(p_{b-1})) = (q_a, \ldots, q_{b-1})
```
 
Si $a = b$, el segmento es vacío y el resultado es la secuencia vacía.
 
### 1.4 `pasoSecuencial` es correcta
 
```scala
def pasoSecuencial(s: Sistema, dt: Double): Sistema =
  pasoSegmento(s, dt, 0, s.length)
```
 
Es `pasoSegmento` con $a = 0$ y $b = n$. Por 1.3:
 
```math
\text{pasoSecuencial}(S, dt) = (q_0, \ldots, q_{n-1}) = \text{paso}(S, dt)
```
 
### 1.5 `pasoParalelo` es correcta
 
```scala
def pasoParalelo(s: Sistema, dt: Double, umbral: Int): Sistema = {
  val limite = Math.max(umbral, 1)
 
  def resolver(desde: Int, hasta: Int): Sistema =
    if (hasta - desde <= limite) pasoSegmento(s, dt, desde, hasta)
    else {
      val mitad = desde + (hasta - desde) / 2
      val (izquierda, derecha) = parallel(resolver(desde, mitad), resolver(mitad, hasta))
      izquierda ++ derecha
    }
 
  resolver(0, s.length)
}
```
 
Sea $L = \max(\text{umbral}, 1)$, así que $L \geq 1$.
 
**Teorema.** Para todo $0 \leq a \leq b \leq n$:
 
```math
\text{resolver}(a, b) = \text{pasoSegmento}(S, dt, a, b)
```
 
Se demuestra por **inducción fuerte sobre el tamaño del tramo** $k = b - a$.
 
**Caso base: $k \leq L$.** La condición `hasta - desde <= limite` se cumple y
`resolver(a, b)` devuelve directamente `pasoSegmento(s, dt, a, b)`. Esto cubre
también $k = 0$, el tramo vacío.
 
**Caso de inducción: $k > L$.** Se supone, como hipótesis de inducción (HI),
que el teorema vale para todo tramo de tamaño menor que $k$. Como $k > L \geq 1$,
se tiene $k \geq 2$. La mitad es $m = a + \lfloor k / 2 \rfloor$. Como
$k \geq 2$, entonces $1 \leq \lfloor k / 2 \rfloor < k$, y por lo tanto:
 
```math
a < m < b
```
 
Los dos tramos nuevos tienen tamaños $m - a = \lfloor k/2 \rfloor < k$ y
$b - m = k - \lfloor k/2 \rfloor < k$. Es decir, ambos son menores que $k$ y la
HI se aplica a los dos:
 
```math
\text{resolver}(a, m) = (q_a, \ldots, q_{m-1}) \qquad \text{resolver}(m, b) = (q_m, \ldots, q_{b-1})
```
 
Como las funciones no usan estado que cambie, `parallel(x, y)` devuelve el
mismo par $(x, y)$ que se obtendría evaluando $x$ y luego $y$. Entonces:
 
```math
\text{resolver}(a, b) = (q_a, \ldots, q_{m-1}) \mathbin{++} (q_m, \ldots, q_{b-1}) = (q_a, \ldots, q_{b-1}) = \text{pasoSegmento}(S, dt, a, b)
```
 
Concluimos por inducción que el teorema vale para todo tramo. $\blacksquare$
 
**Corolario.** `pasoParalelo` llama a `resolver(0, n)`, que por el teorema es
`pasoSegmento(S, dt, 0, n)`, es decir, $\text{paso}(S, dt)$. Para todo sistema,
todo $dt$ y todo umbral:
 
```math
\text{pasoParalelo}(S, dt, \text{umbral}) = \text{pasoSecuencial}(S, dt)
```
 
La igualdad es **exacta**, no aproximada: cada partícula se mueve exactamente
una vez con las mismas operaciones, y solo cambia dónde se calcula.
 
### 1.6 Partir el sistema no pierde ni repite partículas
 
Cuando $k > L$, el tramo $[a, b)$ se parte en $[a, m)$ y $[m, b)$ con
$a < m < b$. Estos dos tramos cumplen:
 
```math
[a, m) \cup [m, b) = [a, b) \qquad\text{y}\qquad [a, m) \cap [m, b) = \emptyset
```
 
- **No se pierde ninguna:** todo índice $i$ con $a \leq i < b$ cumple
  $i < m$ (está en el primer tramo) o $i \geq m$ (está en el segundo).
- **No se repite ninguna:** ningún índice cumple a la vez $i < m$ e $i \geq m$.
Las dos mitades se parten a su vez con el mismo argumento, así que las hojas de
la recursión son tramos disjuntos cuya unión es $[0, n)$: cada partícula cae en
exactamente una hoja y se mueve exactamente una vez.
 
**La recursión termina.** Por $a < m < b$, cada tramo nuevo tiene estrictamente
menos elementos que el tramo del que sale. Un tamaño que baja siempre y no puede
ser negativo llega en algún momento a $k \leq L$, que es el caso base.
 
**Por qué $L \geq 1$.** Sin el $\max$, un umbral $0$ dejaría pasar un tramo de
un elemento al caso recursivo, con $k = 1$ y $m = a$. Entonces el tramo
$[m, b) = [a, b)$ sería igual al original y la recursión no terminaría. Con
$L \geq 1$, el caso recursivo solo ocurre con $k \geq 2$.
 
### 1.7 La operación que combina los resultados es asociativa
 
En `pasoParalelo` los resultados parciales se combinan con la concatenación
$++$. Es **asociativa**: para secuencias $A$, $B$, $C$,
 
```math
(A \mathbin{++} B) \mathbin{++} C = A \mathbin{++} (B \mathbin{++} C)
```
 
Ambos lados son la secuencia que tiene los elementos de $A$, luego los de $B$ y
luego los de $C$, en ese orden. Formalmente, el elemento en la posición $i$ es
el mismo en los dos lados: si $i < |A|$ es $A_i$; si $|A| \leq i < |A| + |B|$ es
$B_{i - |A|}$; y si no es $C_{i - |A| - |B|}$.
 
Esto importa porque la forma del árbol de llamados depende del umbral: con un
umbral distinto, los mismos elementos se agrupan de otra manera. Como $++$ es
asociativa, da igual cómo se agrupen: el resultado final es siempre la misma
secuencia. La secuencia vacía es el elemento neutro:
$() \mathbin{++} A = A \mathbin{++} () = A$. Notar que $++$ **no** es
conmutativa, y no tiene que serlo: el orden se conserva porque `izquierda`
siempre va antes que `derecha`.
 
---
 
## 2. Punto 2: magnitudes globales
 
### 2.1 `energiaSecuencial`
 
**Especificación:**
 
```math
E(S) = \sum_{i=0}^{n-1} \tfrac{1}{2}\, m_i\, \lVert v_i \rVert^2
```
 
```scala
def energiaSecuencial(s: Sistema): Double = {
  def energiaDe(p: Particula): Double =
    0.5 * p.masa * p.vel.norma * p.vel.norma
 
  s.map(p => energiaDe(p)).sum
}
```
 
`energiaDe(p_i)` es $\tfrac{1}{2} \cdot m_i \cdot \lVert v_i \rVert \cdot \lVert v_i \rVert = \tfrac{1}{2}\, m_i \lVert v_i \rVert^2$,
porque `norma` es $\lVert v_i \rVert$. Luego `map` produce la secuencia
$(e_0, \ldots, e_{n-1})$ con $e_i = \tfrac{1}{2} m_i \lVert v_i \rVert^2$ y `sum`
la suma, que es $E(S)$. Para $n = 0$ la suma vacía es $0$, que es lo que pide el
enunciado. Ejemplo: con $m = 2$ y $v = (3, 4)$, $\lVert v \rVert = 5$ y
$E = \tfrac{1}{2} \cdot 2 \cdot 25 = 25$.
 
### 2.2 `masaTotal`
 
**Especificación:** $M(S) = \sum_{i=0}^{n-1} m_i$.
 
```scala
def masaTotal(s: Sistema): Double =
  s.map(p => p.masa).sum
```
 
`map` produce $(m_0, \ldots, m_{n-1})$ y `sum` la suma, que es $M(S)$. Para
$n = 0$ da $0$.
 
### 2.3 `centroDeMasaSecuencial`
 
**Especificación:**
 
```math
C(S) = \begin{cases} \dfrac{1}{M(S)} \displaystyle\sum_{i=0}^{n-1} m_i\, x_i & \text{si } M(S) \neq 0 \\[2ex] (0, 0) & \text{si } M(S) = 0 \end{cases}
```
 
```scala
def centroDeMasaSecuencial(s: Sistema): Vector2 = {
  val origen = Vector2(0.0, 0.0)
  val masa = masaTotal(s)
 
  if (masa == 0.0) origen
  else {
    val sumaPonderada = s
      .map(p => p.pos * p.masa)
      .foldLeft(origen)((acumulado, v) => acumulado + v)
    sumaPonderada * (1.0 / masa)
  }
}
```
 
- **Rama `masa == 0.0`:** por 2.2, `masa` es $M(S)$, así que la función
  devuelve el origen exactamente cuando la especificación lo pide. Esto cubre
  el sistema vacío y el sistema con todas las masas en cero.
- **Rama `masa != 0.0`:** hay que demostrar que `sumaPonderada` es
  $\sum_i m_i x_i$. Como $x_i \cdot m_i = m_i \cdot x_i$, `map` produce
  $(m_0 x_0, \ldots, m_{n-1} x_{n-1})$ y falta ver que `foldLeft` las suma.
  Multiplicar por $1/M(S)$ es entonces dividir entre $M(S)$.
`foldLeft` es un proceso iterativo, así que se argumenta con una invariante.
Sea $w_i = m_i x_i$.
 
- **Estado:** $s = (\text{acc}, l)$, donde `acc` es el acumulado y $l$ es la
  parte de la lista que falta por sumar.
- **Estado inicial:** $s_0 = ((0, 0), (w_0, \ldots, w_{n-1}))$.
- **Estado final:** $l$ es vacía.
- **Invariante:** $l = (w_j, \ldots, w_{n-1}) \land \text{acc} = \sum_{i<j} w_i$.
- **Transformación:** $\text{transformar}((\text{acc}, l)) = (\text{acc} + l.\text{head},\ l.\text{tail})$.
Demostración:
 
**1.** $\text{Inv}(s_0)$: con $j = 0$, $l$ es la lista completa y
$\text{acc} = (0, 0)$, que es la suma vacía.
 
**2.** Si se cumple $\text{Inv}(s)$ y $l$ no es vacía, entonces tras la
transformación $l.\text{tail} = (w_{j+1}, \ldots, w_{n-1})$ y
$\text{acc} + w_j = \sum_{i<j} w_i + w_j = \sum_{i<j+1} w_i$. La invariante se
mantiene con $j + 1$.
 
**3.** En el estado final, $l$ es vacía, así que $j = n$ y
$\text{acc} = \sum_{i<n} w_i = \sum_i m_i x_i$.
 
**4.** En cada paso $l$ pierde un elemento, así que después de $n$ pasos queda
vacía y se llega al estado final.
 
Por lo tanto `sumaPonderada` $= \sum_i m_i x_i$ y la función devuelve
$\frac{1}{M(S)} \sum_i m_i x_i = C(S)$. $\blacksquare$
 
Ejemplo: con $(m, x) = (1, (1,1))$ y $(3, (4,4))$, la suma ponderada es
$(1,1) + (12,12) = (13,13)$, la masa es $4$ y $C = (13,13)/4 = (3.25, 3.25)$.
 
### 2.4 Operaciones que combinan en el punto 2
 
En el punto 2 los resultados se combinan con la suma de números
($\text{sum}$) y la suma de vectores ($\text{foldLeft}$ con $+$). Ambas son
asociativas y conmutativas, y tienen elemento neutro ($0$ y $(0, 0)$):
 
```math
(a + b) + c = a + (b + c) \qquad (u + v) + w = u + (v + w)
```
 
con $u + v = (u_x + v_x,\ u_y + v_y)$, que hereda la propiedad de la suma de
números componente a componente. Esto es lo que permite, en el punto 3,
repartir el cálculo en tramos y combinar los resultados parciales sin cambiar
el valor.
 
**Advertencia sobre los números de punto flotante.** En $\mathbb{R}$ la suma
es asociativa, pero con `Double` solo lo es de forma aproximada: sumar en otro
orden puede cambiar los últimos decimales. Por eso las pruebas de las
magnitudes comparan con una tolerancia pequeña y no con igualdad exacta. En el
punto 1 esto no ocurre, porque no se suma nada entre partículas: cada una se
calcula sola.
 
---
 
## 3. Resumen de las argumentaciones
 
| Afirmación | Cómo se argumenta |
|---|---|
| `mover`, `pasoSegmento`, `pasoSecuencial` cumplen la especificación | Sustitución y definición de `slice` y `map` |
| `pasoParalelo` $=$ `pasoSecuencial` | Inducción fuerte sobre el tamaño del tramo (sección 1.5) |
| Partir no pierde ni repite partículas | Los tramos $[a, m)$ y $[m, b)$ son disjuntos y su unión es $[a, b)$ (sección 1.6) |
| La recursión termina | Los tamaños bajan estrictamente porque $a < m < b$ (sección 1.6) |
| $++$ es asociativa | Mismo elemento en cada posición en ambos lados (sección 1.7) |
| `energiaSecuencial`, `masaTotal` cumplen la especificación | Definición de `map` y `sum` (secciones 2.1 y 2.2) |
| `centroDeMasaSecuencial` cumple la especificación | Invariante de `foldLeft` (sección 2.3) |
| La suma de números y de vectores es asociativa | Propiedad de la suma en $\mathbb{R}$, componente a componente (sección 2.4) |
 