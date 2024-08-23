package de.dnpm.dip.service

package object mvh {

import java.time.LocalDateTime
import scala.util.{
  Left,
  Right
}
import cats.Monad
import de.dnpm.dip.util.Logging
import de.dnpm.dip.coding.Coding
import de.dnpm.dip.model.{
  Id,
  Period,
  Patient,
  PatientRecord,
  Site,
  TransferTAN
}
import de.dnpm.dip.model.NGSReport.SequencingType
import play.api.libs.json.{
  Json,
  JsObject,
  Format,
  Reads,
  Writes,
  OWrites
}

//-----------------------------------------------------------------------------

object SubmissionType extends Enumeration
{
  val Initial    = Value("initial")
  val Addition   = Value("addition")
  val Correction = Value("correction")
  val Other      = Value("other")
  
  implicit val format: Format[Value] =
    Json.formatEnum(this)
}


//-----------------------------------------------------------------------------

final case class Metadata
(
  transferTAN: Id[TransferTAN],
  submissionType: SubmissionType.Value,
  consent: Consent
)

object Metadata
{
  implicit val readsMetadata: Reads[Metadata] =
    Json.reads[Metadata]

  implicit val writesMetadata: Writes[Metadata] =
    Json.writes[Metadata]
}

//-----------------------------------------------------------------------------

final case class MVHPatientRecord[T <: PatientRecord]
(
  record: T,
  meta: Metadata
)

object MVHPatientRecord
{

  import play.api.libs.json.JsPath
  import play.api.libs.functional.syntax._

  implicit def reads[T <: PatientRecord: Reads]: Reads[MVHPatientRecord[T]] =
    (
      JsPath.read[T] and
      (JsPath \ "meta").read[Metadata]
    )(
      MVHPatientRecord(_,_)
    )

  implicit def writes[T <: PatientRecord: Writes]: OWrites[MVHPatientRecord[T]] =
    (
      JsPath.write[T] and
      (JsPath \ "meta").write[Metadata]
    )(
      unlift(MVHPatientRecord.unapply[T](_))
    )

/*
  import json.{
    Json => Js,
    Schema
  }

  implicit val metadataSchema: Schema[Metadata] =
    Js.schema[Metadata]

  implicit def schema[T <: PatientRecord](
    implicit schema: Schema[T]
  ): Schema[MVHPatientRecord[T]] =
    schema match {
      case obj: Schema.`object`[T] =>
        obj.withField(
          "meta",
          Schema[Metadata],
          true
        )
        .asInstanceOf[Schema[MVHPatientRecord[T]]]
      case _ => ???
    }
*/    
}

//-----------------------------------------------------------------------------

object UseCase extends Enumeration
{
  val MTB,RD = Value

  implicit val format: Format[Value] =
    Json.formatEnum(this)
}


}