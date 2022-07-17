package io.foxcapades.lib.kps.kpd

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.ByteArrayInputStream
import java.io.InputStream

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


  @Nested
  @DisplayName("fillFrom(InputStream)")
  inner class FillFrom1 {

    @Nested
    @DisplayName("when the InputStream has already reached it's end")
    inner class InputStreamOver {

      val stream: InputStream
        get() = ByteArrayInputStream(ByteArray(0))

      @Nested
      @DisplayName("and the deque has a capacity of 0")
      inner class Cap0 {

        @Test
        @DisplayName("returns 0")
        fun t1() {
          val tgt = ByteDeque(0)

          assertEquals(0, tgt.fillFrom(stream))
        }
      }

      @Nested
      @DisplayName("and the deque has 0 space available")
      inner class Space0 {

        @Test
        @DisplayName("returns 0")
        fun t1() {
          val tgt = ByteDeque.of(1, 2, 3)

          assertEquals(0, tgt.fillFrom(stream))
        }
      }

      @Nested
      @DisplayName("and the deque has some space available")
      inner class SomeSpace {

        @Test
        @DisplayName("returns -1")
        fun t1() {
          val tgt = ByteDeque(10)

          assertEquals(-1, tgt.fillFrom(stream))
        }
      }
    }

    @Nested
    @DisplayName("when the InputStream has some number of bytes")
    inner class InputStreamBytes {

      val streamValue = "Goodbye cruel world!"

      val stream: InputStream
        get() = ByteArrayInputStream(streamValue.toByteArray())

      val streamSize = 20

      @Nested
      @DisplayName("and the deque has a capacity of 0")
      inner class Cap0 {

        @Test
        @DisplayName("returns 0")
        fun t1() {
          val tgt = ByteDeque(0)

          assertEquals(0, tgt.fillFrom(stream))
        }

        @Test
        @DisplayName("reads no bytes from the input stream")
        fun t2() {
          val tgt = ByteDeque(0)

          tgt.fillFrom(stream)

          assertEquals("Goodbye cruel world!", stream.readBytes().decodeToString())
        }
      }

      @Nested
      @DisplayName("and the deque has 0 space available")
      inner class Space0 {

        @Test
        @DisplayName("returns 0")
        fun t1() {
          val tgt = ByteDeque.of(1, 2, 3)

          assertEquals(0, tgt.fillFrom(stream))
        }

        @Test
        @DisplayName("reads no bytes from the input stream")
        fun t2() {
          val tgt = ByteDeque.of(1, 2, 3)

          tgt.fillFrom(stream)

          assertEquals("Goodbye cruel world!", stream.readBytes().decodeToString())
        }
      }

      @Nested
      @DisplayName("and the deque is empty")
      inner class Empty {

        @Nested
        @DisplayName("with exactly the required amount of space for the stream contents")
        inner class Exact {

          @Test
          @DisplayName("reads the correct number of bytes from the stream")
          fun t1() {
            val tgt = ByteDeque(streamSize)

            assertEquals(streamSize, tgt.fillFrom(stream))
            assertEquals(streamSize, tgt.size)
          }

          @Test
          @DisplayName("contains the expected values after read")
          fun t2() {
            val tgt = ByteDeque(streamSize)

            tgt.fillFrom(stream)
            assertEquals(streamValue, tgt.toArray().decodeToString())
          }
        }

        @Nested
        @DisplayName("with less than the required amount of space for the stream contents")
        inner class LessThan
      }
    }
  }
}