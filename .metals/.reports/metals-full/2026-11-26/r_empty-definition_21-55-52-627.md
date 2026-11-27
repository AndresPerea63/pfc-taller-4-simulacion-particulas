error id: file:///C:/Users/Diego%20Fernando/Documents/GitHub/pfc-taller-4-simulacion-particulas/app/src/test/scala/taller/SimulacionParticulasTest.scala:
file:///C:/Users/Diego%20Fernando/Documents/GitHub/pfc-taller-4-simulacion-particulas/app/src/test/scala/taller/SimulacionParticulasTest.scala
empty definition using pc, found symbol in pc: 
empty definition using semanticdb
empty definition using fallback
non-local guesses:
	 -sim/SimulacionParticulas#
	 -SimulacionParticulas#
	 -scala/Predef.SimulacionParticulas#
offset: 465
uri: file:///C:/Users/Diego%20Fernando/Documents/GitHub/pfc-taller-4-simulacion-particulas/app/src/test/scala/taller/SimulacionParticulasTest.scala
text:
```scala
package taller

import org.scalatest.funsuite.AnyFunSuite
import org.junit.runner.RunWith
import org.scalatestplus.junit.JUnitRunner
import scala.util.Random

/**
 * Las pruebas no miden tiempos: un tiempo depende de la máquina y de lo que
 * esté corriendo al lado, así que una prueba que lo compare fallaría sin que
 * nada esté mal.
 */
@RunWith(classOf[JUnitRunner])
class SimulacionParticulasTest extends AnyFunSuite {

  val sim = new Simulacio@@nParticulas()
  import sim._

  val eps = 1e-9

  val p = Particula(Vector2(0, 0), Vector2(1, 2), 1.0)

  val s: Sistema = Array(
    Particula(Vector2(0, 0), Vector2(3, 4), 2.0), // norma 5
    Particula(Vector2(4, 0), Vector2(0, 0), 2.0))

  val vacio: Sistema = Array()

  val azar = new Random(7)
  val grande: Sistema = Array.fill(1000)(Particula(
    Vector2(azar.nextDouble() * 10, azar.nextDouble() * 10),
    Vector2(azar.nextDouble() * 2, azar.nextDouble() * 2),
    azar.nextDouble() * 5 + 0.1))

  def cerca(a: Double, b: Double): Boolean = Math.abs(a - b) < 1e-6

  def cerca(a: Vector2, b: Vector2): Boolean =
    Math.abs(a.x - b.x) < 1e-6 && Math.abs(a.y - b.y) < 1e-6

  // Punto 1 -------------------------------------------------------------------

  test("Punto 1: con dt negativo la partícula se mueve hacia atrás") {
    val q = Particula(Vector2(10, 10), Vector2(2, 3), 1.0)
    assert(mover(q, -1.0).pos == Vector2(8.0, 7.0))
  }

  test("Punto 1: mover no cambia la velocidad ni la masa") {
    val q = Particula(Vector2(1, 1), Vector2(5, -2), 7.5)
    val r = mover(q, 3.0)
    assert(r.vel == Vector2(5, -2) && r.masa == 7.5)
  }

  test("Punto 1: pasoSegmento da lo mismo que el trozo del paso secuencial") {
    val sistema = sistemaDe(10)
    val tramo = pasoSegmento(sistema, 1.0, 3, 6)
    val esperado = pasoSecuencial(sistema, 1.0).slice(3, 6)
    assert(tramo.length == 3 && tramo.toList == esperado.toList)
  }

  test("Punto 1: paralelo y secuencial dan exactamente lo mismo con varios umbrales") {
    val sistema = sistemaDe(101)
    val esperado = pasoSecuencial(sistema, 0.5).toList
    val umbrales = List(1, 2, 7, 10, 50, 101, 500)
    assert(umbrales.forall(u => pasoParalelo(sistema, 0.5, u).toList == esperado))
  }

  test("Punto 1: el paralelo no pierde ni repite partículas y conserva el orden") {
    val sistema = sistemaDe(37)
    val r = pasoParalelo(sistema, 2.0, 3)
    // la masa identifica a cada partícula
    assert(r.map(q => q.masa).toList == sistema.map(q => q.masa).toList)
  }

  test("Punto 1: el paralelo con umbral negativo da lo mismo que el secuencial") {
    val sistema = sistemaDe(8)
    assert(pasoParalelo(sistema, 1.0, -3).toList == pasoSecuencial(sistema, 1.0).toList)
  }

  // Punto 2 -------------------------------------------------------------------

  test("Punto 2: energía con velocidad (6,8) y masa 1 es 50") {
    // norma 10, media masa por 100
    val unica: Sistema = Array(Particula(Vector2(0, 0), Vector2(6, 8), 1.0))
    assert(cerca(energiaSecuencial(unica), 50.0))
  }

  test("Punto 2: la energía suma la de varias partículas") {
    val dos: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(2, 0), 1.0), // 0.5 * 1 * 4 = 2
      Particula(Vector2(1, 1), Vector2(0, 4), 0.5)) // 0.5 * 0.5 * 16 = 4
    assert(cerca(energiaSecuencial(dos), 6.0))
  }

  test("Punto 2: masaTotal suma masas con decimales") {
    val dos: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(0, 0), 1.5),
      Particula(Vector2(1, 0), Vector2(0, 0), 2.5))
    assert(cerca(masaTotal(dos), 4.0))
  }

  test("Punto 2: centro de masa con masas distintas") {
    // (1 * 1 + 4 * 3) / 4 = 3.25 en x y en y
    val dos: Sistema = Array(
      Particula(Vector2(1, 1), Vector2(0, 0), 1.0),
      Particula(Vector2(4, 4), Vector2(0, 0), 3.0))
    assert(cerca(centroDeMasaSecuencial(dos), Vector2(3.25, 3.25)))
  }

  test("Punto 2: el centro de masa de una sola partícula es su posición") {
    val unica: Sistema = Array(Particula(Vector2(-5, 7), Vector2(1, 1), 2.0))
    assert(cerca(centroDeMasaSecuencial(unica), Vector2(-5, 7)))
  }

  test("Punto 2: con todas las masas en cero el centro de masa es el origen") {
    val sinMasa: Sistema = Array(
      Particula(Vector2(5, 5), Vector2(0, 0), 0.0),
      Particula(Vector2(9, 1), Vector2(0, 0), 0.0))
    assert(centroDeMasaSecuencial(sinMasa) == Vector2(0, 0))
  }
  // Punto 3 -------------------------------------------------------------------

  test("energiaParalela: coincide con la secuencial en el ejemplo") {
    assert(cerca(energiaParalela(s, 1), 25.0))
  }

  test("energiaParalela: coincide con la secuencial para varios umbrales") {
    val esperado = energiaSecuencial(grande)
    assert(List(1, 3, 64, 999, 5000).forall(u => cerca(energiaParalela(grande, u), esperado)))
  }

  test("energiaParalela: sobre el sistema vacío da cero") {
    assert(energiaParalela(vacio, 1) == 0.0)
  }

  test("centroDeMasaParalelo: coincide con el secuencial en el ejemplo") {
    assert(cerca(centroDeMasaParalelo(s, 1), Vector2(2.0, 0.0)))
  }

  test("centroDeMasaParalelo: coincide con el secuencial para varios umbrales") {
    val esperado = centroDeMasaSecuencial(grande)
    assert(List(1, 3, 64, 999, 5000).forall(u => cerca(centroDeMasaParalelo(grande, u), esperado)))
  }

  test("centroDeMasaParalelo: promediar promedios daría otra cosa") {
    // Tres partículas: dos en el origen y una lejos. El promedio de los
    // promedios de las dos mitades no es el centro de masa del conjunto.
    val tres: Sistema = Array(
      Particula(Vector2(0, 0), Vector2(0, 0), 1.0),
      Particula(Vector2(0, 0), Vector2(0, 0), 1.0),
      Particula(Vector2(9, 0), Vector2(0, 0), 1.0))
    assert(cerca(centroDeMasaParalelo(tres, 1), Vector2(3.0, 0.0)))
  }

  test("centroDeMasaParalelo: sin masa el centro es el origen") {
    assert(centroDeMasaParalelo(vacio, 1) == Vector2(0, 0))
  }

  // Punto 4 -------------------------------------------------------------------

  test("trabajo: una operación por partícula") { assert(trabajo(1024) == 1024) }

  test("profundidad: si el umbral cubre todo, no se parte") {
    assert(profundidad(1024, 1024) == 1024)
  }

  test("profundidad: una partición y el tramo que queda") {
    assert(profundidad(1024, 512) == 513)
  }

  test("profundidad: diez particiones y un elemento") {
    assert(profundidad(1024, 1) == 11)
  }

  test("tiempoEstimado: profundidad más trabajo entre procesadores") {
    assert(cerca(tiempoEstimado(1024, 4, 1), 267.0))
  }

  test("tiempoEstimado: con un procesador no hay ganancia") {
    assert(cerca(tiempoEstimado(1024, 1, 1), 11.0 + 1024.0))
  }

  test("profundidad: nunca supera el trabajo") {
    val casos = for (n <- List(1, 7, 64, 1024); u <- List(1, 8, 64)) yield (n, u)
    assert(casos.forall { case (n, u) => profundidad(n, u) <= trabajo(n) })
  }

  // Punto 5 -------------------------------------------------------------------

  test("aceleracion: cuatro veces más rápido da 4.0") {
    assert(cerca(aceleracion(100.0, 25.0), 4.0))
  }

  test("aceleracion: el mismo tiempo da 1.0") {
    assert(cerca(aceleracion(100.0, 100.0), 1.0))
  }

  test("umbralConveniente: sin candidatos devuelve el tamaño del sistema") {
    assert(umbralConveniente(grande, 0.5, Nil) == grande.length)
  }

  test("umbralConveniente: devuelve uno de los candidatos") {
    val candidatos = List(16, 128, 1024)
    assert(candidatos.contains(umbralConveniente(grande, 0.5, candidatos)))
  }
}

```


#### Short summary: 

empty definition using pc, found symbol in pc: 