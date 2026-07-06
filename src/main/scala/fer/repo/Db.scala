package fer.repo

import scalikejdbc.*

import java.nio.file.{Files, Paths}

/** Подключение к SQLite и инициализация схемы БД. */
object Db:
  /** Открывает пул соединений к файлу БД и создает таблицы, если их нет. */
  def init(dbFile: String = "data/mvp-fer.db"): Unit =
    val dir = Paths.get(dbFile).getParent
    if dir != null then Files.createDirectories(dir)
    ConnectionPool.singleton(s"jdbc:sqlite:$dbFile", "", "")
    DB.autoCommit { implicit session =>
      sql"""CREATE TABLE IF NOT EXISTS cold_room_spec (
              id          INTEGER PRIMARY KEY AUTOINCREMENT,
              room_number TEXT NOT NULL,
              name        TEXT NOT NULL,
              spec_json   TEXT NOT NULL,
              created_at  TEXT NOT NULL
            )""".execute.apply()
    }
