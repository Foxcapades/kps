package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractDoubleDeque

/**
 * Double Deque
 *
 * A deque type that deals in unboxed [Double] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class DoubleDeque : PrimitiveDeque, AbstractDoubleDeque<DoubleDeque> {

  /**
   * Constructs a new, empty `DoubleDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `DoubleDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `DoubleDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: DoubleArray) : super(values)

  constructor(values: DoubleArray, head: Int) : super(values, head)

  override fun new(values: DoubleArray, head: Int) = DoubleDeque(values, head)

  companion object {

    /**
     * Creates a new [DoubleDeque] instance wrapping the given values.
     *
     * The returned `DoubleDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [DoubleDeque].
     *
     * @return A new `DoubleDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Double) = DoubleDeque(values, 0)
  }

}