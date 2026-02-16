blockstate_data="""{
  "variants": {
    "": {
      "model": "ominous_dyenamics_candles:block/COLOR_ominous_candle"
    }
  }
}"""
model_data="""{
  "parent": "minecraft:builtin/entity",
  "textures": {
    "particle": "dyenamics:block/COLOR_candle_lit"
  }
}"""

colors=["peach","aquamarine","fluorescent","mint","maroon","bubblegum","lavender","persimmon","cherenkov",
        "amber","honey","ultramarine","spring_green","rose","navy","icy_blue","wine","conifer"]

for color in colors:
    f=open("blockstates/"+color+"_ominous_candle.json","w")
    f.write(blockstate_data.replace("COLOR",color))
    f.close()
    f=open("models/block/"+color+"_ominous_candle.json","w")
    f.write(model_data.replace("COLOR",color))
    f.close()