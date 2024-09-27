# Tameable

*Someone ask me whether they can tame Ingresssus Voltaris (Death Singer) in my SoW Server, so this mod comes out.*

This mod provides a configurable system to control which mobs you can tame.

**Note: Some entities may meet bugs since this mod doesn't rewrite the full AI!**

## How to use

Open file `.minecraft/config/tameable.json`. If this file not exists, create it.

Config explanation:
```json5
{
  "entity type here": {
    "food": [
      "a single item",
      "#or a tag"
    ],
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
    "food": [//This is the only required field
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

With config following, you can tame pillagers with apples.
```json5
{
  "minecraft:pillager": {
    "food": [
      "minecraft:apple"
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