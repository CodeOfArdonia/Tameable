# Tameable

*Someone ask me whether they can tame Ingresssus Voltaris (Death Singer) in my SoW Server, so this mod comes out.*

This mod provides a configurable system to control which mobs you can tame.

**Note: Some entities may meet bugs since this mod doesn't rewrite the full AI!**

## How to use

Open file `.minecraft/config/tameable.json`. If this file not exists, create it.

You can use `/reload` in game to reload your edited config.

Config explanation:
```json5
{
  "entity type here": {
    "tame": [//Which item you can use to tame
      "a single item",
      "#or a tag"
    ],
    "breed": [//Which item you can use to breed. If you leave blank, it will use tame item.
      "a single item",
      "#or a tag",
      {//or an object
        "item": "item or tag",
        "heal": 1
      }
    ],//Breed amount: value set -> food component -> 1
    "chance": 0.1,//The taming chance
    "attack": true,//should attack what player is attacking
    "protect": true,//should attack what is attacking player
    "follow": {
      "enable": true,//should follow player
      "speed": 1,//follow speed
      "minDistance": 10,//The max range to stop follow
      "maxDistance": 2,//The min range to start follow
      "leavesAllowed": false,//Can leave
    }
  },
  //... more
}
```
Default Values:
```json5
{
  "entity type here": {
    "tame": [//This is the only required field
    ],
    "breed": [//Optional, will use tame item if this field not exist or blank
      "item or tag",
      {//or an object
        "item": "item or tag",
        "heal": 1
      }
    ],
    "chance": 1,//The taming chance
    "attack": false,
    "protect": false,
    "follow": {//These value except "enable" is the default value of a wolf
      "enable": false,
      "speed": 1,
      "minDistance": 10,
      "maxDistance": 2,
      "leavesAllowed": false,
    }
  },
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