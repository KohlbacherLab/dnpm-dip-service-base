package de.dnpm.dip.service.mvh


import de.dnpm.dip.model.{
  Id,
  Patient,
  PatientRecord,
}


trait MVHService[F[_],Env,T <: PatientRecord]
{
  import MVHService._

  val useCase: UseCase.Value

  def !(cmd: Command[T])(
    implicit env: Env
  ): F[Either[Error,Outcome]]

  def ?(filter: SubmissionReport.Filter)(
    implicit env: Env
  ): F[Iterable[SubmissionReport]]

}


object MVHService
{
  sealed trait Command[+T]

  final case class Process[T <: PatientRecord](
    record: MVHPatientRecord[T],
    qcPassed: Boolean
  )
  extends Command[T]

  final case class Delete(id: Id[Patient]) extends Command[Nothing]

  sealed trait Outcome
  final case object Saved extends Outcome
  final case object Deleted extends Outcome

  sealed trait Error
  final case class GenericError(msg: String) extends Error
}

