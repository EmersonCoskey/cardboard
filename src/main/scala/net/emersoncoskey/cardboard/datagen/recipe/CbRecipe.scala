package net.emersoncoskey.cardboard.datagen.recipe

import net.emersoncoskey.cardboard.CbMod
import net.minecraft.advancements.critereon.{EntityPredicate, InventoryChangeTrigger, ItemPredicate, MinMaxBounds}
import net.minecraft.core.Registry
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.Tag
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

import java.util.function.Consumer

trait CbRecipe {
	private[cardboard] def save(consumer: Consumer[FinishedRecipe], mod: CbMod): Unit

}

object CbRecipe {
	def inventoryTrigger(firstPred: ItemPredicate, restPred: ItemPredicate*): InventoryChangeTrigger.TriggerInstance =
		new InventoryChangeTrigger.TriggerInstance(
			EntityPredicate.Composite.ANY,
			MinMaxBounds.Ints.ANY,
			MinMaxBounds.Ints.ANY,
			MinMaxBounds.Ints.ANY,
			(firstPred :: restPred.toList).toArray
		)

	def has(firstItem: Item, restItems: Item*): InventoryChangeTrigger.TriggerInstance =
		inventoryTrigger(
			ItemPredicate.Builder.item.of(firstItem).build,
			restItems.map(ItemPredicate.Builder.item.of(_).build):_*
		)

	def has(firstTag: Tag[Item], restTags: Tag[Item]*): InventoryChangeTrigger.TriggerInstance =
		inventoryTrigger(
			ItemPredicate.Builder.item.of(firstTag).build,
			restTags.map(ItemPredicate.Builder.item.of(_).build):_*
		)

	def has(firstItem: (Item, MinMaxBounds.Ints), restItems: (Item, MinMaxBounds.Ints)*): InventoryChangeTrigger.TriggerInstance =
		inventoryTrigger(
			ItemPredicate.Builder.item.of(firstItem._1).withCount(firstItem._2).build,
			restItems.map { case(i, n) => ItemPredicate.Builder.item.of(i).withCount(n).build }:_*
		)
}