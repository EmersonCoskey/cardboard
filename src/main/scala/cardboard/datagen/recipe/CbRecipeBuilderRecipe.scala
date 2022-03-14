package cardboard.datagen.recipe

import cardboard.CbMod
import cats.data.State
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.advancements.critereon.{EnterBlockTrigger, EntityPredicate, ItemPredicate, StatePropertiesPredicate}
import net.minecraft.data.recipes.{FinishedRecipe, RecipeBuilder}
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

import java.util.function.Consumer

abstract class CbRecipeBuilderRecipe[B <: RecipeBuilder](
	val internal: B,
	val act     : State[B, _],
	val id      : Option[String],
) extends CbRecipe {
	private[cardboard] def save(consumer: Consumer[FinishedRecipe], mod: CbMod): Unit = id match {
		case Some(s) => act.runS(internal).value.save(consumer, new ResourceLocation(mod.ModId, s))
		case None => act.runS(internal).value.save(consumer)
	}
}


object CbRecipeBuilderRecipe {
	trait Ops[B <: RecipeBuilder] {
		def group(name: String): State[B, Unit] = State.modify(_.group(name).asInstanceOf[B]) //TODO: NOT THIS

		def unlockedBy(criterionName: String, trigger: CriterionTriggerInstance): State[B, Unit] =
			State.modify(_.unlockedBy(criterionName, trigger).asInstanceOf[B])

		def unlockedByItem(item: Item): State[B, Unit] =
			unlockedBy(
				s"has_${ item.getRegistryName.getPath }",
				CbRecipe.inventoryTrigger(ItemPredicate.Builder.item.of(item).build)
			)

		def unlockedByInBlock(block: Block): State[B, Unit] =
			unlockedBy(
				s"inside_of_${ block.getRegistryName.getPath }",
				new EnterBlockTrigger.TriggerInstance(
					EntityPredicate.Composite.ANY,
					block, StatePropertiesPredicate.ANY
				)
			)
	}
}

