package io.foxcapades.lib.kps.kpd

import io.foxcapades.lib.kps.kpd.base.AbstractUIntDeque

/**
 * UInt Deque
 *
 * A deque type that deals in unboxed [UInt] values.
 *
 * @author Elizabeth Paige Harper - https://github.com/foxcapades
 * @since v1.0.0
 */
@OptIn(ExperimentalUnsignedTypes::class)
class UIntDeque : PrimitiveDeque, AbstractUIntDeque<UIntDeque> {

  /**
   * Constructs a new, empty `UIntDeque` with the given initial capacity.
   *
   * @param initCapacity Initial capacity for the new `UIntDeque` instance.
   */
  constructor(initCapacity: Int) : super(initCapacity)

  /**
   * Constructs a new `UIntDeque` instance populated with the given values.
   *
   * The new deque's capacity will be equal to the input array's length.
   *
   * @param values Values to populate the new deque with.
   */
  constructor(values: UIntArray) : super(values)

  constructor(values: UIntArray, head: Int) : super(values, head)

  override fun new(values: UIntArray, head: Int) = UIntDeque(values, head)

  companion object {

    /**
     * Creates a new [UIntDeque] instance wrapping the given values.
     *
     * The returned `UIntDeque` will have the same size and capacity as the
     * number of values passed to this function.
     *
     * @param values Values to wrap with a new [UIntDeque].
     *
     * @return A new `UIntDeque` wrapping the given values.
     */
    @JvmStatic
    fun of(vararg values: UInt) = UIntDeque(values, 0)
  }

}