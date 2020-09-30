/*
 * This file is part of adventure, licensed under the MIT License.
 *
 * Copyright (c) 2017-2020 KyoriPowered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.kyori.adventure.kt.text

import net.kyori.adventure.key.Key
import net.kyori.adventure.text.BlockNBTComponent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentBuilder
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.EntityNBTComponent
import net.kyori.adventure.text.KeybindComponent
import net.kyori.adventure.text.ScoreComponent
import net.kyori.adventure.text.SelectorComponent
import net.kyori.adventure.text.StorageNBTComponent
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.StyleBuilderApplicable

// Joining components together //

// elipses for truncated joins
internal val TRUNCATE_MARK = TextComponent.of("...")
internal val COMMA_SPACE = TextComponent.of(", ")

/**
 * Add [that] as a child component.
 *
 * @return a component
 * @since 4.0.0
 */
public operator fun Component.plus(that: ComponentLike): Component = this.append(that)

/**
 * Append [that] as a child of the component being built.
 *
 * @since 4.0.0
 */
public operator fun ComponentBuilder<*, *>.plusAssign(that: ComponentLike) {
  this.append(that)
}

/**
 * Append all components in [that] as children of the component being built.
 *
 * @since 4.0.0
 */
public operator fun ComponentBuilder<*, *>.plusAssign(that: Iterable<ComponentLike>) {
  this.append(that)
}

/**
 * Convert this object into a component.
 *
 * @since 4.0.0
 */
public operator fun ComponentLike.unaryPlus(): Component = asComponent()

/**
 * Append the [Iterable] of components to an existing builder, returning the built component
 *
 * This method should match the specification of [kotlin.collections.joinTo], but
 * acting on [Component]s rather than on Strings.
 *
 * @since 4.0.0
 */
public fun <T : ComponentLike, B : ComponentBuilder<*, B>> Iterable<T>.joinTo(
  builder: B,
  separator: Component = COMMA_SPACE,
  prefix: Component = Component.empty(),
  suffix: Component = Component.empty(),
  limit: Int = -1,
  truncated: Component = TRUNCATE_MARK,
  transform: (Component) -> Component = { it }
): B {
  val iter = iterator()
  builder.append(prefix)

  var count = 0
  while(iter.hasNext()) {
    if(limit <= 0 || count++ < limit) {
      builder.append(transform(iter.next().asComponent()))
    } else {
      builder.append(truncated)
      break
    }

    if(iter.hasNext()) {
      builder.append(separator)
    }
  }

  builder.append(suffix)
  return builder
}

/**
 * Join an iterable of components into a new [TextComponent]
 *
 * @see [joinTo] for parameter descriptions
 */
public fun <T : ComponentLike> Iterable<T>.join(
  separator: Component = COMMA_SPACE,
  prefix: Component = Component.empty(),
  suffix: Component = Component.empty(),
  limit: Int = -1,
  truncated: Component = TRUNCATE_MARK,
  transform: (Component) -> Component = { it }
): Component = joinTo(TextComponent.builder(), separator, prefix, suffix, limit, truncated, transform).build()

// Factory methods //

/**
 * Create a new text component from [contents]
 *
 * @sample [net.kyori.adventure.example.kt.componentDsl]
 */
public fun text(contents: String, vararg styles: StyleBuilderApplicable): TextComponent = Component.text(contents, Style.style(*styles))

/**
 * Create a new translatable component from [key]
 *
 * @sample [net.kyori.adventure.example.kt.componentDsl]
 */
public fun translatable(key: String, vararg args: ComponentLike): TranslatableComponent = Component.translatable(key, *args)

/** Create a new keybind component using the key sequence identified by [key] */
public fun keybind(key: String, vararg styles: StyleBuilderApplicable): KeybindComponent = Component.keybind(key, Style.style(*styles))

