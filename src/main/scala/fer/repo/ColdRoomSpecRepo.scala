package fer.repo

import fer.entity.ColdRoomSpec
import scalikejdbc.*
import upickle.default.write

import java.time.OffsetDateTime

/** Хранилище спецификаций камер: [[ColdRoomSpec]] сериализуется в JSON целиком. */
object ColdRoomSpecRepo:

  /** Сохраняет спецификацию и возвращает идентификатор записи. */
  def save(spec: ColdRoomSpec): Long =
    DB.autoCommit { implicit session =>
      sql"""INSERT INTO cold_room_spec (room_number, name, spec_json, created_at)
            VALUES (${spec.roomNumber}, ${spec.name}, ${write(spec)}, ${OffsetDateTime.now.toString})"""
        .updateAndReturnGeneratedKey.apply()
    }
