package fer.api

import com.typesafe.scalalogging.Logger
import fer.calc.{CalcInputs, HeatLoadCalculator, ReportRenderer}
import fer.entity.ColdRoomSpec
import io.swagger.v3.oas.annotations.media.Schema
import upickle.default.{read, write, ReadWriter}

/** Тело запроса на расчет теплопритоков. */
final case class CalcRequest(
    @Schema(description = "Технические условия камеры") spec: ColdRoomSpec,
    @Schema(description = "Справочные параметры расчета") inputs: CalcInputs
) derives ReadWriter

/** HTTP-контроллер расчета теплопритоков (порт 8080). */
object HeatLoadController extends cask.MainRoutes:
  private val logger = Logger(getClass)

  override def port: Int = 8080

  override def main(args: Array[String]): Unit =
    logger.info(s"Запуск сервера на http://$host:$port")
    super.main(args)

  /** Выполняет расчет по JSON [[CalcRequest]] и возвращает отчет [[fer.calc.HeatLoadReport]]. */
  @cask.post("/api/heat-load/calculate")
  def calculate(request: cask.Request): cask.Response[String] =
    try
      val req = read[CalcRequest](request.text())
      logger.info(s"Расчет камеры №${req.spec.roomNumber} «${req.spec.name}»")
      val report = HeatLoadCalculator.calculate(req.spec, req.inputs)
      logger.info(f"Камера №${report.roomNumber}: Q = ${report.totals.totalHeatGainW}%.0f Вт, " +
        f"расчетная мощность ${report.totals.designCapacityW}%.0f Вт")
      logger.debug(s"Полный отчет:\n${ReportRenderer.render(report)}")
      json(write(report, indent = 2))
    catch
      case e: Exception =>
        logger.warn(s"Некорректный запрос: ${e.getMessage}")
        json(write(ujson.Obj("error" -> s"Некорректный запрос: ${e.getMessage}")), statusCode = 400)

  private def json(body: String, statusCode: Int = 200): cask.Response[String] =
    cask.Response(
      body,
      statusCode = statusCode,
      headers = Seq("Content-Type" -> "application/json; charset=utf-8")
    )

  initialize()
