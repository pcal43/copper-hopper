# Copper Hopper

Copper Hopper adds a new kind of Hopper that filters the items to get pulled and pushed.  They 
save you the tedium of building gigantic item sorters while retaining a Vanilla Minecraft spirit.

### Features
* Filter up to 5 items per Copper Hopper
* Filter both stackable and unstackable items
* Uses the Fabric modloader
* Made of copper!

## Recipe

![Recipe](https://github.com/pcal43/copper-hopper/blob/main/etc/copper-hopper-crafting.png?raw=true)

## Setting Filters

![Filters](https://github.com/pcal43/copper-hopper/blob/main/etc/dialog.png?raw=true)

## Make sorting systems like this!

![System](https://github.com/pcal43/copper-hopper/blob/main/etc/sorted_storage.png?raw=true)


## How do use it?

Just put an item in one of the regular hopper slots to "set" a filter.  That item will never be pulled or
pushed out of the hopper.  And the hopper will now only accept items of that type.

You can do this up to five times (once per slot).

## How does it work exactly?

A Copper Hopper works just like a regular hopper except:

* They never accept an item of a type that is not already held in the hopper.
* They always keep the last item of a type that is held in the hopper.
* If one Copper Hopper is below another, it will always pull matching items before the upper hopper pushes them.  
  * This allows you to create hopper columns that filter items into vertically-stacked chests.

## Does it work with unstackable items?

Yes!  But if you filter on unstackable items, be sure to keep one of the hopper slots empty or
the filtering won't work correctly.

## Notes

* The item texture for Copper Hopper was contributed by [Pablo Henrique](https://github.com/ppblitto). (Thanks!)
* Copper Hopper was inspired by Mr. Crayfish's excellent [Golden Hopper](https://www.curseforge.com/minecraft/mc-mods/golden-hopper) forge mod.
* I will not be doing a Forge port of Copper Hopper. (sorry)
 

## Legal

This mod is published under the [MIT License](LICENSE).

You're free to include this mod in your modpack provided you attribute it to pcal.net.

## Discord

https://discord.gg/jUP5nSPrjx
