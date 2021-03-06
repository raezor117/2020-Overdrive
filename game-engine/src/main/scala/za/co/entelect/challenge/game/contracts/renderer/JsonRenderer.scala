package za.co.entelect.challenge.game.contracts.renderer

import net.liftweb.json.JsonDSL._
import net.liftweb.json._
import za.co.entelect.challenge.game.contracts.Config.Config
import za.co.entelect.challenge.game.contracts.game.CarGamePlayer
import za.co.entelect.challenge.game.contracts.map.CarGameMap

class JsonRenderer extends BaseMapRenderer {

  override def renderFragment(gameMap: CarGameMap, gamePlayer: CarGamePlayer): String = {
    val mapFragment = gameMap.getMapFragment(gamePlayer)
    val player = mapFragment.getPlayer()
    val opponent = gameMap.getCarGamePlayers()
      .find(p => p.getGamePlayerId() != gamePlayer.getGamePlayerId()).get
    val opponentBlock = gameMap.getBlocks()
      .find(b => b.occupiedByPlayerWithId == opponent.getGamePlayerId()).get
    val mapFragmentJsonStructure =
      ("currentRound" -> mapFragment.getCurrentRound()) ~
        ("maxRounds" -> Config.MAX_ROUNDS) ~
        ("player" ->
          ("id" -> player.getId()) ~
            ("position" ->
              ("y" -> player.getPosition().getLane()) ~
                ("x" -> player.getPosition().getBlockNumber())
              ) ~
            ("speed" -> player.getSpeed()) ~
            ("state" -> player.getState()) ~
            ("powerups" -> player.getPowerups().toList) ~
            ("boosting" -> player.isBoosting()) ~
            ("boostCounter" -> player.getBoostCounter())
          ) ~
        ("opponent" ->
          ("id" -> opponent.getGamePlayerId()) ~
            ("position" ->
              ("y" -> opponentBlock.getPosition().getLane()) ~
                ("x" -> opponentBlock.getPosition().getBlockNumber())
              ) ~
            ("speed" -> opponent.getSpeed())
          ) ~
        ("worldMap" -> mapFragment.getBlocks()
          .groupBy { b => b.getPosition().getLane() }
          .flatMap { kv =>
            List(kv._2
              .sortBy(b => b.getPosition().getBlockNumber())
              .map { b =>
                ("position" ->
                  ("y" -> b.getPosition().getLane()) ~
                    ("x" -> b.getPosition().getBlockNumber())
                  ) ~
                  ("surfaceObject" -> b.getMapObject()) ~
                  ("occupiedByPlayerId" -> b.getOccupiedByPlayerWithId())
              }.toList)
          }.toList)

    prettyRender(mapFragmentJsonStructure)
  }

  override def renderVisualiserMap(gameMap: CarGameMap): String = {
    val globalMapJsonStructure =
      ("players" ->
        gameMap.getCarGamePlayers().toList.map { p =>
          ("id" -> p.getGamePlayerId()) ~
            ("position" ->
              ("lane" -> gameMap.getPlayerBlockPosition(p.getGamePlayerId()).getLane()) ~
                ("blockNumber" -> gameMap.getPlayerBlockPosition(p.getGamePlayerId()).getBlockNumber())
              ) ~
            ("speed" -> p.getSpeed()) ~
            ("state" -> p.getState()) ~
            ("powerups" -> p.getPowerups().toList) ~
            ("boosting" -> p.isBoosting()) ~
            ("boostCounter" -> p.getBoostCounter())
        }
        ) ~
        ("blocks" -> gameMap.getBlocks().toList
          .map { b =>
            ("position" ->
              ("lane" -> b.getPosition().getLane()) ~
                ("blockNumber" -> b.getPosition().getBlockNumber())
              ) ~
              ("surfaceObject" -> b.getMapObject()) ~
              ("occupiedByPlayerWithId" -> b.getOccupiedByPlayerWithId())
          }
          )

    prettyRender(globalMapJsonStructure)
  }
}
