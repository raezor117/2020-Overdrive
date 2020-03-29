package test

import java.util
import scala.collection.JavaConverters._
import org.scalatest.FunSuite
import za.co.entelect.challenge.game.contracts.Config.Config
import za.co.entelect.challenge.game.contracts.command.RawCommand
import za.co.entelect.challenge.game.contracts.commands.CommandFactory
import za.co.entelect.challenge.game.contracts.game.{CarGamePlayer, CarGameRoundProcessor, GamePlayer}
import za.co.entelect.challenge.game.contracts.map.CarGameMap

class Command_Nothing_Tests extends FunSuite{
  private var carGameRoundProcessor: CarGameRoundProcessor = null

  private val commandText = "NOTHING";
  private var commandFactory: CommandFactory = null
  private var nothingCommand: RawCommand = null

  def initialise() = {
    Config.loadDefault();

    carGameRoundProcessor = new CarGameRoundProcessor

    commandFactory = new CommandFactory;
    nothingCommand = commandFactory.makeCommand(commandText)
    nothingCommand.setCommand(commandText)
  }

  test("Given players at start of race when NOTHING command then player moves forward at initial speed") {
    initialise()
    val gameMap = TestHelper.initialiseGameWithNoMapObjects();
    val testGamePlayer1 = TestHelper.getTestGamePlayer1();

    var commandsToProcess = collection.mutable.Map[GamePlayer, util.List[RawCommand]]()

    var player1Commands = List[RawCommand]()
    player1Commands = player1Commands.appended(nothingCommand)
    commandsToProcess.addOne(testGamePlayer1, player1Commands.asJava)

    var player2Commands = List[RawCommand]()
    player2Commands = player2Commands.appended(nothingCommand)
    commandsToProcess.addOne(TestHelper.getTestGamePlayer2(), player2Commands.asJava)

    val javaCommandsToProcess = commandsToProcess.asJava;

    carGameRoundProcessor.processRound(gameMap, javaCommandsToProcess)

    val carGameMap = gameMap.asInstanceOf[CarGameMap];
    val player1Position = carGameMap.getPlayerBlockPosition(testGamePlayer1.asInstanceOf[CarGamePlayer].getGamePlayerId());

    assert(player1Position.getLane() == Config.PLAYER_ONE_START_LANE && player1Position.getBlockNumber() == Config.PLAYER_ONE_START_BLOCK + Config.INITIAL_SPEED);
  }

  test("Given player during the middle of a race when NOTHING command then player moves forward according to speed") {
    initialise()
    val gameMap = TestHelper.initialiseGameWithNoMapObjects();
    val testGamePlayer1 = TestHelper.getTestGamePlayer1();
    val testCarGamePlayer1 = testGamePlayer1.asInstanceOf[CarGamePlayer];
    val carGameMap = gameMap.asInstanceOf[CarGameMap];

    val testGamePlayer1Id = testCarGamePlayer1.getGamePlayerId();
    val newLaneMidRace = 2;
    val newBlockNumberMidRace = 56;
    TestHelper.putPlayerSomewhereOnTheTrack(carGameMap, testGamePlayer1Id, newLaneMidRace, newBlockNumberMidRace);

    val speedBeforeProcessingCommand = testCarGamePlayer1.getSpeed();

    var commandsToProcess = collection.mutable.Map[GamePlayer, util.List[RawCommand]]()

    var player1Commands = List[RawCommand]()
    player1Commands = player1Commands.appended(nothingCommand)
    commandsToProcess.addOne(testGamePlayer1, player1Commands.asJava)

    var player2Commands = List[RawCommand]()
    player2Commands = player2Commands.appended(nothingCommand)
    commandsToProcess.addOne(TestHelper.getTestGamePlayer2(), player2Commands.asJava)

    val javaCommandsToProcess = commandsToProcess.asJava;

    carGameRoundProcessor.processRound(gameMap, javaCommandsToProcess)

    val newPlayer1PositionAfterCommand = carGameMap.getPlayerBlockPosition(testGamePlayer1Id);
    assert(newPlayer1PositionAfterCommand.getLane() == newLaneMidRace && newPlayer1PositionAfterCommand.getBlockNumber() == newBlockNumberMidRace + speedBeforeProcessingCommand);
  }

