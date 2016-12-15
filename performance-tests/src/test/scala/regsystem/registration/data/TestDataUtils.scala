package regsystem.registration.data

import com.typesafe.config.ConfigFactory

import scala.util.Random

object TestDataUtils {

  private[this] val config = ConfigFactory.load()

  private val GROUP_COUNT = config.getInt("scenario.group_count")
  private val GROUP_CAPACITY = config.getInt("scenario.group_capacity")
  private val USER_COUNT = config.getInt("scenario.user_count")

  private val colour: Array[String] =
    Array("Red", "Orange", "Yellow", "Green", "Cyan", "Blue", "Violet", "Black", "White")

  private val code: Array[String] =
    Array("Alpha", "Bravo", "Charlie", "Delta", "Echo", "Foxtrot", "Golf", "India", "Juliet",
      "Kilo", "Mike", "November", "Oscar", "Papa", "Quebec", "Romeo", "Sierra", "Tango",
      "Uniform", "Victor", "Whiskey", "X-ray", "Yankee", "Zulu")

  private val names: Array[String] =
    Array("Ann", "Andy", "Barbara", "Bob", "Christine", "Charles", "Danielle", "Dave", "Esther", "Elijah", "Fiona", "Fred", "Gwen", "Gabriel",
      "Hannah", "Henry", "Irene", "Ian", "Jane", "James", "Kate", "Karl", "Lucy", "Luke", "Martha", "Matt", "Nicolle", "Neal",
      "Olga", "Oliver", "Penny", "Pedro", "Quinn", "Quentin", "Rachel", "Ray", "Sarah", "Sean", "Tanya", "Tim", "Uma", "Ulysses",
      "Veronica", "Vinny", "Whitney", "Wiliam", "Xena", "Xavier", "Ysabella", "Yasir", "Zoe", "Zac")

  private def randomGroupName = colour(Random.nextInt(colour.length)) + " " + code(Random.nextInt(code.length)) + " " + Random.nextInt(99)

  private def randomUserName = names(Random.nextInt(names.length)) + " " + getRandomCapitalLetter + "."

  private def getRandomCapitalLetter = (Random.nextInt(90 - 65) + 65).toChar

  def randomGroup: JsonGroup = JsonGroup(
    groupIdFrom(randomGroupName),
    randomGroupName,
    GROUP_CAPACITY
  )

  private def groupIdFrom(groupName: String) = groupName.toLowerCase.replace(" ", "-")

  def randomUser: JsonUser = JsonUser(
    groups(Random.nextInt(groups.length)).id,
    randomUserName
  )

  val groups: List[JsonGroup] = List.fill(GROUP_COUNT)(randomGroup)

  val users: List[JsonUser] = List.fill(USER_COUNT)(randomUser)

}

