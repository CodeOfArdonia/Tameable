# Tameable

*Someone ask me whether they can tame Ingresssus Voltaris (Death Singer) in my SoW Server, so this mod comes out.*

This mod provides a configurable system to control which mobs you can tame.

**Note: Some entities may meet bugs since this mod doesn't rewrite the full AI!**

## How to use

You can use command `/tame <entityUuid> <ownerUuid>` to tame manually (`ownerUuid` leave blank to remove owner). Also, there are two ways to configure.

### Configure with data driven (Recommended)

Create file `data/<entity mod id>/tameable/<entity id>.json` in your datapack or mod. Then write contents below:

```json5
{
  "tame": [
    //Which item you can use to tame
    "a single item",
    "#or a tag"
  ],
  "breed": [
    //Which item you can use to breed. If you leave blank, it will use tame item.
    //Breed amount: value set -> food component -> 1
    "a single item",
    "#or a tag",
    {
      //or an object
      "item": "item or tag",
      "heal": 1
    }
  ],
  //The taming chance
  "chance": 0.1,
  //should attack what player is attacking
  "attack": true,
  //should attack what is attacking player
  "protect": true,
  "follow": {
    //should follow player
    "enable": true,
    //follow speed
    "speed": 1,
    //The max range to stop follow
    "minDistance": 10,
    //The min range to start follow
    "maxDistance": 2,
    //Can leave
    "leavesAllowed": false,
  }
}
```

Default Values:

```json5
 {
  "tame": [
    //This is the only required field
  ],
  "breed": [
    //Optional, will use tame item if this field not exist or blank
    "item or tag",
    {
      //or an object
      "item": "item or tag",
      "heal": 1
    }
  ],
  //The taming chance
  "chance": 1,
  "attack": false,
  "protect": false,
  "follow": {
    //These value except "enable" is the default value of a wolf
    "enable": false,
    "speed": 1,
    "minDistance": 10,
    "maxDistance": 2,
    "leavesAllowed": false,
  }
}
```

Example:

Write contents into `data/minecraft/tameable/pillager.json`, you can tame pillagers with apples and breed with
apples&stones

```json5
{
  "tame": [
    "minecraft:apple"
  ],
  "breed": [
    "minecraft:apple",
    {
      "item": "minecraft:stone",
      "heal": 5
    }
  ],
  "chance": 0.1,
  "attack": true,
  "protect": true,
  "follow": {
    "enable": true
  }
}
```

### Configure with config file

**NOTE:This will overwrite settings in datapack(s)!**

Open file `.minecraft/config/tameable.json`. If this file not exists, create it.

You can use `/reload` in game to reload your edited config.

Config explanation:

```json5
{
  "entity type here": {
    //An object in datapack format, see it above
  }
  //... more
}
```

Example:

With config following, you can tame pillagers with apples and breed with apples&stones

```json5
{
  "minecraft:pillager": {
    "tame": [
      "minecraft:apple"
    ],
    "breed": [
      "minecraft:apple",
      {
        "item": "minecraft:stone",
        "heal": 5
      }
    ],
    "chance": 0.1,
    "attack": true,
    "protect": true,
    "follow": {
      "enable": true
    }
  }
}
```