/** Create a new selector component, using [selector] as a selector. */
public fun selector(selector: String, vararg styles: StyleBuilderApplicable): SelectorComponent = Component.selector().pattern(selector).style(Style.style(*styles)).build()

/** Create a new score component, with the [score] in [objective] */
public fun score(score: String, objective: String, vararg styles: StyleBuilderApplicable): ScoreComponent = ScoreComponent.builder(score, objective).style(Style.of(*styles)).build()

/** Create a new block NBT component, with nbt path from [path] gotten at [pos] */
public fun blockNBT(path: String, pos: BlockNBTComponent.Pos, interpret: Boolean = false, vararg styles: StyleBuilderApplicable): BlockNBTComponent = BlockNBTComponent.builder()
  .nbtPath(path)
  .pos(pos)
  .interpret(interpret)
  .style(Style.of(*styles))
  .build()

/** Create a new entity NBT component, with nbt path [path] gotten from the entity marked by [entitySelector] */
public fun entityNBT(path: String, entitySelector: String, interpret: Boolean = false, vararg styles: StyleBuilderApplicable): EntityNBTComponent = EntityNBTComponent.builder()
  .nbtPath(path)
  .selector(entitySelector)
  .interpret(interpret)
  .style(Style.of(*styles))
  .build()

/** Create a new storage NBT component, with nbt path [path] gotten from the named storage at [storage] */
public fun storageNBT(path: String, storage: Key, interpret: Boolean = false, vararg styles: StyleBuilderApplicable): StorageNBTComponent = StorageNBTComponent.builder()
  .nbtPath(path)
  .storage(storage)
  .interpret(interpret)
  .style(Style.of(*styles))
  .build()

/**
 * Create a new text component from [contents]
 *
 * @sample [net.kyori.adventure.example.kt.componentDsl]
 */
public fun text(contents: String, maker: TextComponent.Builder.() -> Unit): TextComponent = TextComponent.builder(contents).also(maker).build()

/**
 * Create a new translatable component from [key]
 *
 * @sample [net.kyori.adventure.example.kt.componentDsl]
 */
public fun translatable(key: String, maker: TranslatableComponent.Builder.() -> Unit): TranslatableComponent = TranslatableComponent.builder(key).also(maker).build()

/** Create a new keybind component using the key sequence identified by [key] */
public fun keybind(key: String, maker: KeybindComponent.Builder.() -> Unit): KeybindComponent = KeybindComponent.builder(key).also(maker).build()

/** Create a new selector component, using [selector] as a selector. */
public fun selector(selector: String, maker: SelectorComponent.Builder.() -> Unit): SelectorComponent = SelectorComponent.builder(selector).also(maker).build()

/** Create a new score component, with [score] in [objective] */
public fun score(score: String, objective: String, maker: ScoreComponent.Builder.() -> Unit): ScoreComponent = ScoreComponent.builder(score, objective).also(maker).build()

/** Create a new block NBT component, with nbt path [path] gotten at [pos] */
public fun blockNBT(path: String, pos: BlockNBTComponent.Pos, maker: BlockNBTComponent.Builder.() -> Unit): BlockNBTComponent = BlockNBTComponent.builder()
  .nbtPath(path)
  .pos(pos)
  .also(maker)
  .build()

/** Create a new entity NBT component, with nbt path [path] gotten from the entity marked by [entitySelector] */
public fun entityNBT(path: String, entitySelector: String, maker: EntityNBTComponent.Builder.() -> Unit): EntityNBTComponent = EntityNBTComponent.builder()
  .nbtPath(path)
  .selector(entitySelector)
  .also(maker)
  .build()

/** Create a new storage NBT component, with nbt path [path] gotten from the named storage at [storage] */
public fun storageNBT(path: String, storage: Key, maker: StorageNBTComponent.Builder.() -> Unit): StorageNBTComponent = StorageNBTComponent.builder()
  .nbtPath(path)
  .storage(storage)
  .also(maker)
  .build()
