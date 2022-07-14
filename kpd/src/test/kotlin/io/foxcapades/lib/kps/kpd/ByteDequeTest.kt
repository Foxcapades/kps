package io.foxcapades.lib.kps.kpd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@DisplayName("ByteDeque")
internal class ByteDequeTest {

  @Nested
  @DisplayName("of(Byte...)")
  inner class Of {

    @Nested
    @DisplayName("with 0 input args")
    inner class ZeroArgs {

      @Test
      @DisplayName("returns an empty deque")
      fun t1() {
        val tgt = ByteDeque.of()

        assertTrue(tgt.isEmpty())
        assertEquals(0, tgt.size)
        assertEquals(0, tgt.capacity)
        assertThrows<NoSuchElementException> { tgt.front }
        assertThrows<NoSuchElementException> { tgt.first }
        assertThrows<NoSuchElementException> { tgt.back }
        assertThrows<NoSuchElementException> { tgt.last }
      }
    }

    @Nested
    @DisplayName("with one input arg")
    inner class OneArg {

      @Test
      @DisplayName("returns a deque wrapping the single value")
      fun t1() {
        val tgt = ByteDeque.of(69)

        assertFalse(tgt.isEmpty())
        assertEquals(1, tgt.size)
        assertEquals(1, tgt.capacity)
        assertEquals(69, tgt.front)
        assertEquals(69, tgt.first)
        assertEquals(69, tgt.back)
        assertEquals(69, tgt.last)
        assertEquals(69, tgt[0])
      }
    }

    @Nested
    @DisplayName("with multiple input args")
    inner class MultiArg {

      @Test
      @DisplayName("returns a deque wrapping the given values")
      fun t1() {
        val tgt = ByteDeque.of(1, 2, 3)

        assertFalse(tgt.isEmpty())
        assertEquals(3, tgt.size)
        assertEquals(3, tgt.capacity)
        assertEquals(1, tgt[0])
        assertEquals(2, tgt[1])
        assertEquals(3, tgt[2])
      }
    }
  }


}