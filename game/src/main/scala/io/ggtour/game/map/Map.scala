package io.ggtour.game.map

import java.util.UUID

case class Map(
    mapId: UUID,
    name: String,
    thumbnail: Array[Byte],
    mapFile: Array[Byte])