  test("Given player near finish line when NOTHING command then player stops at finish line") {
    initialise()
    val gameMap = TestHelper.initialiseGameWithNoMapObjects();
    val testGamePlayer1 = TestHelper.getTestGamePlayer1();
    val testCarGamePlayer1 = testGamePlayer1.asInstanceOf[CarGamePlayer];
    val carGameMap = gameMap.asInstanceOf[CarGameMap];

    val testGamePlayer1Id = testCarGamePlayer1.getGamePlayerId();
    val newLaneEndOfRace = 2;
    val newBlockNumberEndOfRace = Config.TRACK_LENGTH - 4;
    TestHelper.putPlayerSomewhereOnTheTrack(carGameMap, testGamePlayer1Id, newLaneEndOfRace, newBlockNumberEndOfRace);

    var commandsToProcess = collection.mutable.Map[GamePlayer, util.List[RawCommand]]()

    var player1Commands = List[RawCommand]()
    player1Commands = player1Commands.appended(nothingCommand)
    commandsToProcess.addOne(testGamePlayer1, player1Commands.asJava)

    var player2Commands = List[RawCommand]()
    player2Commands = player2Commands.appended(nothingCommand)
    commandsToProcess.addOne(TestHelper.getTestGamePlayer2(), player2Commands.asJava)

    val javaCommandsToProcess = commandsToProcess.asJava;

    carGameRoundProcessor.processRound(gameMap, javaCommandsToProcess)

    val newPlayer1PositionAfterCommand = carGameMap.getPlayerBlockPosition(testGamePlayer1Id);
    assert(newPlayer1PositionAfterCommand.getLane() == newLaneEndOfRace && newPlayer1PositionAfterCommand.getBlockNumber() == Config.TRACK_LENGTH);
  }

  test("Given player that is stopped when NOTHING command then player does not move") {
    initialise()
    val gameMap = TestHelper.initialiseGameWithNoMapObjects();
    val testGamePlayer1 = TestHelper.getTestGamePlayer1();
    val testCarGamePlayer1 = testGamePlayer1.asInstanceOf[CarGamePlayer];
    val carGameMap = gameMap.asInstanceOf[CarGameMap];

    val testGamePlayer1Id = testCarGamePlayer1.getGamePlayerId();
    val newLaneMidRace = 2;
    val newBlockNumberMidRace = 56;
    TestHelper.putPlayerSomewhereOnTheTrack(carGameMap, testGamePlayer1Id, newLaneMidRace, newBlockNumberMidRace);

    testCarGamePlayer1.speed = 0; //stop player

    var commandsToProcess = collection.mutable.Map[GamePlayer, util.List[RawCommand]]()

    var player1Commands = List[RawCommand]()
    player1Commands = player1Commands.appended(nothingCommand)
    commandsToProcess.addOne(testGamePlayer1, player1Commands.asJava)

    var player2Commands = List[RawCommand]()
    player2Commands = player2Commands.appended(nothingCommand)
    commandsToProcess.addOne(TestHelper.getTestGamePlayer2(), player2Commands.asJava)

    val javaCommandsToProcess = commandsToProcess.asJava;

    carGameRoundProcessor.processRound(gameMap, javaCommandsToProcess)

    val newPlayer1PositionAfterCommand = carGameMap.getPlayerBlockPosition(testGamePlayer1Id);
    assert(newPlayer1PositionAfterCommand.getLane() == newLaneMidRace && newPlayer1PositionAfterCommand.getBlockNumber() == newBlockNumberMidRace);
  }

  test("Given player that is boosting when NOTHING command then player moves at boost speed") {
    initialise()
    val gameMap = TestHelper.initialiseGameWithNoMapObjects();
    val testGamePlayer1 = TestHelper.getTestGamePlayer1();
    val testCarGamePlayer1 = testGamePlayer1.asInstanceOf[CarGamePlayer];
    val carGameMap = gameMap.asInstanceOf[CarGameMap];

    testCarGamePlayer1.useBoost();

    val testGamePlayer1Id = testCarGamePlayer1.getGamePlayerId();
    val newLaneMidRace = 2;
    val newBlockNumberMidRace = 56;
    TestHelper.putPlayerSomewhereOnTheTrack(carGameMap, testGamePlayer1Id, newLaneMidRace, newBlockNumberMidRace);

    var commandsToProcess = collection.mutable.Map[GamePlayer, util.List[RawCommand]]()

    var player1Commands = List[RawCommand]()
    player1Commands = player1Commands.appended(nothingCommand)
    commandsToProcess.addOne(testGamePlayer1, player1Commands.asJava)

    var player2Commands = List[RawCommand]()
    player2Commands = player2Commands.appended(nothingCommand)
    commandsToProcess.addOne(TestHelper.getTestGamePlayer2(), player2Commands.asJava)

    val javaCommandsToProcess = commandsToProcess.asJava;

    carGameRoundProcessor.processRound(gameMap, javaCommandsToProcess)

    val newPlayer1PositionAfterCommand = carGameMap.getPlayerBlockPosition(testGamePlayer1Id);
    assert(newPlayer1PositionAfterCommand.getLane() == newLaneMidRace && newPlayer1PositionAfterCommand.getBlockNumber() == newBlockNumberMidRace + Config.BOOST_SPEED);
    assert(testCarGamePlayer1.isBoosting() == true);
  }

}
