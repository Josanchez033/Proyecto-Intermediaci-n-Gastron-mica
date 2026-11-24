package cl.dsy1105.cocinacapital.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ValidacionesTest {

    @Test
    fun formatoDeCorreo() {
        assertTrue(esEmailValido("ana@correo.cl"))
        assertTrue(esEmailValido("  ana.perez@duoc.cl "))
        assertFalse(esEmailValido("ana@"))
        assertFalse(esEmailValido("anacorreo.cl"))
        assertFalse(esEmailValido(""))
    }

    @Test
    fun login() {
        assertEquals(setOf(Campo.EMAIL, Campo.PASSWORD), validarLogin("", "").keys)
        assertEquals("El formato del correo no es válido", validarLogin("malo", "x")[Campo.EMAIL])
        assertTrue(validarLogin("ana@correo.cl", "123").isEmpty()) // al entrar no se exige la regla nueva
    }

    @Test
    fun reglaDeContrasenaNueva() {
        assertEquals("La contraseña es obligatoria", errorClaveNueva(""))
        assertEquals("Debe tener al menos 6 caracteres", errorClaveNueva("ab1"))
        assertEquals("Agrega al menos un número", errorClaveNueva("abcdefg"))
        assertNull(errorClaveNueva("clave123"))
    }

    @Test
    fun registro() {
        assertEquals(setOf(Campo.EMAIL, Campo.PASSWORD, Campo.CONFIRMAR), validarRegistro("", "", "").keys)
        assertEquals("El formato del correo no es válido", validarRegistro("x", "clave123", "clave123")[Campo.EMAIL])
        assertEquals("Las contraseñas no coinciden", validarRegistro("a@b.cl", "clave123", "clave124")[Campo.CONFIRMAR])
        assertTrue(validarRegistro("a@b.cl", "clave123", "clave123").isEmpty())
    }

    @Test
    fun claveNuevaConConfirmacion() {
        assertEquals("Repite la contraseña", validarClaveNueva("clave123", "")[Campo.CONFIRMAR])
        assertEquals("Las contraseñas no coinciden", validarClaveNueva("clave123", "otra")[Campo.CONFIRMAR])
        assertTrue(validarClaveNueva("clave123", "clave123").isEmpty())
    }

    @Test
    fun promocion() {
        val errores = validarPromocion("", "", "abc", "99", "", 18, 15)
        assertEquals(setOf(Campo.NOMBRE, Campo.PROMOCION, Campo.PRECIO, Campo.DESCUENTO, Campo.DIAS, Campo.HORARIO), errores.keys)
        assertEquals("El descuento va de 5% a 90%", validarPromocion("A", "B", "1000", "3", "1", 15, 19)[Campo.DESCUENTO])
        assertTrue(validarPromocion("Trattoria", "2x1", "11900", "50", "5", 19, 23).isEmpty())
    }

    @Test
    fun nuevoLocalDelAdmin() {
        assertEquals(5, validarNuevoLocal("", "", "", "", "", false).size) // nombre, comuna, dirección, correo y contraseña
        assertEquals("El formato del correo no es válido", validarNuevoLocal("A", "B", "C", "malo", "clave1", false)[Campo.EMAIL])
        assertEquals("Ya existe una cuenta con ese correo", validarNuevoLocal("A", "B", "C", "a@b.cl", "clave1", true)[Campo.EMAIL])
        assertTrue(validarNuevoLocal("Sushi", "Viña del Mar", "Calle 1", "sushi@correo.cl", "clave1", false).isEmpty())
    }
}
