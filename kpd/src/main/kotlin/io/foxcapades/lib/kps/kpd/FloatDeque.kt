package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractFloatDeque

/**
 * Float Deque
 *
 * A deque type that deals in unboxed [Float] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class FloatDeque : PrimitiveDeque, AbstractFloatDeque<FloatDeque> {

  /**
   * Constructs a new, empty `FloatDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `FloatDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `FloatDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: FloatArray) : super(values)

  constructor(values: FloatArray, head: Int) : super(values, head)

  override fun new(values: FloatArray, head: Int) = FloatDeque(values, head)

  companion object {

    /**
     * Creates a new [FloatDeque] instance wrapping the given values.
     *
     * The returned `FloatDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [FloatDeque].
     *
     * @return A new `FloatDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Float) = FloatDeque(values, 0)
  }

}