package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractBooleanDeque

/**
 * Boolean Deque
 *
 * A deque type that deals in unboxed [Boolean] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
class BooleanDeque : PrimitiveDeque, AbstractBooleanDeque<BooleanDeque> {

  /**
   * Constructs a new, empty `BooleanDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `BooleanDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `BooleanDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: BooleanArray) : super(values)

  constructor(values: BooleanArray, head: Int) : super(values, head)

  override fun new(values: BooleanArray, head: Int) = BooleanDeque(values, head)

  companion object {

    /**
     * Creates a new [BooleanDeque] instance wrapping the given values.
     *
     * The returned `BooleanDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [BooleanDeque].
     *
     * @return A new `BooleanDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: Boolean) = BooleanDeque(values, 0)
  }

}